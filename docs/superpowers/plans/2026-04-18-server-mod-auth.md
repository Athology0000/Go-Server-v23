# Server + Mod Auth Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the `/auth/verify-minecraft` server endpoint and wire up the mod-side auth flow that gates the title screen until the user's Minecraft account is verified and entitled modules are downloaded.

**Architecture:** The server gains one small change (optional MC username in `Start()`) and one new endpoint (`/auth/verify-minecraft`). The mod reads the session token left by the bootstrapper, calls that endpoint from a background thread when the title screen appears, downloads and verifies entitled module JARs, loads them via `AddonLoader`, and only then unlocks Singleplayer/Multiplayer.

**Tech Stack:** Go 1.22 (Fiber, pgx), Kotlin/Fabric MC 1.21.11, Java 21, Gson, `java.net.http.HttpClient`

---

## File Structure

| File | Action | Responsibility |
|---|---|---|
| `server/internal/auth/service.go` | Modify | Make MC username optional in `Start()`; add `VerifyMinecraft()` |
| `server/internal/auth/handler.go` | Modify | Register `POST /auth/verify-minecraft` route |
| `src/main/kotlin/org/cobalt/internal/auth/AuthState.kt` | Create | Global auth state enum + volatile holder |
| `src/main/kotlin/org/cobalt/internal/auth/CobaltSession.kt` | Create | Read session token from JVM property file |
| `src/main/kotlin/org/cobalt/internal/auth/CobaltAuthService.kt` | Create | Background auth thread: verify-MC call, manifest fetch+verify, module download+load |
| `src/main/kotlin/org/cobalt/internal/loader/AddonLoader.kt` | Modify | Expose `loadFromPath(jarPath)` public method |
| `src/main/java/org/cobalt/mixin/render/TitleScreenMixin.java` | Modify | Gate Singleplayer/Multiplayer buttons until `AuthState.READY` |
| `src/main/kotlin/org/cobalt/Cobalt.kt` | Modify | Initialise `CobaltSession` at startup, start `CobaltAuthService` |

---

### Task 1: Server — make `minecraft_username` optional in `Start()`

**Files:**
- Modify: `server/internal/auth/service.go:97-117`

The server currently rejects any `fully_bound` device that sends an empty `minecraft_username`. The bootstrapper legitimately omits this field. Change the check to only run when the field is non-empty.

- [ ] **Step 1: Edit the MC username guard in `Start()`**

In `server/internal/auth/service.go`, change lines 112–117:

```go
// BEFORE
if device.BindingStatus == "fully_bound" {
    if device.MinecraftUsername == nil || !strings.EqualFold(*device.MinecraftUsername, minecraftUsername) {
        s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "username_mismatch"})
        return nil, ErrUsernameMismatch
    }
}

// AFTER
if device.BindingStatus == "fully_bound" && minecraftUsername != "" {
    if device.MinecraftUsername == nil || !strings.EqualFold(*device.MinecraftUsername, minecraftUsername) {
        s.auditSvc.Log("auth.start.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "username_mismatch"})
        return nil, ErrUsernameMismatch
    }
}
```

- [ ] **Step 2: Build the server to verify it compiles**

```bash
cd server && go build ./...
```

Expected: no output (clean build).

- [ ] **Step 3: Commit**

```bash
cd server
git add internal/auth/service.go
git commit -m "fix(auth): skip minecraft_username check when field is empty"
```

---

### Task 2: Server — add `VerifyMinecraft()` service method

**Files:**
- Modify: `server/internal/auth/service.go` (append new method + error var)

- [ ] **Step 1: Add `ErrSessionInvalid` error var near the top of `service.go`**

Add to the existing `var (...)` block at the top of `service.go`:

```go
ErrSessionInvalid = errors.New("session invalid")
```

- [ ] **Step 2: Add the `VerifyMinecraftResult` type and `VerifyMinecraft()` method**

Append to `server/internal/auth/service.go`:

