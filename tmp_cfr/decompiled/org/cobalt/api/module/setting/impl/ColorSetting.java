/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.module.setting.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.impl.ColorMode;
import org.cobalt.api.module.setting.impl.ThemeColorResolver;
import org.cobalt.api.ui.theme.Theme;
import org.cobalt.api.ui.theme.ThemeManager;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001f\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0002H\u0002\u00a2\u0006\u0004\b\t\u0010\nJ7\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\fH\u0002\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0017\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u0013H\u0016\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u000f\u0010\u0018\u001a\u00020\u0013H\u0016\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u001a\u0010\u0006\u001a\u00020\u00028\u0016X\u0096\u0004\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001a\u001a\u0004\b\u001b\u0010\u001cR\"\u0010\u001e\u001a\u00020\u001d8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001e\u0010\u001f\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u0014\u0010%\u001a\u00020$8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b%\u0010&R$\u0010+\u001a\u00020\u00022\u0006\u0010'\u001a\u00020\u00028V@VX\u0096\u000e\u00a2\u0006\f\u001a\u0004\b(\u0010\u001c\"\u0004\b)\u0010*\u00a8\u0006,"}, d2={"Lorg/cobalt/api/module/setting/impl/ColorSetting;", "Lorg/cobalt/api/module/setting/Setting;", "", "", "name", "description", "defaultValue", "<init>", "(Ljava/lang/String;Ljava/lang/String;I)V", "resolveColor", "()I", "argb", "", "hueShift", "saturationMult", "brightnessMult", "opacityMult", "tweakColor", "(IFFFF)I", "Lcom/google/gson/JsonElement;", "element", "", "read", "(Lcom/google/gson/JsonElement;)V", "write", "()Lcom/google/gson/JsonElement;", "I", "getDefaultValue", "()Ljava/lang/Integer;", "Lorg/cobalt/api/module/setting/impl/ColorMode;", "mode", "Lorg/cobalt/api/module/setting/impl/ColorMode;", "getMode", "()Lorg/cobalt/api/module/setting/impl/ColorMode;", "setMode", "(Lorg/cobalt/api/module/setting/impl/ColorMode;)V", "", "instanceStartTime", "J", "newValue", "getValue", "setValue", "(I)V", "value", "cobalt"})
public final class ColorSetting
extends Setting<Integer> {
    private final int defaultValue;
    @NotNull
    private ColorMode mode;
    private final long instanceStartTime;

    public ColorSetting(@NotNull String name, @NotNull String description, int defaultValue) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        super(name, description, defaultValue);
        this.defaultValue = defaultValue;
        this.mode = new ColorMode.Static(defaultValue);
        this.instanceStartTime = System.currentTimeMillis();
    }

    @Override
    @NotNull
    public Integer getDefaultValue() {
        return this.defaultValue;
    }

    @NotNull
    public final ColorMode getMode() {
        return this.mode;
    }

    public final void setMode(@NotNull ColorMode colorMode) {
        Intrinsics.checkNotNullParameter((Object)colorMode, (String)"<set-?>");
        this.mode = colorMode;
    }

    @Override
    @NotNull
    public Integer getValue() {
        return this.resolveColor();
    }

    @Override
    public void setValue(int newValue) {
        this.mode = new ColorMode.Static(newValue);
        super.setValue(newValue);
    }

    private final int resolveColor() {
        int n;
        ColorMode m = this.mode;
        if (m instanceof ColorMode.Static) {
            n = ((ColorMode.Static)m).getArgb();
        } else if (m instanceof ColorMode.Rainbow) {
            double elapsed = (double)(System.currentTimeMillis() - this.instanceStartTime) / 1000.0;
            float hue = (float)(elapsed * (double)((ColorMode.Rainbow)m).getSpeed() % 1.0 + 1.0) % 1.0f;
            int rgb = Color.HSBtoRGB(hue, ((ColorMode.Rainbow)m).getSaturation(), ((ColorMode.Rainbow)m).getBrightness());
            int alpha = RangesKt.coerceIn((int)((int)(((ColorMode.Rainbow)m).getOpacity() * (float)255)), (int)0, (int)255);
            n = alpha << 24 | rgb & 0xFFFFFF;
        } else if (m instanceof ColorMode.SyncedRainbow) {
            Theme theme = ThemeManager.INSTANCE.getCurrentTheme();
            float hue = ThemeManager.getRainbowHue$default(ThemeManager.INSTANCE, 0.0f, 1, null);
            float sat = theme.getRainbowSaturation();
            float bri = theme.getRainbowBrightness();
            int rgb = Color.HSBtoRGB(hue, sat, bri);
            int alpha = RangesKt.coerceIn((int)((int)(((ColorMode.SyncedRainbow)m).getOpacity() * (float)255)), (int)0, (int)255);
            n = alpha << 24 | rgb & 0xFFFFFF;
        } else if (m instanceof ColorMode.ThemeColor) {
            n = ThemeColorResolver.INSTANCE.resolve(((ColorMode.ThemeColor)m).getPropertyName());
        } else if (m instanceof ColorMode.TweakedTheme) {
            int baseColor = ThemeColorResolver.INSTANCE.resolve(((ColorMode.TweakedTheme)m).getPropertyName());
            n = this.tweakColor(baseColor, ((ColorMode.TweakedTheme)m).getHueOffset(), ((ColorMode.TweakedTheme)m).getSaturationMultiplier(), ((ColorMode.TweakedTheme)m).getBrightnessMultiplier(), ((ColorMode.TweakedTheme)m).getOpacityMultiplier());
        } else {
            throw new NoWhenBranchMatchedException();
        }
        return n;
    }

    private final int tweakColor(int argb, float hueShift, float saturationMult, float brightnessMult, float opacityMult) {
        Color color = new Color(argb, true);
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float newHue = (hsb[0] + hueShift + 1.0f) % 1.0f;
        float newSat = RangesKt.coerceIn((float)(hsb[1] * saturationMult), (float)0.0f, (float)1.0f);
        float newBri = RangesKt.coerceIn((float)(hsb[2] * brightnessMult), (float)0.0f, (float)1.0f);
        float newAlpha = RangesKt.coerceIn((float)((float)color.getAlpha() / 255.0f * opacityMult), (float)0.0f, (float)1.0f);
        int rgb = Color.HSBtoRGB(newHue, newSat, newBri);
        int alpha = RangesKt.coerceIn((int)((int)(newAlpha * (float)255)), (int)0, (int)255);
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    @Override
    public void read(@NotNull JsonElement element) {
        block23: {
            Object modeType;
            block22: {
                Intrinsics.checkNotNullParameter((Object)element, (String)"element");
                if (!element.isJsonPrimitive()) break block22;
                int argb = element.getAsInt();
                this.mode = new ColorMode.Static(argb);
                super.setValue(argb);
                break block23;
            }
            if (!element.isJsonObject()) break block23;
            JsonObject obj = element.getAsJsonObject();
            Object object = obj.get("mode");
            if (object == null || (object = object.getAsString()) == null) {
                object = "static";
            }
            this.mode = switch (modeType = object) {
                case "static" -> {
                    JsonElement v1 = obj.get("argb");
                    int argb = v1 != null ? v1.getAsInt() : ((Number)super.getValue()).intValue();
                    super.setValue(argb);
                    yield new ColorMode.Static(argb);
                }
                case "rainbow" -> {
                    JsonElement v3 = obj.get("speed");
                    JsonElement v4 = obj.get("saturation");
                    JsonElement v5 = obj.get("brightness");
                    JsonElement v6 = obj.get("opacity");
                    yield new ColorMode.Rainbow(v3 != null ? v3.getAsFloat() : 1.0f, v4 != null ? v4.getAsFloat() : 1.0f, v5 != null ? v5.getAsFloat() : 1.0f, v6 != null ? v6.getAsFloat() : 1.0f);
                }
                case "synced_rainbow" -> {
                    JsonElement v7 = obj.get("speed");
                    JsonElement v8 = obj.get("saturation");
                    JsonElement v9 = obj.get("brightness");
                    JsonElement v10 = obj.get("opacity");
                    yield new ColorMode.SyncedRainbow(v7 != null ? v7.getAsFloat() : 1.0f, v8 != null ? v8.getAsFloat() : 1.0f, v9 != null ? v9.getAsFloat() : 1.0f, v10 != null ? v10.getAsFloat() : 1.0f);
                }
                case "theme" -> {
                    Object v11 = obj.get("propertyName");
                    if (v11 == null || (v11 = v11.getAsString()) == null) {
                        v11 = "accent";
                    }
                    yield new ColorMode.ThemeColor((String)v11);
                }
                case "tweaked_theme" -> {
                    Object v12 = obj.get("propertyName");
                    if (v12 == null || (v12 = v12.getAsString()) == null) {
                        v12 = "accent";
                    }
                    JsonElement v13 = obj.get("hueOffset");
                    JsonElement v14 = obj.get("saturationMultiplier");
                    JsonElement v15 = obj.get("brightnessMultiplier");
                    JsonElement v16 = obj.get("opacityMultiplier");
                    yield new ColorMode.TweakedTheme((String)v12, v13 != null ? v13.getAsFloat() : 0.0f, v14 != null ? v14.getAsFloat() : 1.0f, v15 != null ? v15.getAsFloat() : 1.0f, v16 != null ? v16.getAsFloat() : 1.0f);
                }
                default -> {
                    int argb = ((Number)super.getValue()).intValue();
                    yield new ColorMode.Static(argb);
                }
            };
        }
    }

    @Override
    @NotNull
    public JsonElement write() {
        JsonElement jsonElement;
        ColorMode m = this.mode;
        if (m instanceof ColorMode.Static) {
            jsonElement = (JsonElement)new JsonPrimitive((Number)((ColorMode.Static)m).getArgb());
        } else if (m instanceof ColorMode.Rainbow) {
            JsonObject jsonObject;
            JsonObject $this$write_u24lambda_u240 = jsonObject = new JsonObject();
            boolean bl = false;
            $this$write_u24lambda_u240.addProperty("mode", "rainbow");
            $this$write_u24lambda_u240.addProperty("speed", (Number)Float.valueOf(((ColorMode.Rainbow)m).getSpeed()));
            $this$write_u24lambda_u240.addProperty("saturation", (Number)Float.valueOf(((ColorMode.Rainbow)m).getSaturation()));
            $this$write_u24lambda_u240.addProperty("brightness", (Number)Float.valueOf(((ColorMode.Rainbow)m).getBrightness()));
            $this$write_u24lambda_u240.addProperty("opacity", (Number)Float.valueOf(((ColorMode.Rainbow)m).getOpacity()));
            jsonElement = (JsonElement)jsonObject;
        } else if (m instanceof ColorMode.SyncedRainbow) {
            JsonObject jsonObject;
            JsonObject $this$write_u24lambda_u241 = jsonObject = new JsonObject();
            boolean bl = false;
            $this$write_u24lambda_u241.addProperty("mode", "synced_rainbow");
            $this$write_u24lambda_u241.addProperty("speed", (Number)Float.valueOf(((ColorMode.SyncedRainbow)m).getSpeed()));
            $this$write_u24lambda_u241.addProperty("saturation", (Number)Float.valueOf(((ColorMode.SyncedRainbow)m).getSaturation()));
            $this$write_u24lambda_u241.addProperty("brightness", (Number)Float.valueOf(((ColorMode.SyncedRainbow)m).getBrightness()));
            $this$write_u24lambda_u241.addProperty("opacity", (Number)Float.valueOf(((ColorMode.SyncedRainbow)m).getOpacity()));
            jsonElement = (JsonElement)jsonObject;
        } else if (m instanceof ColorMode.ThemeColor) {
            JsonObject jsonObject;
            JsonObject $this$write_u24lambda_u242 = jsonObject = new JsonObject();
            boolean bl = false;
            $this$write_u24lambda_u242.addProperty("mode", "theme");
            $this$write_u24lambda_u242.addProperty("propertyName", ((ColorMode.ThemeColor)m).getPropertyName());
            jsonElement = (JsonElement)jsonObject;
        } else if (m instanceof ColorMode.TweakedTheme) {
            JsonObject jsonObject;
            JsonObject $this$write_u24lambda_u243 = jsonObject = new JsonObject();
            boolean bl = false;
            $this$write_u24lambda_u243.addProperty("mode", "tweaked_theme");
            $this$write_u24lambda_u243.addProperty("propertyName", ((ColorMode.TweakedTheme)m).getPropertyName());
            $this$write_u24lambda_u243.addProperty("hueOffset", (Number)Float.valueOf(((ColorMode.TweakedTheme)m).getHueOffset()));
            $this$write_u24lambda_u243.addProperty("saturationMultiplier", (Number)Float.valueOf(((ColorMode.TweakedTheme)m).getSaturationMultiplier()));
            $this$write_u24lambda_u243.addProperty("brightnessMultiplier", (Number)Float.valueOf(((ColorMode.TweakedTheme)m).getBrightnessMultiplier()));
            $this$write_u24lambda_u243.addProperty("opacityMultiplier", (Number)Float.valueOf(((ColorMode.TweakedTheme)m).getOpacityMultiplier()));
            jsonElement = (JsonElement)jsonObject;
        } else {
            throw new NoWhenBranchMatchedException();
        }
        return jsonElement;
    }
}

