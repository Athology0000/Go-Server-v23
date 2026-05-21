package org.cobalt.loader.bootstrap

import org.cobalt.loader.config.LoaderConfig
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.time.Duration
import kotlin.io.path.exists

object BootstrapModuleDownloader {
    private const val MAX_MODULE_BYTES = 25L * 1024L * 1024L

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(8))
        .followRedirects(HttpClient.Redirect.NEVER)
        .build()

    fun downloadAll(modules: List<BootstrapModule>, sessionToken: String): List<DownloadedBootstrapModule> {
        val cacheRoot = LoaderConfig.cacheDir.toAbsolutePath().normalize()
        Files.createDirectories(cacheRoot)

        return modules.map { module ->
            validateModule(module)
            val downloadUri = resolveDownloadUri(module)

            if (module.delivery.equals("class", ignoreCase = true) || module.delivery.equals("bytecode", ignoreCase = true)) {
                val bytes = downloadBytes(module, sessionToken, downloadUri)
                val actualHash = sha256(bytes)
                require(actualHash.equals(module.sha256, ignoreCase = true)) {
                    "SHA-256 mismatch for ${module.id}"
                }
                val className = ClassFileInspector.className(bytes)
                require(className == module.entrypoint) {
                    "Bytecode class $className does not match entrypoint ${module.entrypoint}"
                }
                return@map DownloadedBootstrapModule.Bytecode(
                    module = module,
                    className = className,
                    bytes = bytes
                )
            }

            val out = cacheRoot
                .resolve("${module.id}-${module.version}.jar")
                .normalize()

            require(out.startsWith(cacheRoot)) {
                "Blocked module cache path escape"
            }

            if (out.exists() && sha256(out).equals(module.sha256, ignoreCase = true)) {
                return@map DownloadedBootstrapModule.Jar(module, out)
            }

            val bytes = downloadBytes(module, sessionToken, downloadUri)
            val actualHash = sha256(bytes)
            require(actualHash.equals(module.sha256, ignoreCase = true)) {
                "SHA-256 mismatch for ${module.id}"
            }
            writeVerifiedModule(out, bytes)

            DownloadedBootstrapModule.Jar(module, out)
        }
    }

    private fun validateModule(module: BootstrapModule) {
        require(Regex("^[a-zA-Z0-9_-]{1,64}$").matches(module.id)) {
            "Unsafe module id: ${module.id}"
        }
        require(Regex("^[a-zA-Z0-9_.-]{1,32}$").matches(module.version)) {
            "Unsafe module version: ${module.version}"
        }
        require(module.delivery.equals("jar", ignoreCase = true) ||
            module.delivery.equals("class", ignoreCase = true) ||
            module.delivery.equals("bytecode", ignoreCase = true)) {
            "Unsupported module delivery: ${module.delivery}"
        }
        require(Regex("^[a-zA-Z_$][a-zA-Z0-9_$]*(\\.[a-zA-Z_$][a-zA-Z0-9_$]*)+$").matches(module.entrypoint)) {
            "Unsafe module entrypoint: ${module.entrypoint}"
        }
        require(Regex("^[a-fA-F0-9]{64}$").matches(module.sha256)) {
            "Invalid SHA-256 for ${module.id}"
        }

        validateModuleUri(resolveDownloadUri(module))
    }

    private fun resolveDownloadUri(module: BootstrapModule): URI {
        val baseUri = URI.create(LoaderConfig.moduleBaseUrl.ensureTrailingSlash())
        validateModuleUri(baseUri)

        val moduleUrl = module.url.trim()
        val relativeOrAbsolute = if (moduleUrl.isBlank()) {
            URI.create(defaultArtifactName(module))
        } else {
            URI.create(moduleUrl)
        }

        val resolved = if (relativeOrAbsolute.isAbsolute) {
            relativeOrAbsolute
        } else {
            baseUri.resolve(relativeOrAbsolute)
        }

        validateModuleUri(resolved)
        validateModuleHostAndPath(baseUri, resolved, module)
        return resolved
    }

    private fun defaultArtifactName(module: BootstrapModule): String {
        return if (module.delivery.equals("class", ignoreCase = true) || module.delivery.equals("bytecode", ignoreCase = true)) {
            "${module.id}-${module.version}.class"
        } else {
            "${module.id}-${module.version}.jar"
        }
    }

    private fun downloadBytes(module: BootstrapModule, sessionToken: String, downloadUri: URI): ByteArray {
        val request = HttpRequest.newBuilder()
            .uri(downloadUri)
            .timeout(Duration.ofSeconds(25))
            .header("Authorization", "Bearer $sessionToken")
            .header("Accept", "application/java-archive, application/octet-stream")
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        require(response.statusCode() == 200) {
            "Failed downloading ${module.id}: HTTP ${response.statusCode()}"
        }

        val bytes = response.body()
        require(bytes.size.toLong() in 1..MAX_MODULE_BYTES) {
            "Module ${module.id} has invalid size ${bytes.size}"
        }
        return bytes
    }

    private fun validateModuleUri(uri: URI) {
        val isLocalhost = uri.host == "localhost" || uri.host == "127.0.0.1"
        val validScheme = uri.scheme == "https" || (LoaderConfig.allowLocalhostHttp && isLocalhost && uri.scheme == "http")
        require(validScheme) { "Module URL must use HTTPS, except localhost in dev mode" }
        require(uri.userInfo == null) { "Module URL must not contain user info" }
        require(uri.query == null && uri.fragment == null) { "Module URL must not contain query or fragment" }
    }

    private fun validateModuleHostAndPath(baseUri: URI, resolvedUri: URI, module: BootstrapModule) {
        if (LoaderConfig.allowExternalModuleUrls) {
            return
        }

        require(resolvedUri.scheme.equals(baseUri.scheme, ignoreCase = true) && resolvedUri.host.equals(baseUri.host, ignoreCase = true)) {
            "Module URL host must match cobalt.module.base.url"
        }

        val basePath = baseUri.normalize().path.ensureTrailingSlash()
        val modulePath = resolvedUri.normalize().path
        require(modulePath.startsWith(basePath)) {
            "Module URL must stay under cobalt.module.base.url"
        }

        val expectedExtension = if (module.delivery.equals("jar", ignoreCase = true)) ".jar" else ".class"
        require(modulePath.endsWith(expectedExtension)) {
            "Module URL for ${module.id} must end with $expectedExtension"
        }
    }

    private fun writeVerifiedModule(out: Path, bytes: ByteArray) {
        val temp = Files.createTempFile(out.parent, "${out.fileName}", ".tmp")
        try {
            Files.write(temp, bytes)
            runCatching {
                Files.move(temp, out, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            }.getOrElse {
                Files.move(temp, out, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temp)
        }
    }

    private fun String.ensureTrailingSlash(): String = if (endsWith("/")) this else "$this/"

    fun sha256(path: Path): String {
        val digest = MessageDigest.getInstance("SHA-256")
        Files.newInputStream(path).use { input ->
            val buffer = ByteArray(8192)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
