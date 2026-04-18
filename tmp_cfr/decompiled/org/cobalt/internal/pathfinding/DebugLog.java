/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.pathfinding;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J%\u0010\n\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u0006\u00a2\u0006\u0004\b\n\u0010\u000bJ/\u0010\u000e\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\r\u001a\u00020\f\u00a2\u0006\u0004\b\u000e\u0010\u000fJ-\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0012\u0010\u0013J-\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0010\u00a2\u0006\u0004\b\u0014\u0010\u0013J\u001d\u0010\u0015\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0015\u0010\u0016J)\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0004\b\u0017\u0010\u000bJ1\u0010\u0018\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\b\u0010\u0011\u001a\u0004\u0018\u00010\u0010H\u0002\u00a2\u0006\u0004\b\u0018\u0010\u0019R\"\u0010\u001a\u001a\u00020\f8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\"\u0010 \u001a\u00020\f8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b \u0010\u001b\u001a\u0004\b!\u0010\u001d\"\u0004\b\"\u0010\u001fR\"\u0010#\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b#\u0010$\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R\"\u0010*\u001a\u00020)8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b*\u0010+\u001a\u0004\b,\u0010-\"\u0004\b.\u0010/R\"\u00100\u001a\u00020\u00068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b0\u0010$\u001a\u0004\b1\u0010&\"\u0004\b2\u0010(R\"\u00103\u001a\u00020\f8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b3\u0010\u001b\u001a\u0004\b4\u0010\u001d\"\u0004\b5\u0010\u001fR\"\u00106\u001a\u00020\f8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b6\u0010\u001b\u001a\u0004\b7\u0010\u001d\"\u0004\b8\u0010\u001fR0\u0010;\u001a\u001e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u001009j\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0010`:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b;\u0010<R0\u0010>\u001a\u001e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020=09j\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020=`:8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b>\u0010<R\u001c\u0010A\u001a\n @*\u0004\u0018\u00010?0?8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bA\u0010B\u00a8\u0006C"}, d2={"Lorg/cobalt/internal/pathfinding/DebugLog;", "", "<init>", "()V", "Lnet/minecraft/class_310;", "client", "", "module", "message", "", "status", "(Lnet/minecraft/class_310;Ljava/lang/String;Ljava/lang/String;)V", "", "overlay", "debug", "(Lnet/minecraft/class_310;Ljava/lang/String;Ljava/lang/String;Z)V", "", "gameTime", "debugTick", "(Lnet/minecraft/class_310;Ljava/lang/String;Ljava/lang/String;J)V", "debugTickFile", "startSession", "(Lnet/minecraft/class_310;Ljava/lang/String;)V", "endSession", "writeLine", "(Lnet/minecraft/class_310;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;)V", "statusChatEnabled", "Z", "getStatusChatEnabled", "()Z", "setStatusChatEnabled", "(Z)V", "debugChatEnabled", "getDebugChatEnabled", "setDebugChatEnabled", "tag", "Ljava/lang/String;", "getTag", "()Ljava/lang/String;", "setTag", "(Ljava/lang/String;)V", "", "tickChatInterval", "I", "getTickChatInterval", "()I", "setTickChatInterval", "(I)V", "debugFileName", "getDebugFileName", "setDebugFileName", "statusFileEnabled", "getStatusFileEnabled", "setStatusFileEnabled", "debugFileEnabled", "getDebugFileEnabled", "setDebugFileEnabled", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "lastChatTickByModule", "Ljava/util/HashMap;", "Ljava/io/File;", "sessionFileByModule", "Ljava/time/format/DateTimeFormatter;", "kotlin.jvm.PlatformType", "timeFormatter", "Ljava/time/format/DateTimeFormatter;", "cobalt"})
public final class DebugLog {
    @NotNull
    public static final DebugLog INSTANCE = new DebugLog();
    private static boolean statusChatEnabled = true;
    private static boolean debugChatEnabled;
    @NotNull
    private static String tag;
    private static int tickChatInterval;
    @NotNull
    private static String debugFileName;
    private static boolean statusFileEnabled;
    private static boolean debugFileEnabled;
    @NotNull
    private static final HashMap<String, Long> lastChatTickByModule;
    @NotNull
    private static final HashMap<String, File> sessionFileByModule;
    private static final DateTimeFormatter timeFormatter;

    private DebugLog() {
    }

    public final boolean getStatusChatEnabled() {
        return statusChatEnabled;
    }

    public final void setStatusChatEnabled(boolean bl) {
        statusChatEnabled = bl;
    }

    public final boolean getDebugChatEnabled() {
        return debugChatEnabled;
    }

    public final void setDebugChatEnabled(boolean bl) {
        debugChatEnabled = bl;
    }

    @NotNull
    public final String getTag() {
        return tag;
    }

    public final void setTag(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        tag = string;
    }

    public final int getTickChatInterval() {
        return tickChatInterval;
    }

    public final void setTickChatInterval(int n) {
        tickChatInterval = n;
    }

    @NotNull
    public final String getDebugFileName() {
        return debugFileName;
    }

    public final void setDebugFileName(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        debugFileName = string;
    }

    public final boolean getStatusFileEnabled() {
        return statusFileEnabled;
    }

    public final void setStatusFileEnabled(boolean bl) {
        statusFileEnabled = bl;
    }

    public final boolean getDebugFileEnabled() {
        return debugFileEnabled;
    }

    public final void setDebugFileEnabled(boolean bl) {
        debugFileEnabled = bl;
    }

