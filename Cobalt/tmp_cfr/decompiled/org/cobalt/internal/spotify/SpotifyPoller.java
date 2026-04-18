/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Lazy
 *  kotlin.LazyKt
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.io.CloseableKt
 *  kotlin.io.FilesKt
 *  kotlin.io.TextStreamsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.sequences.Sequence
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import org.cobalt.internal.spotify.SpotifyTrack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0007\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0005\u0010\u0003J\u0015\u0010\b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\b\u0010\tJ\u000f\u0010\n\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\n\u0010\u0003J\u001f\u0010\u000e\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000bH\u0002\u00a2\u0006\u0004\b\u000e\u0010\u000fJ!\u0010\u0014\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0011H\u0002\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0016\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\u0016\u0010\u0003R$\u0010\u0017\u001a\u0004\u0018\u00010\u00138\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\"\u0010\u001e\u001a\u00020\u001d8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\"\u0010$\u001a\u00020\u000b8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b$\u0010%\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\"\u0010*\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b*\u0010+\u001a\u0004\b,\u0010-\"\u0004\b.\u0010\tR\u0014\u0010/\u001a\u00020\u00118\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b/\u00100R\u0016\u00101\u001a\u00020\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b1\u00100R\u0016\u00103\u001a\u0002028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b3\u00104R\u0016\u00105\u001a\u0002028\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b5\u00104R\u0018\u00107\u001a\u0004\u0018\u0001068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b7\u00108R\u0016\u00109\u001a\u00020\u000b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b9\u0010%R\u0016\u0010:\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b:\u0010+R\u0014\u0010;\u001a\u0002028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b;\u00104R\u001b\u0010A\u001a\u00020<8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b=\u0010>\u001a\u0004\b?\u0010@R\u001b\u0010D\u001a\u00020<8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\bB\u0010>\u001a\u0004\bC\u0010@R\u0014\u0010E\u001a\u00020\u000b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bE\u0010%R\u0014\u0010F\u001a\u00020\u000b8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bF\u0010%\u00a8\u0006G"}, d2={"Lorg/cobalt/internal/spotify/SpotifyPoller;", "", "<init>", "()V", "", "update", "", "vkCode", "sendCommand", "(I)V", "poll", "", "artist", "title", "fetchArt", "(Ljava/lang/String;Ljava/lang/String;)V", "raw", "", "snapshotMs", "Lorg/cobalt/internal/spotify/SpotifyTrack;", "parseOutput", "(Ljava/lang/String;J)Lorg/cobalt/internal/spotify/SpotifyTrack;", "ensureMeterReader", "current", "Lorg/cobalt/internal/spotify/SpotifyTrack;", "getCurrent", "()Lorg/cobalt/internal/spotify/SpotifyTrack;", "setCurrent", "(Lorg/cobalt/internal/spotify/SpotifyTrack;)V", "", "audioLevel", "F", "getAudioLevel", "()F", "setAudioLevel", "(F)V", "currentArtPath", "Ljava/lang/String;", "getCurrentArtPath", "()Ljava/lang/String;", "setCurrentArtPath", "(Ljava/lang/String;)V", "artVersion", "I", "getArtVersion", "()I", "setArtVersion", "POLL_MS", "J", "lastPollMs", "", "polling", "Z", "meterReaderStarted", "Ljava/lang/Process;", "meterProcess", "Ljava/lang/Process;", "lastArtTrack", "artFileCounter", "isWindows", "Ljava/io/File;", "scriptFile$delegate", "Lkotlin/Lazy;", "getScriptFile", "()Ljava/io/File;", "scriptFile", "meterScriptFile$delegate", "getMeterScriptFile", "meterScriptFile", "PS_SCRIPT", "PS_METER_SCRIPT", "cobalt"})
@SourceDebugExtension(value={"SMAP\nSpotifyPoller.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpotifyPoller.kt\norg/cobalt/internal/spotify/SpotifyPoller\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ReadWrite.kt\nkotlin/io/TextStreamsKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,520:1\n1#2:521\n1#2:523\n66#3:522\n1342#4,2:524\n*S KotlinDebug\n*F\n+ 1 SpotifyPoller.kt\norg/cobalt/internal/spotify/SpotifyPoller\n*L\n267#1:523\n267#1:522\n268#1:524,2\n*E\n"})
public final class SpotifyPoller {
    @NotNull
    public static final SpotifyPoller INSTANCE = new SpotifyPoller();
    @Nullable
    private static volatile SpotifyTrack current;
    private static volatile float audioLevel;
    @NotNull
    private static volatile String currentArtPath;
    private static volatile int artVersion;
    private static final long POLL_MS = 2000L;
    private static volatile long lastPollMs;
    private static volatile boolean polling;
    private static volatile boolean meterReaderStarted;
    @Nullable
    private static volatile Process meterProcess;
    @NotNull
    private static String lastArtTrack;
    private static int artFileCounter;
    private static final boolean isWindows;
    @NotNull
    private static final Lazy scriptFile$delegate;
    @NotNull
    private static final Lazy meterScriptFile$delegate;
    @NotNull
    private static final String PS_SCRIPT;
    @NotNull
    private static final String PS_METER_SCRIPT;