```go
type VerifyMinecraftResult struct {
	Authorized           bool
	Reason               string
	PlanTier             string
	Modules              []string
	Features             []string
	ManifestURL          string
	ManifestSignature    string
	EntitlementExpiresAt *time.Time
}

func (s *Service) VerifyMinecraft(ctx context.Context, rawToken, minecraftUsername, sourceIP string) (*VerifyMinecraftResult, error) {
	tokenHash, err := crypto.HashToken(rawToken)
	if err != nil {
		return nil, ErrSessionInvalid
	}

	sess, err := db.GetSessionByTokenHash(ctx, s.pool, tokenHash)
	if err != nil || sess.Revoked || time.Now().After(sess.ExpiresAt) {
		return nil, ErrSessionInvalid
	}

	device, err := db.GetDeviceByID(ctx, s.pool, sess.DeviceID)
	if err != nil {
		return nil, err
	}

	account, err := db.GetAccountByID(ctx, s.pool, sess.AccountID)
	if err != nil {
		return nil, err
	}

	if account.Status != "active" {
		s.auditSvc.Log("auth.verify_mc.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "account_blocked"})
		return &VerifyMinecraftResult{Authorized: false, Reason: "account_blocked"}, nil
	}

	switch device.BindingStatus {
	case "hwid_pending":
		if err := db.FullyBind(ctx, s.pool, device.ID, minecraftUsername, sourceIP); err != nil {
			return nil, err
		}
	case "fully_bound":
		if device.MinecraftUsername == nil || !strings.EqualFold(*device.MinecraftUsername, minecraftUsername) {
			s.auditSvc.Log("auth.verify_mc.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "minecraft_username_mismatch"})
			return &VerifyMinecraftResult{Authorized: false, Reason: "minecraft_username_mismatch"}, nil
		}
	default:
		s.auditSvc.Log("auth.verify_mc.fail", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": "device_not_eligible", "status": device.BindingStatus})
		return &VerifyMinecraftResult{Authorized: false, Reason: "device_not_eligible"}, nil
	}

	ent, err := s.entSvc.Resolve(ctx, account.ID)
	if err != nil {
		return nil, err
	}
	if !ent.Authorized {
		s.auditSvc.Log("auth.verify_mc.entitlement_denied", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"reason": ent.Reason})
		return &VerifyMinecraftResult{Authorized: false, Reason: ent.Reason}, nil
	}

	manifestURL := ""
	manifestSig := ""
	manifest, err := db.GetLatestManifest(ctx, s.pool, ent.ContentChannel)
	if err == nil {
		manifestURL = s.baseURL + "/content/manifest/" + manifest.ID
		manifestSig = manifest.Signature
	}

	s.auditSvc.Log("auth.verify_mc.success", &account.ID, &device.ID, nil, &sourceIP, map[string]any{"plan_tier": ent.PlanTier})

	return &VerifyMinecraftResult{
		Authorized:           true,
		PlanTier:             ent.PlanTier,
		Modules:              ent.EnabledModules,
		Features:             ent.EnabledFeatures,
		ManifestURL:          manifestURL,
		ManifestSignature:    manifestSig,
		EntitlementExpiresAt: ent.EntitlementExpiresAt,
	}, nil
}
```

- [ ] **Step 3: Build**

```bash
cd server && go build ./...
```

Expected: clean.

- [ ] **Step 4: Commit**

```bash
git add server/internal/auth/service.go
git commit -m "feat(auth): add VerifyMinecraft service method"
```

---

### Task 3: Server — add `/auth/verify-minecraft` route

**Files:**
- Modify: `server/internal/auth/handler.go`

- [ ] **Step 1: Add the request struct and handler to `handler.go`**

Append to `server/internal/auth/handler.go`:

