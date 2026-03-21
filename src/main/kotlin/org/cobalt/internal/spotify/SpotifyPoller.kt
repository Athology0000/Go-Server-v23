package org.cobalt.internal.spotify

import java.io.File

data class SpotifyTrack(
    val name: String,
    val artist: String,
    val progressMs: Long,
    val durationMs: Long,
    val isPlaying: Boolean,
    val snapshotMs: Long = System.currentTimeMillis(),
) {
    /** Smoothly interpolated playback position in ms. */
    val currentProgressMs: Long
        get() = if (isPlaying)
            (progressMs + (System.currentTimeMillis() - snapshotMs)).coerceAtMost(durationMs)
        else
            progressMs
}

object SpotifyPoller {

    @Volatile var current: SpotifyTrack? = null

    /** Absolute path to the cached album-art image, or "" if not yet available. */
    @Volatile var currentArtPath: String = ""
    /** Incremented each time a new art file is written — used to detect changes. */
    @Volatile var artVersion: Int = 0

    private const val POLL_MS = 2_000L
    @Volatile private var lastPollMs = 0L
    @Volatile private var polling = false

    private var lastArtTrack = ""
    private var artFileCounter = 0

    private val isWindows = System.getProperty("os.name", "").startsWith("Windows", ignoreCase = true)

    // Written once to a stable temp path; reused on every poll to avoid repeated I/O.
    private val scriptFile: File by lazy {
        File(System.getProperty("java.io.tmpdir"), "cobalt_spotify_smtc.ps1").also { f ->
            f.writeText(PS_SCRIPT)
        }
    }

    // PowerShell script that reads the currently-playing track via Windows SMTC.
    // Outputs: Title|Artist|PositionMs|DurationMs|IsPlaying|ArtPath  — or "NONE" / "ERROR".
    // Art is extracted directly from the SMTC session thumbnail (no iTunes lookup needed).
    private val PS_SCRIPT = """
Add-Type -AssemblyName System.Runtime.WindowsRuntime | Out-Null
${'$'}helper = [System.WindowsRuntimeSystemExtensions]
${'$'}asTask = (${'$'}helper.GetMethods() | Where-Object { ${'$'}_.Name -eq 'AsTask' -and ${'$'}_.IsGenericMethodDefinition -and ${'$'}_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]
function Await(${'$'}op, ${'$'}type) {
    ${'$'}t = ${'$'}asTask.MakeGenericMethod(${'$'}type).Invoke(${'$'}null, ${'$'}op)
    ${'$'}t.Wait() | Out-Null
    ${'$'}t.Result
}
try {
    ${'$'}mgr = Await ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()) `
              ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime])
    ${'$'}s = ${'$'}mgr.GetCurrentSession()
    if (${'$'}null -eq ${'$'}s) { 'NONE'; exit }
    ${'$'}p  = Await (${'$'}s.TryGetMediaPropertiesAsync()) `
              ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties,Windows.Media.Control,ContentType=WindowsRuntime])
    ${'$'}pb = ${'$'}s.GetPlaybackInfo()
    ${'$'}tl = ${'$'}s.GetTimelineProperties()
    ${'$'}artOut = ""
    try {
        if (${'$'}null -ne ${'$'}p.Thumbnail) {
            ${'$'}strm = Await (${'$'}p.Thumbnail.OpenReadAsync()) `
                        ([Windows.Storage.Streams.IRandomAccessStreamWithContentType,Windows.Storage.Streams,ContentType=WindowsRuntime])
            ${'$'}asRead = ([System.IO.WindowsRuntimeStreamExtensions].GetMethods() | Where-Object { ${'$'}_.Name -eq 'AsStreamForRead' -and ${'$'}_.GetParameters().Count -eq 1 })[0]
            ${'$'}netStream = ${'$'}asRead.Invoke(${'$'}null, @(${'$'}strm))
            ${'$'}artOut = [System.IO.Path]::Combine([System.IO.Path]::GetTempPath(), "cobalt_smtc_art.jpg")
            ${'$'}fs = [System.IO.File]::OpenWrite(${'$'}artOut)
            ${'$'}netStream.CopyTo(${'$'}fs)
            ${'$'}fs.Close()
            ${'$'}netStream.Close()
        }
    } catch { ${'$'}artOut = "" }
    "$(${'$'}p.Title)|$(${'$'}p.Artist)|$([long]${'$'}tl.Position.TotalMilliseconds)|$([long]${'$'}tl.EndTime.TotalMilliseconds)|$(${'$'}pb.PlaybackStatus -eq 'Playing')|$(${'$'}artOut)"
} catch { 'ERROR' }
    """.trimIndent()

    /** Call from tick — kicks off an async poll when the interval has elapsed. */
    fun update() {
        if (!isWindows) return
        val now = System.currentTimeMillis()
        if (now - lastPollMs < POLL_MS || polling) return
        lastPollMs = now
        polling = true
        Thread({
            try { poll() } catch (_: Exception) {}
            finally { polling = false }
        }, "spotify-poll").also { it.isDaemon = true }.start()
    }