    public final void status(@NotNull class_310 client, @NotNull String module, @NotNull String message) {
        block2: {
            Intrinsics.checkNotNullParameter((Object)client, (String)"client");
            Intrinsics.checkNotNullParameter((Object)module, (String)"module");
            Intrinsics.checkNotNullParameter((Object)message, (String)"message");
            if (statusFileEnabled && debugFileEnabled) {
                this.writeLine(client, module, message, null);
            }
            if (!statusChatEnabled) break block2;
            class_746 class_7462 = client.field_1724;
            if (class_7462 != null) {
                class_7462.method_7353((class_2561)class_2561.method_43470((String)(tag + "[" + module + "] " + message)), false);
            }
        }
    }

    public final void debug(@NotNull class_310 client, @NotNull String module, @NotNull String message, boolean overlay) {
        block1: {
            Intrinsics.checkNotNullParameter((Object)client, (String)"client");
            Intrinsics.checkNotNullParameter((Object)module, (String)"module");
            Intrinsics.checkNotNullParameter((Object)message, (String)"message");
            if (!debugChatEnabled) {
                return;
            }
            class_746 class_7462 = client.field_1724;
            if (class_7462 == null) break block1;
            class_7462.method_7353((class_2561)class_2561.method_43470((String)(tag + "[" + module + "] " + message)), overlay);
        }
    }

    public static /* synthetic */ void debug$default(DebugLog debugLog, class_310 class_3102, String string, String string2, boolean bl, int n, Object object) {
        if ((n & 8) != 0) {
            bl = true;
        }
        debugLog.debug(class_3102, string, string2, bl);
    }

    public final void debugTick(@NotNull class_310 client, @NotNull String module, @NotNull String message, long gameTime) {
        block1: {
            long lastTick;
            Intrinsics.checkNotNullParameter((Object)client, (String)"client");
            Intrinsics.checkNotNullParameter((Object)module, (String)"module");
            Intrinsics.checkNotNullParameter((Object)message, (String)"message");
            this.writeLine(client, module, message, gameTime);
            Long l = lastChatTickByModule.get(module);
            long l2 = lastTick = l != null ? l : Long.MIN_VALUE;
            if (tickChatInterval <= 0 || gameTime - lastTick < (long)tickChatInterval) break block1;
            ((Map)lastChatTickByModule).put(module, gameTime);
            class_746 class_7462 = client.field_1724;
            if (class_7462 != null) {
                class_7462.method_7353((class_2561)class_2561.method_43470((String)(tag + "[" + module + "] " + message)), false);
            }
        }
    }

    public final void debugTickFile(@NotNull class_310 client, @NotNull String module, @NotNull String message, long gameTime) {
        Intrinsics.checkNotNullParameter((Object)client, (String)"client");
        Intrinsics.checkNotNullParameter((Object)module, (String)"module");
        Intrinsics.checkNotNullParameter((Object)message, (String)"message");
        if (!debugFileEnabled) {
            return;
        }
        this.writeLine(client, module, message, gameTime);
    }

    public final void startSession(@NotNull class_310 client, @NotNull String module) {
        Intrinsics.checkNotNullParameter((Object)client, (String)"client");
        Intrinsics.checkNotNullParameter((Object)module, (String)"module");
        if (!debugFileEnabled) {
            return;
        }
        File file = client.field_1697;
        if (file == null) {
            return;
        }
        File dir = file;
        String stamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.systemDefault()).format(Instant.now());
        File file2 = new File(dir, "dutt-debug-" + module + "-" + stamp + ".txt");
        ((Map)sessionFileByModule).put(module, file2);
        this.writeLine(client, module, "Session start", null);
    }

    public final void endSession(@NotNull class_310 client, @NotNull String module, @Nullable String message) {
        Intrinsics.checkNotNullParameter((Object)client, (String)"client");
        Intrinsics.checkNotNullParameter((Object)module, (String)"module");
        if (!debugFileEnabled) {
            sessionFileByModule.remove(module);
            return;
        }
        if (message != null) {
            this.writeLine(client, module, message, null);
        }
        this.writeLine(client, module, "Session end", null);
        sessionFileByModule.remove(module);
    }

    public static /* synthetic */ void endSession$default(DebugLog debugLog, class_310 class_3102, String string, String string2, int n, Object object) {
        if ((n & 4) != 0) {
            string2 = null;
        }
        debugLog.endSession(class_3102, string, string2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void writeLine(class_310 client, String module, String message, Long gameTime) {
        File file = client.field_1697;
        if (file == null) {
            return;
        }
        File dir = file;
        File file2 = sessionFileByModule.get(module);
        if (file2 == null) {
            file2 = new File(dir, debugFileName);
        }
        File file3 = file2;
        String time = timeFormatter.format(Instant.now());
        Long l = gameTime;
        Object tickText = l != null ? " t=" + l : "";
        try {
            Closeable closeable = new FileWriter(file3, true);
            Throwable throwable = null;
            try {
                FileWriter writer = (FileWriter)closeable;
                boolean bl = false;
                writer.append(time);
                writer.append((CharSequence)tickText);
                writer.append(" ");
                writer.append(tag);
                writer.append("[");
                writer.append(module);
                writer.append("] ");
                writer.append(message);
                Writer writer2 = writer.append("\n");
            }
            catch (Throwable throwable2) {
                throwable = throwable2;
                throw throwable2;
            }
            finally {
                CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static {
        tag = "[Dutt]";
        tickChatInterval = 20;
        debugFileName = "dutt-debug.txt";
        statusFileEnabled = true;
        lastChatTickByModule = new HashMap();
        sessionFileByModule = new HashMap();
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    }
}