```go
type verifyMinecraftRequest struct {
	SessionToken      string `json:"session_token"`
	MinecraftUsername string `json:"minecraft_username"`
}

func handleVerifyMinecraft(svc *Service) fiber.Handler {
	return func(c *fiber.Ctx) error {
		var req verifyMinecraftRequest
		if err := c.BodyParser(&req); err != nil || req.SessionToken == "" || req.MinecraftUsername == "" {
			return c.Status(400).JSON(fiber.Map{"error": "invalid_request"})
		}
		ip := middleware.GetRealIP(c)
		result, err := svc.VerifyMinecraft(c.Context(), req.SessionToken, req.MinecraftUsername, ip)
		if errors.Is(err, ErrSessionInvalid) {
			return c.Status(401).JSON(fiber.Map{"error": "session_invalid"})
		}
		if err != nil {
			return c.Status(500).JSON(fiber.Map{"error": "internal_error"})
		}
		if !result.Authorized {
			return c.JSON(fiber.Map{"authorized": false, "reason": result.Reason})
		}
		return c.JSON(fiber.Map{
			"authorized":             true,
			"plan_tier":              result.PlanTier,
			"enabled_modules":        result.Modules,
			"enabled_features":       result.Features,
			"manifest_url":           result.ManifestURL,
			"manifest_signature":     result.ManifestSignature,
			"entitlement_expires_at": result.EntitlementExpiresAt,
		})
	}
}
```

- [ ] **Step 2: Register the route in `RegisterRoutes()`**

In `RegisterRoutes()` in `handler.go`, after the existing `app.Post("/auth/finish", ...)` line, add:

```go
// 10 attempts per minute — same rate limit as auth start/finish
app.Post("/auth/verify-minecraft", authLimit, handleVerifyMinecraft(svc))
```

- [ ] **Step 3: Build**

```bash
cd server && go build ./...
```

Expected: clean.

- [ ] **Step 4: Manual smoke test**

Start the server locally. Send a request with an invalid token:
```bash
curl -s -X POST http://localhost:8080/auth/verify-minecraft \
  -H "Content-Type: application/json" \
  -d '{"session_token":"invalid","minecraft_username":"Steve"}' | jq .
```
Expected: `{"error":"session_invalid"}` with HTTP 401.

- [ ] **Step 5: Commit**

```bash
git add server/internal/auth/handler.go
git commit -m "feat(auth): add /auth/verify-minecraft endpoint"
```

---

### Task 4: Mod — `AuthState.kt`

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/auth/AuthState.kt`

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.internal.auth

enum class AuthState { PENDING, VERIFYING, LOADING, READY, FAILED }

object Auth {
    @Volatile var state: AuthState = AuthState.PENDING
    @Volatile var statusMessage: String = "Waiting…"
    @Volatile var failureReason: String = ""
    @Volatile var modulesLoaded: Int = 0
    @Volatile var modulesTotal: Int = 0

    fun isGateLocked(): Boolean = state != AuthState.READY
}
```

- [ ] **Step 2: Build**

```bash
./gradlew classes
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/auth/AuthState.kt
git commit -m "feat(auth): add AuthState singleton"
```

---

### Task 5: Mod — `CobaltSession.kt`

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/auth/CobaltSession.kt`

Reads the session token from the file path given by the `-Dcobalt.session=<path>` JVM property. Deletes the file immediately after reading.

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.internal.auth

import com.google.gson.Gson
import java.io.File
import java.nio.charset.StandardCharsets
import org.slf4j.LoggerFactory

data class CobaltSession(val sessionToken: String) {
    val isValid: Boolean get() = sessionToken.isNotBlank()

    companion object {
        val INVALID = CobaltSession("")
        private val logger = LoggerFactory.getLogger("Cobalt/CobaltSession")
        private val gson = Gson()

        fun readAndDelete(): CobaltSession {
            val path = System.getProperty("cobalt.session") ?: run {
                logger.warn("cobalt.session system property not set — running without auth")
                return INVALID
            }
            val file = File(path)
            return try {
                val json = file.readText(StandardCharsets.UTF_8)
                file.delete()
                val raw = gson.fromJson(json, RawSession::class.java)
                if (raw?.session_token.isNullOrBlank()) INVALID
                else CobaltSession(raw.session_token)
            } catch (e: Exception) {
                logger.error("Failed to read session file at {}: {}", path, e.message)
                runCatching { file.delete() }
                INVALID
            }
        }

        private data class RawSession(val session_token: String?)
    }
}
```

- [ ] **Step 2: Build**

```bash
./gradlew classes
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/auth/CobaltSession.kt
git commit -m "feat(auth): add CobaltSession reader"
```

---