    private SpotifyPoller() {
    }

    @Nullable
    public final SpotifyTrack getCurrent() {
        return current;
    }

    public final void setCurrent(@Nullable SpotifyTrack spotifyTrack) {
        current = spotifyTrack;
    }

    public final float getAudioLevel() {
        return audioLevel;
    }

    public final void setAudioLevel(float f) {
        audioLevel = f;
    }

    @NotNull
    public final String getCurrentArtPath() {
        return currentArtPath;
    }

    public final void setCurrentArtPath(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        currentArtPath = string;
    }

    public final int getArtVersion() {
        return artVersion;
    }

    public final void setArtVersion(int n) {
        artVersion = n;
    }

    private final File getScriptFile() {
        Lazy lazy = scriptFile$delegate;
        return (File)lazy.getValue();
    }

    private final File getMeterScriptFile() {
        Lazy lazy = meterScriptFile$delegate;
        return (File)lazy.getValue();
    }

    public final void update() {
        Thread thread;
        if (!isWindows) {
            return;
        }
        this.ensureMeterReader();
        long now = System.currentTimeMillis();
        if (now - lastPollMs < 2000L || polling) {
            return;
        }
        lastPollMs = now;
        polling = true;
        Thread it = thread = new Thread(SpotifyPoller::update$lambda$0, "spotify-poll");
        boolean bl = false;
        it.setDaemon(true);
        thread.start();
    }

    public final void sendCommand(int vkCode) {
        Thread thread;
        if (!isWindows) {
            return;
        }
        Thread it = thread = new Thread(() -> SpotifyPoller.sendCommand$lambda$0(vkCode), "spotify-cmd");
        boolean bl = false;
        it.setDaemon(true);
        thread.start();
    }

