package org.cobalt.loader.bootstrap

import org.cobalt.api.runtime.CobaltModuleContext
import org.cobalt.api.runtime.CobaltModuleRegistrar
import org.cobalt.api.runtime.CobaltRuntimeLogger
import org.cobalt.api.runtime.CobaltRuntimeModule
import org.cobalt.loader.runtime.SimpleRuntimeRegistrar
import org.cobalt.loader.util.LoaderLog
import java.net.URLClassLoader

object BootstrapModuleLoader {
    private val loaded = linkedMapOf<String, CobaltRuntimeModule>()
    private val registrar: CobaltModuleRegistrar = SimpleRuntimeRegistrar
    private val runtimeLogger: CobaltRuntimeLogger = LoaderRuntimeLogger

    fun loadModules(manifest: BootstrapManifest, modules: List<DownloadedBootstrapModule>) {
        for (artifact in modules) {
            val module = artifact.module
            if (loaded.containsKey(module.id)) {
                LoaderLog.warn("Skipping already loaded module ${module.id}")
                continue
            }

            val clazz = loadEntrypointClass(artifact)
            val instance = clazz.getDeclaredConstructor().newInstance()

            require(instance is CobaltRuntimeModule) {
                "Entrypoint ${module.entrypoint} must implement CobaltRuntimeModule"
            }

            instance.onLoad(
                CobaltModuleContext(
                    userId = manifest.userId,
                    minecraftUsername = manifest.minecraftUsername,
                    moduleId = module.id,
                    moduleVersion = module.version,
                    requiredRole = module.requiredRole,
                    registrar = registrar,
                    logger = runtimeLogger
                )
            )

            loaded[module.id] = instance
            LoaderLog.info("Loaded runtime module ${module.id} ${module.version} from ${module.delivery}")
        }
    }

    private fun loadEntrypointClass(artifact: DownloadedBootstrapModule): Class<*> {
        return when (artifact) {
            is DownloadedBootstrapModule.Jar -> {
                val classLoader = URLClassLoader(
                    arrayOf(artifact.path.toUri().toURL()),
                    BootstrapModuleLoader::class.java.classLoader
                )
                Class.forName(artifact.module.entrypoint, true, classLoader)
            }
            is DownloadedBootstrapModule.Bytecode -> {
                val classLoader = BytecodeModuleClassLoader(
                    artifact.className,
                    artifact.bytes,
                    BootstrapModuleLoader::class.java.classLoader
                )
                Class.forName(artifact.className, true, classLoader)
            }
        }
    }

    fun unloadAll() {
        loaded.entries.reversed().forEach { (id, module) ->
            runCatching { module.onUnload() }
                .onFailure { LoaderLog.error("Failed unloading runtime module $id", it) }
        }
        loaded.clear()
    }

    private object LoaderRuntimeLogger : CobaltRuntimeLogger {
        override fun info(message: String) = LoaderLog.info(message)
        override fun warn(message: String) = LoaderLog.warn(message)
        override fun error(message: String, throwable: Throwable?) = LoaderLog.error(message, throwable)
    }

    private class BytecodeModuleClassLoader(
        private val className: String,
        private val classBytes: ByteArray,
        parent: ClassLoader
    ) : ClassLoader(parent) {
        override fun findClass(name: String): Class<*> {
            if (name == className) {
                return defineClass(name, classBytes, 0, classBytes.size)
            }
            return super.findClass(name)
        }
    }
}