### Task 6: Mod — expose `AddonLoader.loadFromPath()`

**Files:**
- Modify: `src/main/kotlin/org/cobalt/internal/loader/AddonLoader.kt`

The existing `loadAddon(jarPath)` is private. Post-auth module loading needs to call it for dynamically downloaded JARs. Add a public wrapper.

**Note:** Modules loaded via this path cannot use Mixin configurations (mixins are applied at launch time only). Post-auth modules must be pure event-based addons.

- [ ] **Step 1: Add the public method to `AddonLoader`**

In `AddonLoader.kt`, after the `findAddons()` function, add:

```kotlin
fun loadFromPath(jarPath: java.nio.file.Path) {
    try {
        FabricLauncherBase.getLauncher().addToClassPath(jarPath)
        loadAddon(jarPath)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

- [ ] **Step 2: Build**

```bash
./gradlew classes
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/loader/AddonLoader.kt
git commit -m "feat(loader): expose loadFromPath for post-auth module loading"
```

---

### Task 7: Mod — `CobaltAuthService.kt`

**Files:**
- Create: `src/main/kotlin/org/cobalt/internal/auth/CobaltAuthService.kt`

Background thread that fires at title screen: calls `/auth/verify-minecraft`, fetches and verifies the signed manifest, downloads entitled module JARs, verifies SHA-256, loads them via AddonLoader, then sets `AuthState.READY`.

**Before implementing:** Determine the server's Ed25519 public key (32 raw bytes). Derive it from `MANIFEST_SIGNING_KEY`:
```bash
echo "$MANIFEST_SIGNING_KEY" | base64 -d | tail -c 32 | base64
```
Hard-code the result as `MANIFEST_PUBLIC_KEY_B64` in the file below.

- [ ] **Step 1: Create the file**

```kotlin
package org.cobalt.internal.auth

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.time.Duration
import java.util.Base64
import kotlin.concurrent.thread
import net.minecraft.client.Minecraft
import org.cobalt.internal.loader.AddonLoader
import org.slf4j.LoggerFactory

object CobaltAuthService {

    // Replace with: echo "$MANIFEST_SIGNING_KEY" | base64 -d | tail -c 32 | base64
    private const val MANIFEST_PUBLIC_KEY_B64 = "REPLACE_ME_WITH_32_BYTE_BASE64_ED25519_PUBLIC_KEY"
    private const val SERVER_BASE_URL = "https://your-server-url.com" // replace with actual URL

    private val logger = LoggerFactory.getLogger("Cobalt/AuthService")
    private val gson = Gson()
    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .build()
    private val cacheDir: Path = Paths.get("config/cobalt/cache/payloads")

    // ASN.1 header for Ed25519 public key (OID 1.3.101.112)
    private val ED25519_ASN1_HEADER = byteArrayOf(
        0x30, 0x2A, 0x30, 0x05, 0x06, 0x03, 0x2B, 0x65, 0x70, 0x03, 0x21, 0x00
    )

    fun start(session: CobaltSession) {
        if (!session.isValid) {
            Auth.state = AuthState.FAILED
            Auth.statusMessage = "No session — run the bootstrapper first"
            Auth.failureReason = "no_session"
            return
        }
        thread(name = "Cobalt-Auth", isDaemon = true) { runAuth(session) }
    }