    private final void poll() {
        Object trackKey;
        Object object;
        SpotifyTrack track;
        block12: {
            block11: {
                SpotifyTrack spotifyTrack;
                SpotifyTrack rawTrack;
                String[] stringArray = new String[]{"powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-File", this.getScriptFile().getAbsolutePath()};
                Process proc = new ProcessBuilder(stringArray).redirectErrorStream(true).start();
                InputStream inputStream = proc.getInputStream();
                Intrinsics.checkNotNullExpressionValue((Object)inputStream, (String)"getInputStream(...)");
                InputStream inputStream2 = inputStream;
                Charset charset = Charsets.UTF_8;
                Reader reader = new InputStreamReader(inputStream2, charset);
                int n = 8192;
                String output = ((Object)StringsKt.trim((CharSequence)TextStreamsKt.readText((Reader)(reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, n))))).toString();
                long snapshotMs = System.currentTimeMillis();
                proc.waitFor();
                SpotifyTrack spotifyTrack2 = rawTrack = ((CharSequence)output).length() == 0 || Intrinsics.areEqual((Object)output, (Object)"NONE") || Intrinsics.areEqual((Object)output, (Object)"ERROR") ? null : this.parseOutput(output, snapshotMs);
                if (spotifyTrack2 != null) {
                    SpotifyTrack t = spotifyTrack2;
                    boolean bl = false;
                    SpotifyTrack prev = current;
                    if (prev != null && Intrinsics.areEqual((Object)t.getName(), (Object)prev.getName()) && Intrinsics.areEqual((Object)t.getArtist(), (Object)prev.getArtist()) && t.isPlaying()) {
                        long prevMs = prev.getCurrentProgressMs();
                        long newMs = t.getCurrentProgressMs();
                        spotifyTrack = newMs < prevMs && prevMs - newMs < 3000L ? SpotifyTrack.copy$default(t, null, null, prevMs, 0L, false, snapshotMs, 27, null) : t;
                    } else {
                        spotifyTrack = t;
                    }
                } else {
                    spotifyTrack = null;
                }
                current = track = spotifyTrack;
                if (track == null || !track.isPlaying()) {
                    audioLevel *= 0.6f;
                }
                if ((object = track) == null) break block11;
                SpotifyTrack it = object;
                boolean bl = false;
                String string = it.getName() + "|" + it.getArtist();
                object = string;
                if (string != null) break block12;
            }
            object = "";
        }
        if (!Intrinsics.areEqual((Object)(trackKey = object), (Object)lastArtTrack)) {
            lastArtTrack = trackKey;
            if (track != null) {
                Thread thread;
                Thread it = thread = new Thread(() -> SpotifyPoller.poll$lambda$2(track), "art-fetch");
                boolean bl = false;
                it.setDaemon(true);
                thread.start();
            } else {
                currentArtPath = "";
                int n = artVersion;
                artVersion = n + 1;
            }
        }
    }

    private final void fetchArt(String artist, String title) {
        try {
            Charset data;
            URL root;
            Object object;
            Object $this$fetchArt_u24lambda_u240;
            String q = URLEncoder.encode(artist + " " + title, "UTF-8");
            String artUrl = null;
            SpotifyPoller spotifyPoller = this;
            try {
                $this$fetchArt_u24lambda_u240 = spotifyPoller;
                boolean bl = false;
                URL uRL = new URL("https://api.deezer.com/search?q=" + q + "&limit=5");
                Charset charset = Charsets.UTF_8;
                object = TextStreamsKt.readBytes((URL)uRL);
                String deezerJson = new String((byte[])object, charset);
                root = JsonParser.parseString((String)deezerJson).getAsJsonObject();
                data = root.getAsJsonArray("data");
                if (data != null && data.size() > 0) {
                    JsonObject jsonObject = data.get(0).getAsJsonObject().getAsJsonObject("album");
                    artUrl = jsonObject != null && (jsonObject = jsonObject.get("cover_big")) != null ? jsonObject.getAsString() : null;
                }
                $this$fetchArt_u24lambda_u240 = Result.constructor-impl((Object)Unit.INSTANCE);
            }
            catch (Throwable bl) {
                $this$fetchArt_u24lambda_u240 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)bl));
            }
            if (artUrl == null) {
                Object $this$fetchArt_u24lambda_u241;
                spotifyPoller = this;
                try {
                    $this$fetchArt_u24lambda_u241 = spotifyPoller;
                    boolean bl = false;
                    root = new URL("https://itunes.apple.com/search?term=" + q + "&media=music&limit=1&entity=song");
                    data = Charsets.UTF_8;
                    object = TextStreamsKt.readBytes((URL)root);
                    String itunesJson = new String((byte[])object, data);
                    root = JsonParser.parseString((String)itunesJson).getAsJsonObject();
                    JsonArray results = root.getAsJsonArray("results");
                    if (results != null && results.size() > 0) {
                        String string;
                        object = results.get(0).getAsJsonObject().get("artworkUrl100");
                        artUrl = object != null && (string = object.getAsString()) != null ? StringsKt.replace$default((String)string, (String)"100x100bb", (String)"600x600bb", (boolean)false, (int)4, null) : null;
                    }
                    $this$fetchArt_u24lambda_u241 = Result.constructor-impl((Object)Unit.INSTANCE);
                }
                catch (Throwable throwable) {
                    $this$fetchArt_u24lambda_u241 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
                }
            }
            if (artUrl == null) {
                return;
            }
            byte[] bytes = TextStreamsKt.readBytes((URL)new URL(artUrl));
            int $this$fetchArt_u24lambda_u241 = artFileCounter;
            artFileCounter = $this$fetchArt_u24lambda_u241 + 1;
            new File(System.getProperty("java.io.tmpdir"), "cobalt_art_" + (artFileCounter - 1) + ".jpg").delete();
            File f = new File(System.getProperty("java.io.tmpdir"), "cobalt_art_" + artFileCounter + ".jpg");
            FilesKt.writeBytes((File)f, (byte[])bytes);
            String string = f.getAbsolutePath();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getAbsolutePath(...)");
            currentArtPath = string;
            artVersion = artFileCounter;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private final SpotifyTrack parseOutput(String raw, long snapshotMs) {
        Object object;
        Object object2 = new String[]{"|"};
        List parts = StringsKt.split$default((CharSequence)raw, (String[])object2, (boolean)false, (int)0, (int)6, null);
        if (parts.size() < 5) {
            return null;
        }
        object2 = this;
        try {
            SpotifyPoller $this$parseOutput_u24lambda_u240 = (SpotifyPoller)object2;
            boolean bl = false;
            object = Result.constructor-impl((Object)new SpotifyTrack((String)parts.get(0), (String)parts.get(1), Long.parseLong((String)parts.get(2)), RangesKt.coerceAtLeast((long)Long.parseLong((String)parts.get(3)), (long)1L), StringsKt.equals((String)((Object)StringsKt.trim((CharSequence)((String)parts.get(4)))).toString(), (String)"True", (boolean)true), snapshotMs));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (SpotifyTrack)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void ensureMeterReader() {
        if (meterReaderStarted) {
            return;
        }
        Object object = this;
        synchronized (object) {
            boolean bl = false;
            if (meterReaderStarted) {
                return;
            }
            meterReaderStarted = true;
            Unit $i$a$-synchronized-SpotifyPoller$ensureMeterReader$2 = Unit.INSTANCE;
        }
        Object it = object = new Thread(SpotifyPoller::ensureMeterReader$lambda$1, "spotify-meter");
        boolean bl = false;
        ((Thread)it).setDaemon(true);
        ((Thread)object).start();
    }

    private static final File scriptFile_delegate$lambda$0() {
        File file;
        File f = file = new File(System.getProperty("java.io.tmpdir"), "cobalt_spotify_smtc.ps1");
        boolean bl = false;
        FilesKt.writeText$default((File)f, (String)PS_SCRIPT, null, (int)2, null);
        return file;
    }

    private static final File meterScriptFile_delegate$lambda$0() {
        File file;
        File f = file = new File(System.getProperty("java.io.tmpdir"), "cobalt_spotify_meter.ps1");
        boolean bl = false;
        FilesKt.writeText$default((File)f, (String)PS_METER_SCRIPT, null, (int)2, null);
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void update$lambda$0() {
        try {
            INSTANCE.poll();
        }
        catch (Exception exception) {
        }
        finally {
            polling = false;
        }
    }

    private static final void sendCommand$lambda$0(int $vkCode) {
        try {
            String script = StringsKt.trimIndent((String)("\nAdd-Type -TypeDefinition @'\nusing System.Runtime.InteropServices;\npublic class CobaltKbd {\n    [DllImport(\"user32.dll\")] public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, uint dwExtraInfo);\n}\n'@\nCobaltKbd::keybd_event(" + $vkCode + ", 0, 0, 0)\nCobaltKbd::keybd_event(" + $vkCode + ", 0, 2, 0)\n                "));
            String[] stringArray = new String[]{"powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-Command", script};
            new ProcessBuilder(stringArray).redirectErrorStream(true).start().waitFor();
            Thread.sleep(600L);
            lastPollMs = 0L;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static final void poll$lambda$2(SpotifyTrack $track) {
        INSTANCE.fetchArt($track.getArtist(), $track.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static final void ensureMeterReader$lambda$1() {
        while (isWindows) {
            try {
                Process proc;
                Object object = new String[]{"powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-File", INSTANCE.getMeterScriptFile().getAbsolutePath()};
                meterProcess = proc = new ProcessBuilder((String)object).redirectErrorStream(true).start();
                InputStream inputStream = proc.getInputStream();
                Intrinsics.checkNotNullExpressionValue((Object)inputStream, (String)"getInputStream(...)");
                object = inputStream;
                Charset charset = Charsets.UTF_8;
                Closeable closeable = new InputStreamReader((InputStream)object, charset);
                int n = 8192;
                Reader $this$useLines$iv = closeable instanceof BufferedReader ? (BufferedReader)closeable : new BufferedReader((Reader)closeable, n);
                boolean $i$f$useLines = false;
                closeable = $this$useLines$iv;
                n = 8192;
                closeable = closeable instanceof BufferedReader ? (BufferedReader)closeable : new BufferedReader((Reader)closeable, n);
                Throwable throwable = null;
                try {
                    BufferedReader it$iv = (BufferedReader)closeable;
                    boolean bl = false;
                    Sequence lines = TextStreamsKt.lineSequence((BufferedReader)it$iv);
                    boolean bl2 = false;
                    Sequence $this$forEach$iv = lines;
                    boolean $i$f$forEach = false;
                    for (Object element$iv : $this$forEach$iv) {
                        float f;
                        float parsed;
                        String line = (String)element$iv;
                        boolean bl3 = false;
                        String raw = ((Object)StringsKt.trim((CharSequence)line)).toString();
                        Float f2 = StringsKt.toFloatOrNull((String)StringsKt.replace$default((String)raw, (char)',', (char)'.', (boolean)false, (int)4, null));
                        float f3 = parsed = f2 != null ? RangesKt.coerceIn((float)f2.floatValue(), (float)0.0f, (float)1.0f) : 0.0f;
                        if (parsed <= 5.0E-5f) {
                            f = 0.0f;
                        } else {
                            float normalized = RangesKt.coerceIn((float)((parsed - 5.0E-5f) / 0.99995f), (float)0.0f, (float)1.0f);
                            float curved = (float)Math.pow(normalized, 0.34);
                            f = RangesKt.coerceIn((float)(normalized * 0.38f + curved * 0.96f), (float)0.0f, (float)1.0f);
                        }
                        float leveled = f;
                        float prev = audioLevel;
                        float blend = leveled > prev ? 0.9f : (leveled > prev * 0.6f ? 0.48f : 0.24f);
                        audioLevel = prev + (leveled - prev) * blend;
                    }
                    Unit unit = Unit.INSTANCE;
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
                }
                proc.waitFor();
            }
            catch (Exception exception) {
                audioLevel = 0.0f;
            }
            finally {
                meterProcess = null;
            }
            try {
                Thread.sleep(900L);
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    static {
        currentArtPath = "";
        lastArtTrack = "";
        String string = System.getProperty("os.name", "");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getProperty(...)");
        isWindows = StringsKt.startsWith((String)string, (String)"Windows", (boolean)true);
        scriptFile$delegate = LazyKt.lazy(SpotifyPoller::scriptFile_delegate$lambda$0);
        meterScriptFile$delegate = LazyKt.lazy(SpotifyPoller::meterScriptFile_delegate$lambda$0);
        PS_SCRIPT = "Add-Type -AssemblyName System.Runtime.WindowsRuntime | Out-Null\n$helper = [System.WindowsRuntimeSystemExtensions]\n$asTask = ($helper.GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.IsGenericMethodDefinition -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]\nfunction Await($op, $type) {\n    $t = $asTask.MakeGenericMethod($type).Invoke($null, $op)\n    $t.Wait() | Out-Null\n    $t.Result\n}\ntry {\n    $mgr = Await ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]::RequestAsync()) `\n              ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime])\n    $sessions = $mgr.GetSessions()\n    $s = $null\n    foreach ($sess in $sessions) {\n        $pb2 = $sess.GetPlaybackInfo()\n        if ($pb2.PlaybackStatus -eq 'Playing' -or $pb2.PlaybackStatus -eq 'Paused') {\n            if ($sess.SourceAppUserModelId -like '*spotify*') { $s = $sess; break }\n            if ($null -eq $s) { $s = $sess }\n        }\n    }\n    if ($null -eq $s) { 'NONE'; exit }\n    $p  = Await ($s.TryGetMediaPropertiesAsync()) `\n              ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties,Windows.Media.Control,ContentType=WindowsRuntime])\n    $pb = $s.GetPlaybackInfo()\n    $tl = $s.GetTimelineProperties()\n    $artOut = \"\"\n    try {\n        if ($null -ne $p.Thumbnail) {\n            $strm = Await ($p.Thumbnail.OpenReadAsync()) `\n                        ([Windows.Storage.Streams.IRandomAccessStreamWithContentType,Windows.Storage.Streams,ContentType=WindowsRuntime])\n            $asRead = ([System.IO.WindowsRuntimeStreamExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsStreamForRead' -and $_.GetParameters().Count -eq 1 })[0]\n            $netStream = $asRead.Invoke($null, @($strm))\n            $artOut = [System.IO.Path]::Combine([System.IO.Path]::GetTempPath(), \"cobalt_smtc_art.jpg\")\n            $fs = [System.IO.File]::OpenWrite($artOut)\n            $netStream.CopyTo($fs)\n            $fs.Close()\n            $netStream.Close()\n        }\n    } catch { $artOut = \"\" }\n    \"$($p.Title)|$($p.Artist)|$([long]$tl.Position.TotalMilliseconds)|$([long]$tl.EndTime.TotalMilliseconds)|$($pb.PlaybackStatus -eq 'Playing')|$($artOut)\"\n} catch { 'ERROR' }";
        PS_METER_SCRIPT = "Add-Type -Language CSharp -TypeDefinition @'\nusing System;\nusing System.Diagnostics;\nusing System.Runtime.InteropServices;\n\n[ComImport]\n[Guid(\"BCDE0395-E52F-467C-8E3D-C4579291692E\")]\npublic class MMDeviceEnumeratorComObject {}\n\npublic enum EDataFlow {\n    eRender,\n    eCapture,\n    eAll\n}\n\npublic enum ERole {\n    eConsole,\n    eMultimedia,\n    eCommunications\n}\n\n[Flags]\npublic enum CLSCTX : uint {\n    INPROC_SERVER = 0x1,\n    INPROC_HANDLER = 0x2,\n    LOCAL_SERVER = 0x4,\n    REMOTE_SERVER = 0x10,\n    ALL = INPROC_SERVER | INPROC_HANDLER | LOCAL_SERVER | REMOTE_SERVER\n}\n\n[ComImport]\n[Guid(\"A95664D2-9614-4F35-A746-DE8DB63617E6\")]\n[InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]\npublic interface IMMDeviceEnumerator {\n    int EnumAudioEndpoints(EDataFlow dataFlow, uint dwStateMask, out IntPtr ppDevices);\n    int GetDefaultAudioEndpoint(EDataFlow dataFlow, ERole role, out IMMDevice ppDevice);\n    int GetDevice(string pwstrId, out IMMDevice ppDevice);\n    int RegisterEndpointNotificationCallback(IntPtr pClient);\n    int UnregisterEndpointNotificationCallback(IntPtr pClient);\n}\n\n[ComImport]\n[Guid(\"D666063F-1587-4E43-81F1-B948E807363F\")]\n[InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]\npublic interface IMMDevice {\n    int Activate(ref Guid iid, CLSCTX dwClsCtx, IntPtr pActivationParams, [MarshalAs(UnmanagedType.IUnknown)] out object ppInterface);\n    int OpenPropertyStore(int stgmAccess, out IntPtr ppProperties);\n    int GetId([MarshalAs(UnmanagedType.LPWStr)] out string ppstrId);\n    int GetState(out int pdwState);\n}\n\n[ComImport]\n[Guid(\"77AA99A0-1BD6-484F-8BC7-2C654C9A9B6F\")]\n[InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]\npublic interface IAudioSessionManager2 {\n    int GetAudioSessionControl(IntPtr AudioSessionGuid, uint StreamFlags, out IntPtr SessionControl);\n    int GetSimpleAudioVolume(IntPtr AudioSessionGuid, uint StreamFlags, out IntPtr AudioVolume);\n    int GetSessionEnumerator(out IAudioSessionEnumerator SessionEnum);\n    int RegisterSessionNotification(IntPtr SessionNotification);\n    int UnregisterSessionNotification(IntPtr SessionNotification);\n    int RegisterDuckNotification(string sessionID, IntPtr duckNotification);\n    int UnregisterDuckNotification(IntPtr duckNotification);\n}\n\n[ComImport]\n[Guid(\"E2F5BB11-0570-40CA-ACDD-3AA01277DEE8\")]\n[InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]\npublic interface IAudioSessionEnumerator {\n    int GetCount(out int SessionCount);\n    int GetSession(int SessionCount, out IAudioSessionControl Session);\n}\n\n[ComImport]\n[Guid(\"F4B1A599-7266-4319-A8CA-E70ACB11E8CD\")]\n[InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]\npublic interface IAudioSessionControl {\n    int GetState(out int pRetVal);\n    int GetDisplayName([MarshalAs(UnmanagedType.LPWStr)] out string pRetVal);\n    int SetDisplayName([MarshalAs(UnmanagedType.LPWStr)] string Value, ref Guid EventContext);\n    int GetIconPath([MarshalAs(UnmanagedType.LPWStr)] out string pRetVal);\n    int SetIconPath([MarshalAs(UnmanagedType.LPWStr)] string Value, ref Guid EventContext);\n    int GetGroupingParam(out Guid pRetVal);\n    int SetGroupingParam(ref Guid Override, ref Guid EventContext);\n    int RegisterAudioSessionNotification(IntPtr NewNotifications);\n    int UnregisterAudioSessionNotification(IntPtr NewNotifications);\n}\n\n[ComImport]\n[Guid(\"bfb7ff88-7239-4fc9-8fa2-07c950be9c6d\")]\n[InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]\npublic interface IAudioSessionControl2 : IAudioSessionControl {\n    int GetSessionIdentifier([MarshalAs(UnmanagedType.LPWStr)] out string pRetVal);\n    int GetSessionInstanceIdentifier([MarshalAs(UnmanagedType.LPWStr)] out string pRetVal);\n    int GetProcessId(out uint pRetVal);\n    int IsSystemSoundsSession();\n    int SetDuckingPreference(bool optOut);\n}\n\n[ComImport]\n[Guid(\"C02216F6-8C67-4B5B-9D00-D008E73E0064\")]\n[InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]\npublic interface IAudioMeterInformation {\n    int GetPeakValue(out float pfPeak);\n    int GetMeteringChannelCount(out int pnChannelCount);\n    int GetChannelsPeakValues(int u32ChannelCount, [Out, MarshalAs(UnmanagedType.LPArray, SizeParamIndex = 0)] float[] afPeakValues);\n    int QueryHardwareSupport(out int pdwHardwareSupportMask);\n}\n\npublic static class SpotifyAudioMeter {\n    public static float GetSpotifyPeak() {\n        IMMDeviceEnumerator enumerator = null;\n        IMMDevice device = null;\n        object managerObj = null;\n        IAudioSessionEnumerator sessions = null;\n        try {\n            enumerator = (IMMDeviceEnumerator)new MMDeviceEnumeratorComObject();\n            Marshal.ThrowExceptionForHR(enumerator.GetDefaultAudioEndpoint(EDataFlow.eRender, ERole.eMultimedia, out device));\n\n            Guid iid = typeof(IAudioSessionManager2).GUID;\n            Marshal.ThrowExceptionForHR(device.Activate(ref iid, CLSCTX.ALL, IntPtr.Zero, out managerObj));\n            var manager = (IAudioSessionManager2)managerObj;\n\n            Marshal.ThrowExceptionForHR(manager.GetSessionEnumerator(out sessions));\n            int count;\n            Marshal.ThrowExceptionForHR(sessions.GetCount(out count));\n\n            float bestPeak = 0f;\n\n            for (int i = 0; i < count; i++) {\n                IAudioSessionControl session = null;\n                try {\n                    Marshal.ThrowExceptionForHR(sessions.GetSession(i, out session));\n                    var session2 = session as IAudioSessionControl2;\n                    if (session2 == null) {\n                        continue;\n                    }\n\n                    uint pid;\n                    if (session2.GetProcessId(out pid) != 0 || pid == 0) {\n                        continue;\n                    }\n\n                    Process process;\n                    try {\n                        process = Process.GetProcessById((int)pid);\n                    } catch {\n                        continue;\n                    }\n\n                    string processName = process.ProcessName ?? string.Empty;\n                    string sessionDisplayName = string.Empty;\n                    string sessionIdentifier = string.Empty;\n                    try { session.GetDisplayName(out sessionDisplayName); } catch {}\n                    try { session2.GetSessionIdentifier(out sessionIdentifier); } catch {}\n\n                    bool isSpotifySession =\n                        processName.IndexOf(\"spotify\", StringComparison.OrdinalIgnoreCase) >= 0 ||\n                        sessionDisplayName.IndexOf(\"spotify\", StringComparison.OrdinalIgnoreCase) >= 0 ||\n                        sessionIdentifier.IndexOf(\"spotify\", StringComparison.OrdinalIgnoreCase) >= 0;\n\n                    if (!isSpotifySession) {\n                        continue;\n                    }\n\n                    var meter = session as IAudioMeterInformation;\n                    if (meter == null) {\n                        continue;\n                    }\n\n                    float peak;\n                    if (meter.GetPeakValue(out peak) == 0 && peak > bestPeak) {\n                        bestPeak = peak;\n                    }\n                } catch {\n                } finally {\n                    Release(session);\n                }\n            }\n\n            return bestPeak;\n        } catch {\n            return 0f;\n        } finally {\n            Release(sessions);\n            Release(managerObj);\n            Release(device);\n            Release(enumerator);\n        }\n    }\n\n    private static void Release(object obj) {\n        try {\n            if (obj != null && Marshal.IsComObject(obj)) {\n                Marshal.ReleaseComObject(obj);\n            }\n        } catch {\n        }\n    }\n}\n'@\n\nwhile ($true) {\n    try {\n        $peak = [SpotifyAudioMeter]::GetSpotifyPeak()\n        [Console]::WriteLine($peak.ToString([System.Globalization.CultureInfo]::InvariantCulture))\n    } catch {\n        [Console]::WriteLine(\"0\")\n    }\n    Start-Sleep -Milliseconds 125\n}";
    }
}

