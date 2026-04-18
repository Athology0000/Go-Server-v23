/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.random.Random
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.rotation;

import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import org.cobalt.api.module.Module;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.InfoSetting;
import org.cobalt.api.module.setting.impl.InfoType;
import org.cobalt.api.module.setting.impl.ModeSetting;
import org.cobalt.api.module.setting.impl.RangeSetting;
import org.cobalt.api.module.setting.impl.SliderSetting;
import org.cobalt.internal.pathfinding.HeadRotationModule;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J!\u0010\u0007\u001a\u00020\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\n\u001a\u00020\t\u00a2\u0006\u0004\b\n\u0010\u000bR\u0014\u0010\r\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000eR\u0017\u0010\u0010\u001a\u00020\u000f8\u0006\u00a2\u0006\f\n\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0014\u001a\u00020\u000f8\u0006\u00a2\u0006\f\n\u0004\b\u0014\u0010\u0011\u001a\u0004\b\u0015\u0010\u0013R\u0017\u0010\u0016\u001a\u00020\u000f8\u0006\u00a2\u0006\f\n\u0004\b\u0016\u0010\u0011\u001a\u0004\b\u0017\u0010\u0013R\u0017\u0010\u0018\u001a\u00020\u000f8\u0006\u00a2\u0006\f\n\u0004\b\u0018\u0010\u0011\u001a\u0004\b\u0019\u0010\u0013R\u0014\u0010\u001a\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u000eR\u0017\u0010\u001c\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b\u001c\u0010\u001d\u001a\u0004\b\u001e\u0010\u001fR\u0017\u0010 \u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b \u0010\u001d\u001a\u0004\b!\u0010\u001fR\u0014\u0010\"\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\"\u0010\u000eR\u0017\u0010#\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b#\u0010\u001d\u001a\u0004\b$\u0010\u001fR\u0017\u0010%\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b%\u0010\u001d\u001a\u0004\b&\u0010\u001fR\u0014\u0010'\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b'\u0010\u000eR\u0017\u0010(\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b(\u0010\u001d\u001a\u0004\b)\u0010\u001fR\u0017\u0010*\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b*\u0010\u001d\u001a\u0004\b+\u0010\u001fR\u0017\u0010,\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b,\u0010\u001d\u001a\u0004\b-\u0010\u001fR\u0017\u0010.\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b.\u0010\u001d\u001a\u0004\b/\u0010\u001fR\u0017\u00100\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b0\u0010\u001d\u001a\u0004\b1\u0010\u001fR\u0017\u00103\u001a\u0002028\u0006\u00a2\u0006\f\n\u0004\b3\u00104\u001a\u0004\b5\u00106R\u0014\u00107\u001a\u00020\f8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b7\u0010\u000eR\u0017\u00108\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b8\u0010\u001d\u001a\u0004\b9\u0010\u001fR\u0017\u0010:\u001a\u00020\u001b8\u0006\u00a2\u0006\f\n\u0004\b:\u0010\u001d\u001a\u0004\b;\u0010\u001f\u00a8\u0006<"}, d2={"Lorg/cobalt/internal/rotation/RotationsModule;", "Lorg/cobalt/api/module/Module;", "<init>", "()V", "Lkotlin/Pair;", "", "range", "sample", "(Lkotlin/Pair;)D", "Lorg/cobalt/internal/pathfinding/HeadRotationModule$EaseMode;", "currentMiningEase", "()Lorg/cobalt/internal/pathfinding/HeadRotationModule$EaseMode;", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "headerBezier", "Lorg/cobalt/api/module/setting/impl/InfoSetting;", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "bezierCurveIn", "Lorg/cobalt/api/module/setting/impl/SliderSetting;", "getBezierCurveIn", "()Lorg/cobalt/api/module/setting/impl/SliderSetting;", "bezierCurveOut", "getBezierCurveOut", "bezierMinScale", "getBezierMinScale", "bezierSnapThreshold", "getBezierSnapThreshold", "headerCombat", "Lorg/cobalt/api/module/setting/impl/RangeSetting;", "combatYawStep", "Lorg/cobalt/api/module/setting/impl/RangeSetting;", "getCombatYawStep", "()Lorg/cobalt/api/module/setting/impl/RangeSetting;", "combatPitchStep", "getCombatPitchStep", "headerRoutes", "routeYawStep", "getRouteYawStep", "routePitchStep", "getRoutePitchStep", "headerMining", "miningMaxSpeed", "getMiningMaxSpeed", "miningMaxAccel", "getMiningMaxAccel", "miningSpeedScale", "getMiningSpeedScale", "miningAccelScale", "getMiningAccelScale", "miningPitchStep", "getMiningPitchStep", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "miningEasing", "Lorg/cobalt/api/module/setting/impl/ModeSetting;", "getMiningEasing", "()Lorg/cobalt/api/module/setting/impl/ModeSetting;", "headerWarp", "warpSpeedScale", "getWarpSpeedScale", "warpAccelScale", "getWarpAccelScale", "cobalt"})
public final class RotationsModule
extends Module {
    @NotNull
    public static final RotationsModule INSTANCE = new RotationsModule();
    @NotNull
    private static final InfoSetting headerBezier = new InfoSetting("Bezier Shape", "Easing curve shared by combat and route rotations.", InfoType.INFO);
    @NotNull
    private static final SliderSetting bezierCurveIn = new SliderSetting("Curve In", "Bezier start control point. Lower = slower start.", 0.2, 0.01, 0.49, 0.0, 32, null);
    @NotNull
    private static final SliderSetting bezierCurveOut = new SliderSetting("Curve Out", "Bezier end control point. Higher = smoother finish.", 0.94, 0.51, 0.99, 0.0, 32, null);
    @NotNull
    private static final SliderSetting bezierMinScale = new SliderSetting("Min Scale", "Minimum movement scale at small deltas (0-1).", 0.18, 0.05, 1.0, 0.0, 32, null);
    @NotNull
    private static final SliderSetting bezierSnapThreshold = new SliderSetting("Snap Threshold", "Degrees below which rotation snaps instantly. Set to 0 to disable.", 0.25, 0.0, 3.0, 0.0, 32, null);
    @NotNull
    private static final InfoSetting headerCombat = new InfoSetting("Combat Macro", "Rotation step ranges for the combat macro.", InfoType.INFO);
    @NotNull
    private static final RangeSetting combatYawStep = new RangeSetting("Combat Yaw Step", "Max yaw degrees per frame for combat.", (Pair<Double, Double>)new Pair((Object)8.0, (Object)12.0), 1.0, 30.0);
    @NotNull
    private static final RangeSetting combatPitchStep = new RangeSetting("Combat Pitch Step", "Max pitch degrees per frame for combat.", (Pair<Double, Double>)new Pair((Object)6.0, (Object)10.0), 1.0, 30.0);
    @NotNull
    private static final InfoSetting headerRoutes = new InfoSetting("Routes", "Rotation step ranges for route walking.", InfoType.INFO);
    @NotNull
    private static final RangeSetting routeYawStep = new RangeSetting("Route Yaw Step", "Max yaw degrees per frame for route walking.", (Pair<Double, Double>)new Pair((Object)7.0, (Object)11.0), 1.0, 30.0);
    @NotNull
    private static final RangeSetting routePitchStep = new RangeSetting("Route Pitch Step", "Max pitch degrees per frame for route walking.", (Pair<Double, Double>)new Pair((Object)5.0, (Object)9.0), 1.0, 30.0);
    @NotNull
    private static final InfoSetting headerMining = new InfoSetting("Mining Macro", "Head rotation settings for the mining macro.", InfoType.INFO);
    @NotNull
    private static final RangeSetting miningMaxSpeed = new RangeSetting("Mining Max Speed", "Max turn speed (deg/sec) while mining.", (Pair<Double, Double>)new Pair((Object)80.0, (Object)120.0), 10.0, 300.0);
    @NotNull
    private static final RangeSetting miningMaxAccel = new RangeSetting("Mining Max Accel", "Max turn acceleration (deg/sec^2) while mining.", (Pair<Double, Double>)new Pair((Object)180.0, (Object)260.0), 50.0, 600.0);
    @NotNull
    private static final RangeSetting miningSpeedScale = new RangeSetting("Mining Speed Scale", "Speed scale multiplier while mining.", (Pair<Double, Double>)new Pair((Object)0.7, (Object)1.0), 0.1, 2.0);
    @NotNull
    private static final RangeSetting miningAccelScale = new RangeSetting("Mining Accel Scale", "Accel scale multiplier while mining.", (Pair<Double, Double>)new Pair((Object)0.6, (Object)1.0), 0.1, 2.0);
    @NotNull
    private static final RangeSetting miningPitchStep = new RangeSetting("Mining Pitch Step", "Max pitch step per frame while mining.", (Pair<Double, Double>)new Pair((Object)2.5, (Object)4.5), 0.5, 15.0);
    @NotNull
    private static final ModeSetting miningEasing;
    @NotNull
    private static final InfoSetting headerWarp;
    @NotNull
    private static final RangeSetting warpSpeedScale;
    @NotNull
    private static final RangeSetting warpAccelScale;

    private RotationsModule() {
        super("Rotations");
    }

    @NotNull
    public final SliderSetting getBezierCurveIn() {
        return bezierCurveIn;
    }

    @NotNull
    public final SliderSetting getBezierCurveOut() {
        return bezierCurveOut;
    }

    @NotNull
    public final SliderSetting getBezierMinScale() {
        return bezierMinScale;
    }

    @NotNull
    public final SliderSetting getBezierSnapThreshold() {
        return bezierSnapThreshold;
    }

    @NotNull
    public final RangeSetting getCombatYawStep() {
        return combatYawStep;
    }

    @NotNull
    public final RangeSetting getCombatPitchStep() {
        return combatPitchStep;
    }

    @NotNull
    public final RangeSetting getRouteYawStep() {
        return routeYawStep;
    }

    @NotNull
    public final RangeSetting getRoutePitchStep() {
        return routePitchStep;
    }

    @NotNull
    public final RangeSetting getMiningMaxSpeed() {
        return miningMaxSpeed;
    }

    @NotNull
    public final RangeSetting getMiningMaxAccel() {
        return miningMaxAccel;
    }

    @NotNull
    public final RangeSetting getMiningSpeedScale() {
        return miningSpeedScale;
    }

    @NotNull
    public final RangeSetting getMiningAccelScale() {
        return miningAccelScale;
    }

    @NotNull
    public final RangeSetting getMiningPitchStep() {
        return miningPitchStep;
    }

    @NotNull
    public final ModeSetting getMiningEasing() {
        return miningEasing;
    }

    @NotNull
    public final RangeSetting getWarpSpeedScale() {
        return warpSpeedScale;
    }

    @NotNull
    public final RangeSetting getWarpAccelScale() {
        return warpAccelScale;
    }

    public final double sample(@NotNull Pair<Double, Double> range) {
        Intrinsics.checkNotNullParameter(range, (String)"range");
        return ((Number)range.getFirst()).doubleValue() >= ((Number)range.getSecond()).doubleValue() ? ((Number)range.getFirst()).doubleValue() : ((Number)range.getFirst()).doubleValue() + Random.Default.nextDouble() * (((Number)range.getSecond()).doubleValue() - ((Number)range.getFirst()).doubleValue());
    }

    @NotNull
    public final HeadRotationModule.EaseMode currentMiningEase() {
        return switch (((Number)miningEasing.getValue()).intValue()) {
            case 1 -> HeadRotationModule.EaseMode.SINE_OUT;
            case 2 -> HeadRotationModule.EaseMode.SINE_IN_OUT;
            case 3 -> HeadRotationModule.EaseMode.CUBIC_IN_OUT;
            case 4 -> HeadRotationModule.EaseMode.LINEAR;
            default -> HeadRotationModule.EaseMode.TANH;
        };
    }

    static {
        Object[] objectArray = new String[]{"Tanh (Default)", "Sine Out", "Sine In/Out", "Cubic In/Out", "Linear"};
        miningEasing = new ModeSetting("Mining Easing", "Easing curve used when the mining macro rotates the head toward the next block.", 0, (String[])objectArray);
        headerWarp = new InfoSetting("Warp", "Head rotation settings during etherwarp.", InfoType.INFO);
        warpSpeedScale = new RangeSetting("Warp Speed Scale", "Speed scale multiplier during warp aim.", (Pair<Double, Double>)new Pair((Object)0.8, (Object)1.2), 0.1, 2.0);
        warpAccelScale = new RangeSetting("Warp Accel Scale", "Accel scale multiplier during warp aim.", (Pair<Double, Double>)new Pair((Object)0.7, (Object)1.1), 0.1, 2.0);
        objectArray = new Setting[21];
        objectArray[0] = headerBezier;
        objectArray[1] = bezierCurveIn;
        objectArray[2] = bezierCurveOut;
        objectArray[3] = bezierMinScale;
        objectArray[4] = bezierSnapThreshold;
        objectArray[5] = headerCombat;
        objectArray[6] = combatYawStep;
        objectArray[7] = combatPitchStep;
        objectArray[8] = headerRoutes;
        objectArray[9] = routeYawStep;
        objectArray[10] = routePitchStep;
        objectArray[11] = headerMining;
        objectArray[12] = miningMaxSpeed;
        objectArray[13] = miningMaxAccel;
        objectArray[14] = miningSpeedScale;
        objectArray[15] = miningAccelScale;
        objectArray[16] = miningPitchStep;
        objectArray[17] = miningEasing;
        objectArray[18] = headerWarp;
        objectArray[19] = warpSpeedScale;
        objectArray[20] = warpAccelScale;
        INSTANCE.addSetting((Setting<?>[])objectArray);
    }
}

