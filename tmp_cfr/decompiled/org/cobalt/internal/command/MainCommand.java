/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.random.Random
 *  kotlin.ranges.RangesKt
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.command;

import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.cobalt.api.command.Command;
import org.cobalt.api.command.annotation.DefaultHandler;
import org.cobalt.api.command.annotation.SubCommand;
import org.cobalt.api.notification.NotificationManager;
import org.cobalt.api.rotation.EasingType;
import org.cobalt.api.rotation.RotationExecutor;
import org.cobalt.api.rotation.strategy.TimedEaseStrategy;
import org.cobalt.api.util.ChatUtils;
import org.cobalt.api.util.helper.Rotation;
import org.cobalt.internal.pathfinding.PathfindingModule;
import org.cobalt.internal.stats.MacroTimeTracker;
import org.cobalt.internal.ui.screen.UIConfig;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0003J'\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\tH\u0007\u00a2\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\u000b\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u000b\u0010\u0003J'\u0010\u0010\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u000f\u0010\u0012\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0012\u0010\u0003J\u000f\u0010\u0013\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0013\u0010\u0003J\u000f\u0010\u0014\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0014\u0010\u0003J'\u0010\u0015\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0006H\u0007\u00a2\u0006\u0004\b\u0015\u0010\u0011J\u000f\u0010\u0016\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u0016\u0010\u0003J\u001f\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u0017H\u0007\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u000f\u0010\u001c\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u001c\u0010\u0003J\u000f\u0010\u001d\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u001d\u0010\u0003J\u000f\u0010\u001e\u001a\u00020\u0004H\u0007\u00a2\u0006\u0004\b\u001e\u0010\u0003J%\u0010\"\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u00172\f\u0010!\u001a\b\u0012\u0004\u0012\u00020 0\u001fH\u0002\u00a2\u0006\u0004\b\"\u0010#J\u0017\u0010&\u001a\u00020\u00172\u0006\u0010%\u001a\u00020$H\u0002\u00a2\u0006\u0004\b&\u0010'\u00a8\u0006("}, d2={"Lorg/cobalt/internal/command/MainCommand;", "Lorg/cobalt/api/command/Command;", "<init>", "()V", "", "main", "", "yaw", "pitch", "", "duration", "rotate", "(DDI)V", "x", "y", "z", "start", "(DDD)V", "stop", "pathstart", "pathstop", "setpos", "setposhere", "", "title", "description", "notification", "(Ljava/lang/String;Ljava/lang/String;)V", "today", "tdd", "entityscan", "", "Lorg/cobalt/internal/stats/MacroTimeTracker$MacroDuration;", "durations", "emitBreakdown", "(Ljava/lang/String;Ljava/util/List;)V", "", "ms", "formatDuration", "(J)Ljava/lang/String;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nMainCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MainCommand.kt\norg/cobalt/internal/command/MainCommand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,158:1\n1915#2,2:159\n*S KotlinDebug\n*F\n+ 1 MainCommand.kt\norg/cobalt/internal/command/MainCommand\n*L\n141#1:159,2\n*E\n"})
public final class MainCommand
extends Command {
    @NotNull
    public static final MainCommand INSTANCE = new MainCommand();

    private MainCommand() {
        String[] stringArray = new String[]{"cobalt", "cb"};
        super("dutt", stringArray);
    }

    @DefaultHandler
    public final void main() {
        UIConfig.INSTANCE.openUI();
    }

    @SubCommand
    public final void rotate(double yaw, double pitch, int duration) {
        RotationExecutor.INSTANCE.rotateTo(new Rotation((float)yaw, (float)pitch), new TimedEaseStrategy(EasingType.EASE_OUT_EXPO, EasingType.EASE_OUT_EXPO, duration));
    }

    @SubCommand
    public final void rotate() {
        float yaw = Random.Default.nextFloat() * 360.0f - 180.0f;
        float pitch = Random.Default.nextFloat() * 180.0f - 90.0f;
        RotationExecutor.INSTANCE.rotateTo(new Rotation(yaw, pitch), new TimedEaseStrategy(EasingType.EASE_OUT_EXPO, EasingType.EASE_OUT_EXPO, 400L));
    }

    @SubCommand
    public final void start(double x, double y, double z) {
        PathfindingModule.INSTANCE.startTo(x, y, z);
    }

    @SubCommand
    public final void stop() {
        PathfindingModule.INSTANCE.stopPath();
    }

    @SubCommand
    public final void pathstart() {
        PathfindingModule.INSTANCE.startFromSettings();
    }

    @SubCommand
    public final void pathstop() {
        PathfindingModule.INSTANCE.stopPath();
    }

    @SubCommand
    public final void setpos(double x, double y, double z) {
        PathfindingModule.INSTANCE.setTargetOnly(x, y, z);
    }

    @SubCommand
    public final void setposhere() {
        PathfindingModule.INSTANCE.setTargetAtPlayer();
    }

    @SubCommand
    public final void notification(@NotNull String title, @NotNull String description) {
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        NotificationManager.INSTANCE.queue(title, description, 2000L);
    }

    @SubCommand
    public final void today() {
        MacroTimeTracker.Snapshot snapshot = MacroTimeTracker.INSTANCE.snapshot();
        ChatUtils.sendMessage("Today: " + this.formatDuration(snapshot.getTodayTotalMs()));
    }

    @SubCommand
    public final void tdd() {
        MacroTimeTracker.Snapshot snapshot = MacroTimeTracker.INSTANCE.snapshot();
        ChatUtils.sendMessage("Macroed Today: " + this.formatDuration(snapshot.getTodayTotalMs()));
        ChatUtils.sendMessage("Macroed Lifetime: " + this.formatDuration(snapshot.getLifetimeTotalMs()));
        this.emitBreakdown("Today Macros", snapshot.getTodayByMacro());
        this.emitBreakdown("Lifetime Macros", snapshot.getLifetimeByMacro());
    }

    @SubCommand
    public final void entityscan() {
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        class_746 player = mc.field_1724;
        class_638 level2 = mc.field_1687;
        if (player == null || level2 == null) {
            ChatUtils.sendMessage("No world loaded.");
            return;
        }
        double range = 5.0;
        double rangeSq = range * range;
        int count = 0;
        for (Object t : level2.method_18112()) {
            String name;
            double dz;
            double dy;
            double dx;
            double distSq;
            Intrinsics.checkNotNullExpressionValue(t, (String)"next(...)");
            class_1297 entity = (class_1297)t;
            if (Intrinsics.areEqual((Object)entity, (Object)player) || (distSq = (dx = entity.method_23317() - player.method_23317()) * dx + (dy = entity.method_23318() - player.method_23318()) * dy + (dz = entity.method_23321() - player.method_23321()) * dz) > rangeSq) continue;
            ++count;
            Intrinsics.checkNotNullExpressionValue((Object)entity.method_5477().getString(), (String)"getString(...)");
            String string = entity.method_5864().method_5882();
            String string2 = "%.1f";
            Object[] objectArray = new Object[]{entity.method_23317()};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
            string2 = "%.1f";
            objectArray = new Object[]{entity.method_23318()};
            String string4 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"format(...)");
            string2 = "%.1f";
            objectArray = new Object[]{entity.method_23321()};
            String string5 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"format(...)");
            ChatUtils.sendMessage("[EntityScan] " + string + " \"" + name + "\" @ " + string3 + ", " + string4 + ", " + string5);
        }
        ChatUtils.sendMessage("[EntityScan] Found " + count + " entities within " + range + " blocks.");
    }

    private final void emitBreakdown(String title, List<MacroTimeTracker.MacroDuration> durations) {
        if (durations.isEmpty()) {
            ChatUtils.sendMessage(title + ": None");
            return;
        }
        ChatUtils.sendMessage(title);
        Iterable $this$forEach$iv = durations;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            MacroTimeTracker.MacroDuration duration = (MacroTimeTracker.MacroDuration)element$iv;
            boolean bl = false;
            ChatUtils.sendMessage(duration.getName() + ": " + INSTANCE.formatDuration(duration.getDurationMs()));
        }
    }

    private final String formatDuration(long ms) {
        String string;
        long totalSeconds = RangesKt.coerceAtLeast((long)(ms / 1000L), (long)0L);
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;
        if (hours > 0L) {
            String string2 = "%02d:%02d:%02d";
            Object[] objectArray = new Object[]{hours, minutes, seconds};
            String string3 = String.format(string2, Arrays.copyOf(objectArray, objectArray.length));
            string = string3;
            Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"format(...)");
        } else {
            String string4 = "%02d:%02d";
            Object[] objectArray = new Object[]{minutes, seconds};
            String string5 = String.format(string4, Arrays.copyOf(objectArray, objectArray.length));
            string = string5;
            Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"format(...)");
        }
        return string;
    }
}

