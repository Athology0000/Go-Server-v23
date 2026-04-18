/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.ui.theme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Base64;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import org.cobalt.api.ui.theme.impl.CustomTheme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\b\u00c0\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0017\u0010\n\u001a\u0004\u0018\u00010\u00042\u0006\u0010\t\u001a\u00020\u0006\u00a2\u0006\u0004\b\n\u0010\u000bJ\u0015\u0010\r\u001a\u00020\f2\u0006\u0010\u0005\u001a\u00020\u0004\u00a2\u0006\u0004\b\r\u0010\u000eJ\u0015\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\f\u00a2\u0006\u0004\b\u0010\u0010\u0011R\u0014\u0010\u0012\u001a\u00020\u00068\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0014"}, d2={"Lorg/cobalt/internal/ui/theme/ThemeSerializer;", "", "<init>", "()V", "Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "theme", "", "toBase64", "(Lorg/cobalt/api/ui/theme/impl/CustomTheme;)Ljava/lang/String;", "data", "fromBase64", "(Ljava/lang/String;)Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "Lcom/google/gson/JsonObject;", "toJson", "(Lorg/cobalt/api/ui/theme/impl/CustomTheme;)Lcom/google/gson/JsonObject;", "json", "fromJson", "(Lcom/google/gson/JsonObject;)Lorg/cobalt/api/ui/theme/impl/CustomTheme;", "THEME_PREFIX", "Ljava/lang/String;", "cobalt"})
@SourceDebugExtension(value={"SMAP\nThemeSerializer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ThemeSerializer.kt\norg/cobalt/internal/ui/theme/ThemeSerializer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,153:1\n1#2:154\n*E\n"})
public final class ThemeSerializer {
    @NotNull
    public static final ThemeSerializer INSTANCE = new ThemeSerializer();
    @NotNull
    private static final String THEME_PREFIX = "COBALT_THEME:";

    private ThemeSerializer() {
    }