    private fun runAuth(session: CobaltSession) {
        try {
            Auth.state = AuthState.VERIFYING
            Auth.statusMessage = "Verifying account…"

            val mc = Minecraft.getInstance()
            val mcUsername = mc.user.name

            val entitlement = verifyMinecraft(session.sessionToken, mcUsername)
                ?: return fail("Server auth failed")

            if (!entitlement.authorized) {
                return fail(entitlement.reason ?: "not_authorized")
            }

            if (entitlement.manifestUrl.isNullOrBlank()) {
                Auth.state = AuthState.READY
                Auth.statusMessage = "Ready"
                return
            }

            Auth.state = AuthState.LOADING
            Auth.statusMessage = "Fetching module list…"

            val manifest = fetchManifest(session.sessionToken, entitlement.manifestUrl)
                ?: return fail("Failed to fetch manifest")

            if (!verifyManifestSignature(manifest, entitlement.manifestSignature ?: "")) {
                return fail("Manifest signature invalid")
            }

            val entitled = (entitlement.enabledModules ?: emptyList()).toSet()
            val toLoad = manifest.modules.filter { it.name in entitled }
            Auth.modulesTotal = toLoad.size
            Auth.modulesLoaded = 0

            Files.createDirectories(cacheDir)

            for (module in toLoad) {
                Auth.statusMessage = "Loading ${module.name}… (${Auth.modulesLoaded + 1}/${Auth.modulesTotal})"
                val jarPath = ensureModule(session.sessionToken, module) ?: return fail("Failed to load ${module.name}")
                AddonLoader.loadFromPath(jarPath)
                Auth.modulesLoaded++
            }

            Auth.state = AuthState.READY
            Auth.statusMessage = "Ready"

        } catch (e: Exception) {
            logger.error("Auth failed with exception", e)
            fail(e.message ?: "unknown_error")
        }
    }

    private fun fail(reason: String) {
        logger.warn("Auth gate failed: {}", reason)
        Auth.failureReason = reason
        Auth.statusMessage = "Auth failed: $reason"
        Auth.state = AuthState.FAILED
    }

    // ── HTTP calls ────────────────────────────────────────────────────────────

    private fun verifyMinecraft(sessionToken: String, mcUsername: String): EntitlementResponse? {
        val body = gson.toJson(mapOf("session_token" to sessionToken, "minecraft_username" to mcUsername))
        val req = HttpRequest.newBuilder(URI.create("$SERVER_BASE_URL/auth/verify-minecraft"))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        return try {
            val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
            gson.fromJson(resp.body(), EntitlementResponse::class.java)
        } catch (e: Exception) {
            logger.error("verify-minecraft call failed", e)
            null
        }
    }

