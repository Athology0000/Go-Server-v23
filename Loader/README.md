# Cobalt Loader Project

A clean starter Fabric/Kotlin loader project for a split-loader setup:

- `loader` is the only jar users install.
- `shared-api` is the tiny stable API that loader and runtime modules share.
- `sample-module` is an example protected runtime module jar.

The loader authenticates, verifies a signed manifest, downloads entitled jars, checks SHA-256, then loads runtime module entrypoints.

## Requirements

- Java 21+
- Gradle 8.14+ recommended
- Minecraft 1.21.11
- Fabric Loader 0.18.1+
- Fabric API
- Fabric Language Kotlin

## Build

```bash
gradle build
```

If you do not have Gradle installed, create a wrapper first:

```bash
gradle wrapper --gradle-version 8.14.3
./gradlew build
```

On Windows:

```bat
gradle wrapper --gradle-version 8.14.3
gradlew.bat build
```

## Output jars

Loader jar:

```text
loader/build/libs/cobalt-loader-0.1.0.jar
```

Sample runtime module jar:

```text
sample-module/build/libs/sample-module-0.1.0.jar
```

Only distribute the loader jar. Put runtime module jars on your server.

## Configure loader

The loader reads config from JVM system properties first, then environment variables.

Required for real auth:

```text
-Dcobalt.manifest.publicKey=BASE64_X509_ED25519_PUBLIC_KEY
```

Users enter their Cobalt account username in-game when the loader starts. The loader sends that username with the active Minecraft username to the auth server, then starts a heartbeat after auth succeeds.

Optional:

```text
-Dcobalt.auth.url=https://valiant-cooperation-production.up.railway.app/api/auth/verify
-Dcobalt.heartbeat.url=https://valiant-cooperation-production.up.railway.app/api/auth/heartbeat
-Dcobalt.module.base.url=https://valiant-cooperation-production.up.railway.app/modules/
-Dcobalt.username=your_login_username
-Dcobalt.client.version=0.1.0
-Dcobalt.cache.dir=config/cobalt/runtime-modules
-Dcobalt.user.config=config/cobalt/loader.properties
-Dcobalt.heartbeat.intervalSeconds=30
-Dcobalt.heartbeat.failureLimit=2
-Dcobalt.allow.localhost=true
-Dcobalt.allow.external.modules=false
```

`cobalt.allow.localhost=true` only allows `http://localhost` / `http://127.0.0.1` for dev testing. Production module URLs must be HTTPS.

## Server auth response shape

```json
{
  "ok": true,
  "sessionToken": "short-lived-token",
  "manifest": {
    "payloadBase64": "base64-json-payload",
    "signatureBase64": "ed25519-signature-over-payload-bytes"
  }
}
```

The decoded manifest payload shape:

```json
{
  "issuedAt": 1777920000,
  "expiresAt": 1777920900,
  "userId": "user_123",
  "minecraftUsername": "PlayerName",
  "modules": [
    {
      "id": "sample",
      "version": "0.1.0",
      "delivery": "jar",
      "url": "sample-0.1.0.jar",
      "sha256": "actual_sha256_here",
      "entrypoint": "org.cobalt.sample.SampleRuntimeModule",
      "requiredRole": "standard"
    }
  ]
}
```

## Runtime module contract

Runtime jars must include a class that implements:

```kotlin
org.cobalt.api.runtime.CobaltRuntimeModule
```

The loader constructs it with an empty constructor and calls `onLoad(context)`.

## Raw bytecode modules

For a single-class module, the signed manifest can use raw bytecode instead of a jar:

```json
{
  "id": "sample",
  "version": "0.1.0",
  "delivery": "class",
  "url": "sample-0.1.0.class",
  "sha256": "actual_sha256_of_class_bytes",
  "entrypoint": "org.cobalt.sample.SampleRuntimeModule",
  "requiredRole": "standard"
}
```

If `url` is omitted, the loader requests `{id}-{version}.class` from `cobalt.module.base.url`. Raw bytecode modules are defined in memory and are not cached to disk. Use jar delivery for modules with multiple classes, Kotlin helper classes, resources, or bundled dependencies.

## Security defaults

The loader:

- Fails closed when auth fails.
- Requires an Ed25519 signed manifest.
- Rejects expired manifests.
- Rejects non-HTTPS module URLs, except localhost when explicitly enabled.
- Resolves relative module URLs from `https://valiant-cooperation-production.up.railway.app/modules/` by default.
- Rejects module URLs outside `cobalt.module.base.url` unless `cobalt.allow.external.modules=true`.
- Rejects module URLs with query strings or fragments.
- Verifies raw bytecode is a Java class file for the manifest entrypoint before defining it.
- Unloads protected modules if heartbeat fails or is rejected repeatedly.
- Validates module id/version/path basics.
- Verifies SHA-256 before loading a jar.
- Does not register protected modules before auth.

This is not magic anti-reverse-engineering. The key security improvement is that protected code is not inside the shipped loader jar.