    @NotNull
    public final String toBase64(@NotNull CustomTheme theme) {
        Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
        String string = this.toJson(theme).toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String json = string;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] byArray = json.getBytes(Charsets.UTF_8);
        Intrinsics.checkNotNullExpressionValue((Object)byArray, (String)"getBytes(...)");
        String encoded = encoder.encodeToString(byArray);
        return THEME_PREFIX + encoded;
    }

    @Nullable
    public final CustomTheme fromBase64(@NotNull String data) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        Object object2 = this;
        try {
            ThemeSerializer $this$fromBase64_u24lambda_u240 = object2;
            boolean bl = false;
            if (!StringsKt.startsWith$default((String)data, (String)THEME_PREFIX, (boolean)false, (int)2, null)) {
                boolean $i$a$-require-ThemeSerializer$fromBase64$1$22 = false;
                String $i$a$-require-ThemeSerializer$fromBase64$1$22 = "Invalid theme data format";
                throw new IllegalArgumentException($i$a$-require-ThemeSerializer$fromBase64$1$22.toString());
            }
            String encoded = StringsKt.removePrefix((String)data, (CharSequence)THEME_PREFIX);
            byte[] decoded = Base64.getDecoder().decode(encoded);
            Intrinsics.checkNotNull((Object)decoded);
            JsonObject json = JsonParser.parseString((String)new String(decoded, Charsets.UTF_8)).getAsJsonObject();
            Intrinsics.checkNotNull((Object)json);
            object = Result.constructor-impl((Object)$this$fromBase64_u24lambda_u240.fromJson(json));
        }
        catch (Throwable throwable) {
            object = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)throwable));
        }
        object2 = object;
        return (CustomTheme)(Result.isFailure-impl((Object)object2) ? null : object2);
    }

    @NotNull
    public final JsonObject toJson(@NotNull CustomTheme theme) {
        JsonObject jsonObject;
        Intrinsics.checkNotNullParameter((Object)theme, (String)"theme");
        JsonObject $this$toJson_u24lambda_u240 = jsonObject = new JsonObject();
        boolean bl = false;
        $this$toJson_u24lambda_u240.addProperty("name", theme.getName());
        $this$toJson_u24lambda_u240.addProperty("rainbowEnabled", Boolean.valueOf(theme.getRainbowEnabled()));
        $this$toJson_u24lambda_u240.addProperty("rainbowSpeed", (Number)Float.valueOf(theme.getRainbowSpeed()));
        $this$toJson_u24lambda_u240.addProperty("rainbowSaturation", (Number)Float.valueOf(theme.getRainbowSaturation()));
        $this$toJson_u24lambda_u240.addProperty("rainbowBrightness", (Number)Float.valueOf(theme.getRainbowBrightness()));
        $this$toJson_u24lambda_u240.addProperty("background", (Number)theme.getBackground());
        $this$toJson_u24lambda_u240.addProperty("panel", (Number)theme.getPanel());
        $this$toJson_u24lambda_u240.addProperty("inset", (Number)theme.getInset());
        $this$toJson_u24lambda_u240.addProperty("overlay", (Number)theme.getOverlay());
        $this$toJson_u24lambda_u240.addProperty("text", (Number)theme.getText());
        $this$toJson_u24lambda_u240.addProperty("textPrimary", (Number)theme.getTextPrimary());
        $this$toJson_u24lambda_u240.addProperty("textSecondary", (Number)theme.getTextSecondary());
        $this$toJson_u24lambda_u240.addProperty("textDisabled", (Number)theme.getTextDisabled());
        $this$toJson_u24lambda_u240.addProperty("textPlaceholder", (Number)theme.getTextPlaceholder());
        $this$toJson_u24lambda_u240.addProperty("textOnAccent", (Number)theme.getTextOnAccent());
        $this$toJson_u24lambda_u240.addProperty("accent", (Number)theme.getAccent());
        $this$toJson_u24lambda_u240.addProperty("accentPrimary", (Number)theme.getAccentPrimary());
        $this$toJson_u24lambda_u240.addProperty("accentSecondary", (Number)theme.getAccentSecondary());
        $this$toJson_u24lambda_u240.addProperty("selection", (Number)theme.getSelection());
        $this$toJson_u24lambda_u240.addProperty("controlBg", (Number)theme.getControlBg());
        $this$toJson_u24lambda_u240.addProperty("controlBorder", (Number)theme.getControlBorder());
        $this$toJson_u24lambda_u240.addProperty("inputBg", (Number)theme.getInputBg());
        $this$toJson_u24lambda_u240.addProperty("inputBorder", (Number)theme.getInputBorder());
        $this$toJson_u24lambda_u240.addProperty("success", (Number)theme.getSuccess());
        $this$toJson_u24lambda_u240.addProperty("warning", (Number)theme.getWarning());
        $this$toJson_u24lambda_u240.addProperty("error", (Number)theme.getError());
        $this$toJson_u24lambda_u240.addProperty("info", (Number)theme.getInfo());
        $this$toJson_u24lambda_u240.addProperty("scrollbarThumb", (Number)theme.getScrollbarThumb());
        $this$toJson_u24lambda_u240.addProperty("scrollbarTrack", (Number)theme.getScrollbarTrack());
        $this$toJson_u24lambda_u240.addProperty("sliderTrack", (Number)theme.getSliderTrack());
        $this$toJson_u24lambda_u240.addProperty("sliderFill", (Number)theme.getSliderFill());
        $this$toJson_u24lambda_u240.addProperty("sliderThumb", (Number)theme.getSliderThumb());
        $this$toJson_u24lambda_u240.addProperty("tooltipBackground", (Number)theme.getTooltipBackground());
        $this$toJson_u24lambda_u240.addProperty("tooltipBorder", (Number)theme.getTooltipBorder());
        $this$toJson_u24lambda_u240.addProperty("tooltipText", (Number)theme.getTooltipText());
        $this$toJson_u24lambda_u240.addProperty("notificationBackground", (Number)theme.getNotificationBackground());
        $this$toJson_u24lambda_u240.addProperty("notificationBorder", (Number)theme.getNotificationBorder());
        $this$toJson_u24lambda_u240.addProperty("notificationText", (Number)theme.getNotificationText());
        $this$toJson_u24lambda_u240.addProperty("notificationTextSecondary", (Number)theme.getNotificationTextSecondary());
        $this$toJson_u24lambda_u240.addProperty("infoBackground", (Number)theme.getInfoBackground());
        $this$toJson_u24lambda_u240.addProperty("infoBorder", (Number)theme.getInfoBorder());
        $this$toJson_u24lambda_u240.addProperty("infoIcon", (Number)theme.getInfoIcon());
        $this$toJson_u24lambda_u240.addProperty("warningBackground", (Number)theme.getWarningBackground());
        $this$toJson_u24lambda_u240.addProperty("warningBorder", (Number)theme.getWarningBorder());
        $this$toJson_u24lambda_u240.addProperty("warningIcon", (Number)theme.getWarningIcon());
        $this$toJson_u24lambda_u240.addProperty("successBackground", (Number)theme.getSuccessBackground());
        $this$toJson_u24lambda_u240.addProperty("successBorder", (Number)theme.getSuccessBorder());
        $this$toJson_u24lambda_u240.addProperty("successIcon", (Number)theme.getSuccessIcon());
        $this$toJson_u24lambda_u240.addProperty("errorBackground", (Number)theme.getErrorBackground());
        $this$toJson_u24lambda_u240.addProperty("errorBorder", (Number)theme.getErrorBorder());
        $this$toJson_u24lambda_u240.addProperty("errorIcon", (Number)theme.getErrorIcon());
        $this$toJson_u24lambda_u240.addProperty("selectionText", (Number)theme.getSelectionText());
        $this$toJson_u24lambda_u240.addProperty("searchPlaceholderText", (Number)theme.getSearchPlaceholderText());
        $this$toJson_u24lambda_u240.addProperty("moduleDivider", (Number)theme.getModuleDivider());
        $this$toJson_u24lambda_u240.addProperty("selectedOverlay", (Number)theme.getSelectedOverlay());
        $this$toJson_u24lambda_u240.addProperty("white", (Number)theme.getWhite());
        $this$toJson_u24lambda_u240.addProperty("black", (Number)theme.getBlack());
        $this$toJson_u24lambda_u240.addProperty("transparent", (Number)theme.getTransparent());
        return jsonObject;
    }

    @NotNull
    public final CustomTheme fromJson(@NotNull JsonObject json) {
        Intrinsics.checkNotNullParameter((Object)json, (String)"json");
        CustomTheme defaults = new CustomTheme(null, false, 0.0f, 0.0f, 0.0f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0x3FFFFFF, null);
        Object object = json.get("name");
        if (object == null || (object = object.getAsString()) == null) {
            object = defaults.getName();
        }
        JsonElement jsonElement = json.get("rainbowEnabled");
        JsonElement jsonElement2 = json.get("rainbowSpeed");
        JsonElement jsonElement3 = json.get("rainbowSaturation");
        JsonElement jsonElement4 = json.get("rainbowBrightness");
        JsonElement jsonElement5 = json.get("background");
        JsonElement jsonElement6 = json.get("panel");
        JsonElement jsonElement7 = json.get("inset");
        JsonElement jsonElement8 = json.get("overlay");
        JsonElement jsonElement9 = json.get("text");
        JsonElement jsonElement10 = json.get("textPrimary");
        JsonElement jsonElement11 = json.get("textSecondary");
        JsonElement jsonElement12 = json.get("textDisabled");
        JsonElement jsonElement13 = json.get("textPlaceholder");
        JsonElement jsonElement14 = json.get("textOnAccent");
        JsonElement jsonElement15 = json.get("accent");
        JsonElement jsonElement16 = json.get("accentPrimary");
        JsonElement jsonElement17 = json.get("accentSecondary");
        JsonElement jsonElement18 = json.get("selection");
        JsonElement jsonElement19 = json.get("controlBg");
        JsonElement jsonElement20 = json.get("controlBorder");
        JsonElement jsonElement21 = json.get("inputBg");
        JsonElement jsonElement22 = json.get("inputBorder");
        JsonElement jsonElement23 = json.get("success");
        JsonElement jsonElement24 = json.get("warning");
        JsonElement jsonElement25 = json.get("error");
        JsonElement jsonElement26 = json.get("info");
        JsonElement jsonElement27 = json.get("scrollbarThumb");
        JsonElement jsonElement28 = json.get("scrollbarTrack");
        JsonElement jsonElement29 = json.get("sliderTrack");
        JsonElement jsonElement30 = json.get("sliderFill");
        JsonElement jsonElement31 = json.get("sliderThumb");
        JsonElement jsonElement32 = json.get("tooltipBackground");
        JsonElement jsonElement33 = json.get("tooltipBorder");
        JsonElement jsonElement34 = json.get("tooltipText");
        JsonElement jsonElement35 = json.get("notificationBackground");
        JsonElement jsonElement36 = json.get("notificationBorder");
        JsonElement jsonElement37 = json.get("notificationText");
        JsonElement jsonElement38 = json.get("notificationTextSecondary");
        JsonElement jsonElement39 = json.get("infoBackground");
        JsonElement jsonElement40 = json.get("infoBorder");
        JsonElement jsonElement41 = json.get("infoIcon");
        JsonElement jsonElement42 = json.get("warningBackground");
        JsonElement jsonElement43 = json.get("warningBorder");
        JsonElement jsonElement44 = json.get("warningIcon");
        JsonElement jsonElement45 = json.get("successBackground");
        JsonElement jsonElement46 = json.get("successBorder");
        JsonElement jsonElement47 = json.get("successIcon");
        JsonElement jsonElement48 = json.get("errorBackground");
        JsonElement jsonElement49 = json.get("errorBorder");
        JsonElement jsonElement50 = json.get("errorIcon");
        JsonElement jsonElement51 = json.get("selectionText");
        JsonElement jsonElement52 = json.get("searchPlaceholderText");
        JsonElement jsonElement53 = json.get("moduleDivider");
        JsonElement jsonElement54 = json.get("selectedOverlay");
        JsonElement jsonElement55 = json.get("white");
        JsonElement jsonElement56 = json.get("black");
        JsonElement jsonElement57 = json.get("transparent");
        return new CustomTheme((String)object, jsonElement != null ? jsonElement.getAsBoolean() : defaults.getRainbowEnabled(), jsonElement2 != null ? jsonElement2.getAsFloat() : defaults.getRainbowSpeed(), jsonElement3 != null ? jsonElement3.getAsFloat() : defaults.getRainbowSaturation(), jsonElement4 != null ? jsonElement4.getAsFloat() : defaults.getRainbowBrightness(), jsonElement5 != null ? jsonElement5.getAsInt() : defaults.getBackground(), jsonElement6 != null ? jsonElement6.getAsInt() : defaults.getPanel(), jsonElement7 != null ? jsonElement7.getAsInt() : defaults.getInset(), jsonElement8 != null ? jsonElement8.getAsInt() : defaults.getOverlay(), jsonElement9 != null ? jsonElement9.getAsInt() : defaults.getText(), jsonElement10 != null ? jsonElement10.getAsInt() : defaults.getTextPrimary(), jsonElement11 != null ? jsonElement11.getAsInt() : defaults.getTextSecondary(), jsonElement12 != null ? jsonElement12.getAsInt() : defaults.getTextDisabled(), jsonElement13 != null ? jsonElement13.getAsInt() : defaults.getTextPlaceholder(), jsonElement14 != null ? jsonElement14.getAsInt() : defaults.getTextOnAccent(), jsonElement15 != null ? jsonElement15.getAsInt() : defaults.getAccent(), jsonElement16 != null ? jsonElement16.getAsInt() : defaults.getAccentPrimary(), jsonElement17 != null ? jsonElement17.getAsInt() : defaults.getAccentSecondary(), jsonElement18 != null ? jsonElement18.getAsInt() : defaults.getSelection(), jsonElement19 != null ? jsonElement19.getAsInt() : defaults.getControlBg(), jsonElement20 != null ? jsonElement20.getAsInt() : defaults.getControlBorder(), jsonElement21 != null ? jsonElement21.getAsInt() : defaults.getInputBg(), jsonElement22 != null ? jsonElement22.getAsInt() : defaults.getInputBorder(), jsonElement23 != null ? jsonElement23.getAsInt() : defaults.getSuccess(), jsonElement24 != null ? jsonElement24.getAsInt() : defaults.getWarning(), jsonElement25 != null ? jsonElement25.getAsInt() : defaults.getError(), jsonElement26 != null ? jsonElement26.getAsInt() : defaults.getInfo(), jsonElement27 != null ? jsonElement27.getAsInt() : defaults.getScrollbarThumb(), jsonElement28 != null ? jsonElement28.getAsInt() : defaults.getScrollbarTrack(), jsonElement29 != null ? jsonElement29.getAsInt() : defaults.getSliderTrack(), jsonElement30 != null ? jsonElement30.getAsInt() : defaults.getSliderFill(), jsonElement31 != null ? jsonElement31.getAsInt() : defaults.getSliderThumb(), jsonElement32 != null ? jsonElement32.getAsInt() : defaults.getTooltipBackground(), jsonElement33 != null ? jsonElement33.getAsInt() : defaults.getTooltipBorder(), jsonElement34 != null ? jsonElement34.getAsInt() : defaults.getTooltipText(), jsonElement35 != null ? jsonElement35.getAsInt() : defaults.getNotificationBackground(), jsonElement36 != null ? jsonElement36.getAsInt() : defaults.getNotificationBorder(), jsonElement37 != null ? jsonElement37.getAsInt() : defaults.getNotificationText(), jsonElement38 != null ? jsonElement38.getAsInt() : defaults.getNotificationTextSecondary(), jsonElement39 != null ? jsonElement39.getAsInt() : defaults.getInfoBackground(), jsonElement40 != null ? jsonElement40.getAsInt() : defaults.getInfoBorder(), jsonElement41 != null ? jsonElement41.getAsInt() : defaults.getInfoIcon(), jsonElement42 != null ? jsonElement42.getAsInt() : defaults.getWarningBackground(), jsonElement43 != null ? jsonElement43.getAsInt() : defaults.getWarningBorder(), jsonElement44 != null ? jsonElement44.getAsInt() : defaults.getWarningIcon(), jsonElement45 != null ? jsonElement45.getAsInt() : defaults.getSuccessBackground(), jsonElement46 != null ? jsonElement46.getAsInt() : defaults.getSuccessBorder(), jsonElement47 != null ? jsonElement47.getAsInt() : defaults.getSuccessIcon(), jsonElement48 != null ? jsonElement48.getAsInt() : defaults.getErrorBackground(), jsonElement49 != null ? jsonElement49.getAsInt() : defaults.getErrorBorder(), jsonElement50 != null ? jsonElement50.getAsInt() : defaults.getErrorIcon(), jsonElement51 != null ? jsonElement51.getAsInt() : defaults.getSelectionText(), jsonElement52 != null ? jsonElement52.getAsInt() : defaults.getSearchPlaceholderText(), jsonElement53 != null ? jsonElement53.getAsInt() : defaults.getModuleDivider(), jsonElement54 != null ? jsonElement54.getAsInt() : defaults.getSelectedOverlay(), jsonElement55 != null ? jsonElement55.getAsInt() : defaults.getWhite(), jsonElement56 != null ? jsonElement56.getAsInt() : defaults.getBlack(), jsonElement57 != null ? jsonElement57.getAsInt() : defaults.getTransparent());
    }
}