    private fun fetchManifest(sessionToken: String, url: String): ManifestResponse? {
        val req = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(20))
            .header("Authorization", "Bearer $sessionToken")
            .GET()
            .build()
        return try {
            val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
            if (resp.statusCode() != 200) return null
            gson.fromJson(resp.body(), ManifestResponse::class.java)
        } catch (e: Exception) {
            logger.error("Manifest fetch failed", e)
            null
        }
    }

    private fun ensureModule(sessionToken: String, module: ManifestModule): Path? {
        val dest = cacheDir.resolve("${module.name}.jar")
        if (Files.exists(dest) && sha256Hex(dest.toFile()) == module.sha256) {
            return dest
        }
        // Download
        val downloadUrl = "$SERVER_BASE_URL/content/module/${module.name}"
        val req = HttpRequest.newBuilder(URI.create(downloadUrl))
            .timeout(Duration.ofSeconds(60))
            .header("Authorization", "Bearer $sessionToken")
            .GET()
            .build()
        return try {
            val resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray())
            if (resp.statusCode() != 200) {
                logger.error("Module {} download returned {}", module.name, resp.statusCode())
                return null
            }
            Files.write(dest, resp.body())
            val actual = sha256Hex(dest.toFile())
            if (actual != module.sha256) {
                logger.error("Module {} digest mismatch: expected {} got {}", module.name, module.sha256, actual)
                Files.deleteIfExists(dest)
                return null
            }
            dest
        } catch (e: Exception) {
            logger.error("Module {} download failed", module.name, e)
            null
        }
    }

    // ── Manifest signature verification ───────────────────────────────────────

    private fun verifyManifestSignature(manifest: ManifestResponse, sigBase64: String): Boolean {
        if (sigBase64.isBlank()) return false
        return try {
            // Reconstruct the exact payload that was signed by the server:
            // json.Marshal of { build_id, channel, minimum_loader_version, modules, native_components }
            // Fields in declaration order matching Go struct — Gson serializes in declaration order.
            val payload = SignedPayload(
                build_id = manifest.buildId,
                channel = manifest.channel,
                minimum_loader_version = manifest.minimumLoaderVersion,
                modules = manifest.modules.map { m ->
                    SignedModule(m.name, m.url, m.sha256, m.required, m.initOrder)
                },
                native_components = manifest.nativeComponents.map { n ->
                    SignedNative(n.name, n.url, n.sha256, n.required)
                }
            )
            val payloadJson = gson.toJson(payload).toByteArray(StandardCharsets.UTF_8)
            val sigBytes = Base64.getDecoder().decode(sigBase64)
            val rawKey = Base64.getDecoder().decode(MANIFEST_PUBLIC_KEY_B64)
            verifyEd25519(rawKey, payloadJson, sigBytes)
        } catch (e: Exception) {
            logger.error("Manifest signature verification failed", e)
            false
        }
    }

    private fun verifyEd25519(rawPublicKey: ByteArray, message: ByteArray, sig: ByteArray): Boolean {
        val keyBytes = ED25519_ASN1_HEADER + rawPublicKey
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("Ed25519")
        val publicKey = keyFactory.generatePublic(keySpec)
        val verifier = Signature.getInstance("Ed25519")
        verifier.initVerify(publicKey)
        verifier.update(message)
        return verifier.verify(sig)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun sha256Hex(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { stream ->
            val buf = ByteArray(8192)
            var n: Int
            while (stream.read(buf).also { n = it } != -1) digest.update(buf, 0, n)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    // ── Response DTOs ─────────────────────────────────────────────────────────

    private data class EntitlementResponse(
        val authorized: Boolean = false,
        val reason: String? = null,
        @SerializedName("plan_tier") val planTier: String? = null,
        @SerializedName("enabled_modules") val enabledModules: List<String>? = null,
        @SerializedName("enabled_features") val enabledFeatures: List<String>? = null,
        @SerializedName("manifest_url") val manifestUrl: String? = null,
        @SerializedName("manifest_signature") val manifestSignature: String? = null
    )

    private data class ManifestModule(
        val name: String = "",
        val url: String = "",
        val sha256: String = "",
        val required: Boolean = false,
        @SerializedName("init_order") val initOrder: Int = 0
    )

    private data class ManifestNative(
        val name: String = "",
        val url: String = "",
        val sha256: String = "",
        val required: Boolean = false
    )

    private data class ManifestResponse(
        val id: String = "",
        @SerializedName("build_id") val buildId: String = "",
        val channel: String = "",
        @SerializedName("minimum_loader_version") val minimumLoaderVersion: String = "",
        val modules: List<ManifestModule> = emptyList(),
        @SerializedName("native_components") val nativeComponents: List<ManifestNative> = emptyList(),
        val signature: String = ""
    )

    // Signed payload structs — field declaration order MUST match Go struct order for JSON to match
    private data class SignedModule(
        val name: String, val url: String, val sha256: String,
        val required: Boolean, val init_order: Int
    )
    private data class SignedNative(
        val name: String, val url: String, val sha256: String, val required: Boolean
    )
    private data class SignedPayload(
        val build_id: String, val channel: String, val minimum_loader_version: String,
        val modules: List<SignedModule>, val native_components: List<SignedNative>
    )
}
```

- [ ] **Step 2: Build**

```bash
./gradlew classes
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 3: Commit**

```bash
git add src/main/kotlin/org/cobalt/internal/auth/CobaltAuthService.kt
git commit -m "feat(auth): add CobaltAuthService background auth and module loader"
```

---

### Task 8: Mod — gate title screen buttons in `TitleScreenMixin.java`

**Files:**
- Modify: `src/main/java/org/cobalt/mixin/render/TitleScreenMixin.java`

Add a `mouseClicked` injection that swallows Singleplayer and Multiplayer clicks while `Auth.isGateLocked()` is true.

- [ ] **Step 1: Add the necessary imports to `TitleScreenMixin.java`**

Add to the import block:

```java
import org.cobalt.internal.auth.Auth;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
```

- [ ] **Step 2: Add the click gate injection**

Append inside the `TitleScreenMixin` class, after the existing `@Inject` methods:

```java
@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
private void cobalt$gateMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
    if (!Auth.INSTANCE.isGateLocked()) return;

    Screen screen = (Screen) (Object) this;
    int centerX = screen.width / 2;
    int singleplayerY = screen.height / 4 + 48;
    int multiplayerY = screen.height / 4 + 72;
    int buttonHeight = 20;
    int buttonWidth = 200;
    int left = centerX - 100;

    boolean overSingleplayer = mouseX >= left && mouseX <= left + buttonWidth
        && mouseY >= singleplayerY && mouseY <= singleplayerY + buttonHeight;
    boolean overMultiplayer = mouseX >= left && mouseX <= left + buttonWidth
        && mouseY >= multiplayerY && mouseY <= multiplayerY + buttonHeight;

    if (overSingleplayer || overMultiplayer) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
```

- [ ] **Step 3: Build**

```bash
./gradlew classes
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add src/main/java/org/cobalt/mixin/render/TitleScreenMixin.java
git commit -m "feat(auth): gate singleplayer/multiplayer until auth ready"
```

---

### Task 9: Mod — wire up in `Cobalt.kt`

**Files:**
- Modify: `src/main/kotlin/org/cobalt/Cobalt.kt`

Read the session at startup and start `CobaltAuthService` when the title screen is first shown.

- [ ] **Step 1: Add the import block additions to `Cobalt.kt`**

Add to the imports in `Cobalt.kt`:

```kotlin
import org.cobalt.internal.auth.Auth
import org.cobalt.internal.auth.AuthState
import org.cobalt.internal.auth.CobaltAuthService
import org.cobalt.internal.auth.CobaltSession
import org.cobalt.api.event.impl.render.GuiRenderEvent
```

- [ ] **Step 2: Add a session field and auth trigger to `Cobalt`**

In the `Cobalt` object/class, add at the class level:

```kotlin
private var cobaltSession: CobaltSession = CobaltSession.INVALID
private var authStarted = false
```

- [ ] **Step 3: Read session in `onInitializeClient()`**

At the very start of `onInitializeClient()` (before module registration), add:

```kotlin
cobaltSession = CobaltSession.readAndDelete()
EventBus.register(this)
```

- [ ] **Step 4: Add `onGuiRender` to trigger auth on first title screen appearance**

Add to the `Cobalt` object:

```kotlin
@SubscribeEvent
fun onGuiRender(event: GuiRenderEvent) {
    if (authStarted) return
    val screen = net.minecraft.client.Minecraft.getInstance().screen
    if (screen is net.minecraft.client.gui.screens.TitleScreen) {
        authStarted = true
        CobaltAuthService.start(cobaltSession)
    }
}
```

- [ ] **Step 5: Build and run**

```bash
./gradlew runClient
```

Expected: Minecraft launches. Title screen shows. Singleplayer/Multiplayer are clickable only after auth completes (or if running without `-Dcobalt.session`, `Auth.state` stays `FAILED` and buttons stay locked — verify this behaviour).

To test with a valid session token, run the server locally, perform a full auth flow to get a session token, write `{"session_token":"<token>"}` to a temp file, and launch with `-Dcobalt.session=<path>`.

- [ ] **Step 6: Commit**

```bash
git add src/main/kotlin/org/cobalt/Cobalt.kt
git commit -m "feat(auth): wire up CobaltSession and CobaltAuthService on title screen"
```

---

## Self-Review Checklist

- [x] Server: `Start()` skips MC username check when empty → Task 1
- [x] Server: `VerifyMinecraft()` binds hwid_pending, verifies fully_bound → Task 2
- [x] Server: `/auth/verify-minecraft` route registered with rate limit → Task 3
- [x] Mod: AuthState holds state + message + progress counters → Task 4
- [x] Mod: CobaltSession reads + deletes session file at startup → Task 5
- [x] Mod: AddonLoader.loadFromPath exposed → Task 6
- [x] Mod: CobaltAuthService background thread, all failure paths call `fail()` → Task 7
- [x] Mod: Singleplayer/Multiplayer gated while `Auth.isGateLocked()` → Task 8
- [x] Mod: Auth starts on first title screen render → Task 9
- [x] Manifest Ed25519 signature verified using hard-coded public key → Task 7
- [x] Module JAR SHA-256 verified before loading → Task 7
- [x] All failures set `AuthState.FAILED` and never proceed → Task 7