    /**
     * Send a media control key to the OS (system-wide).
     * Common VK codes: 0xB3 = Play/Pause, 0xB0 = Next Track, 0xB1 = Prev Track.
     * After sending, forces a re-poll so the HUD updates immediately.
     */
    fun sendCommand(vkCode: Int) {
        if (!isWindows) return
        Thread({
            try {
                val script = """
Add-Type -TypeDefinition @'
using System.Runtime.InteropServices;
public class CobaltKbd {
    [DllImport("user32.dll")] public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, uint dwExtraInfo);
}
'@
CobaltKbd::keybd_event($vkCode, 0, 0, 0)
CobaltKbd::keybd_event($vkCode, 0, 2, 0)
                """.trimIndent()
                ProcessBuilder(
                    "powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-Command", script
                ).redirectErrorStream(true).start().waitFor()
                Thread.sleep(600)
                lastPollMs = 0L  // force re-poll so HUD reflects new play state
            } catch (_: Exception) {}
        }, "spotify-cmd").also { it.isDaemon = true }.start()
    }

    private fun poll() {
        val proc = ProcessBuilder(
            "powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass",
            "-File", scriptFile.absolutePath
        ).redirectErrorStream(true).start()

        val output = proc.inputStream.bufferedReader().readText().trim()
        // Snapshot AFTER receiving data so the interpolation baseline matches "when we
        // learned the position", not "when we started asking" (script takes 100-400ms).
        // This keeps the display at or slightly behind actual, never ahead.
        val snapshotMs = System.currentTimeMillis()
        proc.waitFor()

        val rawTrack = when {
            output.isEmpty() || output == "NONE" || output == "ERROR" -> null
            else -> parseOutput(output, snapshotMs)
        }

        // Ratchet: for the same playing track, if SMTC lag causes the new interpolated
        // position to fall behind what's already displayed, carry the current value forward
        // rather than snapping backward.  Only applies to small differences (< 3 s) so
        // intentional seeks (large jumps) are still honoured.
        val track = rawTrack?.let { t ->
            val prev = current
            if (prev != null && t.name == prev.name && t.artist == prev.artist && t.isPlaying) {
                val prevMs = prev.currentProgressMs
                val newMs  = t.currentProgressMs
                if (newMs < prevMs && prevMs - newMs < 3_000L)
                    t.copy(progressMs = prevMs, snapshotMs = snapshotMs)
                else t
            } else t
        }
        current = track

        // Update album art when the track changes
        val trackKey = track?.let { "${it.name}|${it.artist}" } ?: ""
        if (trackKey != lastArtTrack) {
            lastArtTrack = trackKey
            if (track != null) {
                // Always fetch clean art from iTunes (no branding watermarks)
                Thread({ fetchArt(track.artist, track.name) }, "art-fetch")
                    .also { it.isDaemon = true }.start()
            } else {
                currentArtPath = ""
                artVersion++
            }
        }
    }

    private fun fetchArt(artist: String, title: String) {
        try {
            val q = java.net.URLEncoder.encode("$artist $title", "UTF-8")

            // Try Deezer first — clean art, no watermarks, no auth needed
            var artUrl: String? = null
            runCatching {
                val deezerJson = java.net.URL("https://api.deezer.com/search?q=$q&limit=5").readText()
                val root = com.google.gson.JsonParser.parseString(deezerJson).asJsonObject
                val data = root.getAsJsonArray("data")
                if (data != null && data.size() > 0) {
                    artUrl = data[0].asJsonObject.getAsJsonObject("album")?.get("cover_big")?.asString
                }
            }

            // Fallback to iTunes
            if (artUrl == null) {
                runCatching {
                    val itunesJson = java.net.URL("https://itunes.apple.com/search?term=$q&media=music&limit=1&entity=song").readText()
                    val root = com.google.gson.JsonParser.parseString(itunesJson).asJsonObject
                    val results = root.getAsJsonArray("results")
                    if (results != null && results.size() > 0) {
                        artUrl = results[0].asJsonObject.get("artworkUrl100")?.asString
                            ?.replace("100x100bb", "600x600bb")
                    }
                }
            }

            artUrl ?: return
            val bytes = java.net.URL(artUrl).readBytes()
            artFileCounter++
            File(System.getProperty("java.io.tmpdir"), "cobalt_art_${artFileCounter - 1}.jpg").delete()
            val f = File(System.getProperty("java.io.tmpdir"), "cobalt_art_$artFileCounter.jpg")
            f.writeBytes(bytes)
            currentArtPath = f.absolutePath
            artVersion     = artFileCounter
        } catch (_: Exception) {}
    }

    private fun parseOutput(raw: String, snapshotMs: Long): SpotifyTrack? {
        val parts = raw.split("|")
        if (parts.size < 5) return null
        return runCatching {
            SpotifyTrack(
                name       = parts[0],
                artist     = parts[1],
                progressMs = parts[2].toLong(),
                durationMs = parts[3].toLong().coerceAtLeast(1L),
                isPlaying  = parts[4].trim().equals("True", ignoreCase = true),
                snapshotMs = snapshotMs,
            )
        }.getOrNull()
    }
}
