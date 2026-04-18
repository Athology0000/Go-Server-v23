/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.text.CharsKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  net.minecraft.class_11905
 *  net.minecraft.class_11908
 *  net.minecraft.class_310
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.components.settings;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.CharsKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_310;
import org.cobalt.api.module.setting.impl.ColorMode;
import org.cobalt.api.module.setting.impl.ColorSetting;
import org.cobalt.api.module.setting.impl.ThemeColorResolver;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.api.util.ui.helper.Gradient;
import org.cobalt.api.util.ui.helper.Image;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.ColorAnimation;
import org.cobalt.internal.ui.util.ExtensionsKt;
import org.cobalt.internal.ui.util.ScrollHandler;
import org.cobalt.internal.ui.util.TextInputHandler;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u001c\n\u0002\u0010\u0006\n\u0002\b\u001b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0000\u0018\u0000 \u009a\u00012\u00020\u0001:\u0002\u009a\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0016\u00a2\u0006\u0004\b\u0007\u0010\bJ\r\u0010\t\u001a\u00020\u0006\u00a2\u0006\u0004\b\t\u0010\bJ\u001f\u0010\r\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\r\u0010\u000eJ\u001f\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u000f\u0010\u000eJ7\u0010\u0017\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0018J\u001f\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b\u0019\u0010\u000eJ'\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\u001aH\u0002\u00a2\u0006\u0004\b\u001c\u0010\u001dJ'\u0010\u001f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020\u001eH\u0002\u00a2\u0006\u0004\b\u001f\u0010 J?\u0010'\u001a\u00020\u00062\u0006\u0010!\u001a\u00020\n2\u0006\u0010\"\u001a\u00020\n2\u0006\u0010#\u001a\u00020\n2\u0006\u0010$\u001a\u00020\n2\u0006\u0010%\u001a\u00020\n2\u0006\u0010&\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b'\u0010(J'\u0010*\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020)H\u0002\u00a2\u0006\u0004\b*\u0010+J'\u0010-\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\u001b\u001a\u00020,H\u0002\u00a2\u0006\u0004\b-\u0010.J\u001f\u0010/\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b/\u0010\u000eJ\u0017\u00102\u001a\u00020\u00152\u0006\u00101\u001a\u000200H\u0002\u00a2\u0006\u0004\b2\u00103J\u0019\u00105\u001a\u0004\u0018\u0001002\u0006\u00104\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b5\u00106J\u0017\u00107\u001a\u00020\u00132\u0006\u00104\u001a\u00020\u0015H\u0002\u00a2\u0006\u0004\b7\u00108J\u000f\u00109\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b9\u0010\bJ\u000f\u0010:\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b:\u0010\bJ\u0017\u0010<\u001a\u00020\u00132\u0006\u0010;\u001a\u000200H\u0016\u00a2\u0006\u0004\b<\u0010=J\u001f\u0010>\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\n2\u0006\u0010\"\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b>\u0010?J\u001f\u0010@\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b@\u0010?J'\u0010C\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\n2\u0006\u0010A\u001a\u00020\n2\u0006\u0010B\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bC\u0010DJ\u000f\u0010E\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bE\u0010\bJ\u000f\u0010F\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\bF\u0010\bJ'\u0010G\u001a\u00020\u00132\u0006\u0010!\u001a\u00020\n2\u0006\u0010A\u001a\u00020\n2\u0006\u0010B\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bG\u0010DJ\u001f\u0010H\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bH\u0010?J\u001f\u0010I\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bI\u0010?J\u001f\u0010J\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bJ\u0010?J\u001f\u0010K\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bK\u0010?J\u001f\u0010L\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bL\u0010?J'\u0010P\u001a\u00020\u00132\u0006\u0010;\u001a\u0002002\u0006\u0010N\u001a\u00020M2\u0006\u0010O\u001a\u00020MH\u0016\u00a2\u0006\u0004\bP\u0010QJ\u0017\u0010R\u001a\u00020\u00132\u0006\u0010;\u001a\u000200H\u0016\u00a2\u0006\u0004\bR\u0010=J\u001f\u0010U\u001a\u00020\u00132\u0006\u0010S\u001a\u00020M2\u0006\u0010T\u001a\u00020MH\u0016\u00a2\u0006\u0004\bU\u0010VJ\u001f\u0010Y\u001a\u00020\u00062\u0006\u0010W\u001a\u00020\n2\u0006\u0010X\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bY\u0010\u000eJ\u0017\u0010[\u001a\u00020\u00062\u0006\u0010Z\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b[\u0010\\J\u0017\u0010]\u001a\u00020\u00062\u0006\u0010Z\u001a\u00020\nH\u0002\u00a2\u0006\u0004\b]\u0010\\J\u000f\u0010^\u001a\u00020\u0006H\u0002\u00a2\u0006\u0004\b^\u0010\bJ'\u0010a\u001a\u00020\u00062\u0006\u0010_\u001a\u0002002\u0006\u0010!\u001a\u00020\n2\u0006\u0010`\u001a\u00020\nH\u0002\u00a2\u0006\u0004\ba\u0010bJ'\u0010c\u001a\u00020\u00062\u0006\u0010_\u001a\u0002002\u0006\u0010!\u001a\u00020\n2\u0006\u0010`\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bc\u0010bJ'\u0010e\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001a2\u0006\u0010_\u001a\u0002002\u0006\u0010d\u001a\u00020\nH\u0002\u00a2\u0006\u0004\be\u0010fJ'\u0010g\u001a\u00020\u001e2\u0006\u0010\u001b\u001a\u00020\u001e2\u0006\u0010_\u001a\u0002002\u0006\u0010d\u001a\u00020\nH\u0002\u00a2\u0006\u0004\bg\u0010hJ\u0017\u0010k\u001a\u00020\u00132\u0006\u0010j\u001a\u00020iH\u0016\u00a2\u0006\u0004\bk\u0010lJ\u0017\u0010n\u001a\u00020\u00132\u0006\u0010j\u001a\u00020mH\u0016\u00a2\u0006\u0004\bn\u0010oJ\u0017\u0010q\u001a\u00020\u00132\u0006\u0010p\u001a\u000200H\u0002\u00a2\u0006\u0004\bq\u0010=J\u001f\u0010s\u001a\u00020\u00132\u0006\u0010p\u001a\u0002002\u0006\u0010r\u001a\u00020\u0013H\u0002\u00a2\u0006\u0004\bs\u0010tR\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010uR\u0016\u0010v\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bv\u0010wR\u0016\u0010x\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bx\u0010wR\u0014\u0010z\u001a\u00020y8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bz\u0010{R\u0016\u0010|\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b|\u0010wR\u0016\u0010}\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b}\u0010~R\u0016\u0010\u007f\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u007f\u0010~R\u0018\u0010\u0080\u0001\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0080\u0001\u0010~R\u0018\u0010\u0081\u0001\u001a\u00020\n8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0081\u0001\u0010~R\u0018\u0010\u0082\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0082\u0001\u0010wR\u0018\u0010\u0083\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0083\u0001\u0010wR\u0018\u0010\u0084\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0084\u0001\u0010wR\u0018\u0010\u0085\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0085\u0001\u0010wR\u0018\u0010\u0086\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0086\u0001\u0010wR\u0018\u0010\u0087\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0087\u0001\u0010wR\u0018\u0010\u0088\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0088\u0001\u0010wR\u0018\u0010\u008a\u0001\u001a\u00030\u0089\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008a\u0001\u0010\u008b\u0001R\u0019\u0010\u008c\u0001\u001a\u00020\u00158\u0002@\u0002X\u0082\u000e\u00a2\u0006\b\n\u0006\b\u008c\u0001\u0010\u008d\u0001R\u0018\u0010\u008e\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u008e\u0001\u0010wR\u0018\u0010\u008f\u0001\u001a\u00030\u0089\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u008f\u0001\u0010\u008b\u0001R\u0018\u0010\u0090\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0090\u0001\u0010wR\u0018\u0010\u0091\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0091\u0001\u0010wR\u0018\u0010\u0092\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0092\u0001\u0010wR\u0018\u0010\u0093\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0093\u0001\u0010wR\u0018\u0010\u0095\u0001\u001a\u00030\u0094\u00018\u0002X\u0082\u0004\u00a2\u0006\b\n\u0006\b\u0095\u0001\u0010\u0096\u0001R\u0018\u0010\u0097\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0097\u0001\u0010wR\u0018\u0010\u0098\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0098\u0001\u0010wR\u0018\u0010\u0099\u0001\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0007\n\u0005\b\u0099\u0001\u0010w\u00a8\u0006\u009b\u0001"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIColorSetting;", "Lorg/cobalt/internal/ui/UIComponent;", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "setting", "<init>", "(Lorg/cobalt/api/module/setting/impl/ColorSetting;)V", "", "render", "()V", "drawColorPicker", "", "px", "py", "drawSourceTabs", "(FF)V", "drawEffectToggles", "x", "y", "size", "", "checked", "", "label", "drawCheckbox", "(FFFZLjava/lang/String;)V", "drawStaticPanel", "Lorg/cobalt/api/module/setting/impl/ColorMode$Rainbow;", "mode", "drawRainbowPanel", "(FFLorg/cobalt/api/module/setting/impl/ColorMode$Rainbow;)V", "Lorg/cobalt/api/module/setting/impl/ColorMode$SyncedRainbow;", "drawSyncedRainbowPanel", "(FFLorg/cobalt/api/module/setting/impl/ColorMode$SyncedRainbow;)V", "bx", "by", "speed", "saturation", "brightness", "opacity", "drawRainbowSliders", "(FFFFFF)V", "Lorg/cobalt/api/module/setting/impl/ColorMode$ThemeColor;", "drawThemePanel", "(FFLorg/cobalt/api/module/setting/impl/ColorMode$ThemeColor;)V", "Lorg/cobalt/api/module/setting/impl/ColorMode$TweakedTheme;", "drawTweakedPanel", "(FFLorg/cobalt/api/module/setting/impl/ColorMode$TweakedTheme;)V", "drawPreviewSwatch", "", "argb", "argbToHex", "(I)Ljava/lang/String;", "hex", "parseHexToARGB", "(Ljava/lang/String;)Ljava/lang/Integer;", "validateHexInput", "(Ljava/lang/String;)Z", "commitHexInput", "updateHexFromCurrentColor", "button", "mouseClicked", "(I)Z", "handleTabClicks", "(FF)Z", "handleCheckboxClicks", "checkboxY", "checkboxSize", "handleCustomCheckboxClicks", "(FFF)Z", "toggleRainbowMode", "toggleSyncedMode", "handleThemeCheckboxClicks", "handleStaticPanelClick", "handleRainbowPanelClick", "handleSyncedRainbowPanelClick", "handleThemePanelClick", "handleTweakedPanelClick", "", "offsetX", "offsetY", "mouseDragged", "(IDD)Z", "mouseReleased", "horizontalAmount", "verticalAmount", "mouseScrolled", "(DD)Z", "boxX", "boxY", "updateStaticColorFromBox", "sliderX", "updateStaticHueFromSlider", "(F)V", "updateStaticOpacityFromSlider", "updateStaticColor", "index", "sliderWidth", "updateRainbowSlider", "(IFF)V", "updateTweakedSlider", "normalized", "updateRainbowModeValues", "(Lorg/cobalt/api/module/setting/impl/ColorMode$Rainbow;IF)Lorg/cobalt/api/module/setting/impl/ColorMode$Rainbow;", "updateSyncedRainbowModeValues", "(Lorg/cobalt/api/module/setting/impl/ColorMode$SyncedRainbow;IF)Lorg/cobalt/api/module/setting/impl/ColorMode$SyncedRainbow;", "Lnet/minecraft/class_11905;", "input", "charTyped", "(Lnet/minecraft/class_11905;)Z", "Lnet/minecraft/class_11908;", "keyPressed", "(Lnet/minecraft/class_11908;)Z", "key", "handleCtrlKeyCombo", "shift", "handleHexInputKey", "(IZ)Z", "Lorg/cobalt/api/module/setting/impl/ColorSetting;", "pickerOpen", "Z", "modeDropdownOpen", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "modeDropdownHoverAnim", "Lorg/cobalt/internal/ui/animation/ColorAnimation;", "modeDropdownWasHovering", "staticHue", "F", "staticSaturation", "staticBrightness", "staticOpacity", "draggingStaticHue", "draggingStaticOpacity", "draggingStaticColor", "draggingSpeed", "draggingSaturation", "draggingBrightness", "draggingOpacity", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "themeScrollHandler", "Lorg/cobalt/internal/ui/util/ScrollHandler;", "selectedThemeProperty", "Ljava/lang/String;", "tweakedPropertyDropdownOpen", "tweakedPropertyScrollHandler", "draggingHueOffset", "draggingSaturationMult", "draggingBrightnessMult", "draggingOpacityMult", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "hexInputHandler", "Lorg/cobalt/internal/ui/util/TextInputHandler;", "hexFocused", "hexDragging", "hexValid", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUIColorSetting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UIColorSetting.kt\norg/cobalt/internal/ui/components/settings/UIColorSetting\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 4 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1035:1\n1924#2,3:1036\n1915#2,2:1040\n1924#2,3:1043\n1915#2,2:1048\n221#3:1039\n222#3:1042\n221#3:1047\n222#3:1050\n6#4:1046\n6#4:1051\n6#4:1052\n9#4:1053\n6#4:1054\n6#4:1055\n6#4:1056\n6#4:1057\n1#5:1058\n*S KotlinDebug\n*F\n+ 1 UIColorSetting.kt\norg/cobalt/internal/ui/components/settings/UIColorSetting\n*L\n313#1:1036,3\n356#1:1040,2\n433#1:1043,3\n744#1:1048,2\n349#1:1039\n349#1:1042\n741#1:1047\n741#1:1050\n693#1:1046\n796#1:1051\n868#1:1052\n869#1:1053\n874#1:1054\n879#1:1055\n891#1:1056\n901#1:1057\n*E\n"})
public final class UIColorSetting
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ColorSetting setting;
    private boolean pickerOpen;
    private boolean modeDropdownOpen;
    @NotNull
    private final ColorAnimation modeDropdownHoverAnim;
    private boolean modeDropdownWasHovering;
    private float staticHue;
    private float staticSaturation;
    private float staticBrightness;
    private float staticOpacity;
    private boolean draggingStaticHue;
    private boolean draggingStaticOpacity;
    private boolean draggingStaticColor;
    private boolean draggingSpeed;
    private boolean draggingSaturation;
    private boolean draggingBrightness;
    private boolean draggingOpacity;
    @NotNull
    private final ScrollHandler themeScrollHandler;
    @NotNull
    private String selectedThemeProperty;
    private boolean tweakedPropertyDropdownOpen;
    @NotNull
    private final ScrollHandler tweakedPropertyScrollHandler;
    private boolean draggingHueOffset;
    private boolean draggingSaturationMult;
    private boolean draggingBrightnessMult;
    private boolean draggingOpacityMult;
    @NotNull
    private final TextInputHandler hexInputHandler;
    private boolean hexFocused;
    private boolean hexDragging;
    private boolean hexValid;
    @NotNull
    private static final Image checkmarkIcon = NVGRenderer.createImage("/assets/cobalt/textures/ui/checkmark.svg");

    public UIColorSetting(@NotNull ColorSetting setting) {
        Intrinsics.checkNotNullParameter((Object)setting, (String)"setting");
        super(0.0f, 0.0f, 627.5f, 60.0f);
        this.setting = setting;
        this.modeDropdownHoverAnim = new ColorAnimation(150L);
        this.staticSaturation = 1.0f;
        this.staticBrightness = 0.5f;
        this.staticOpacity = 1.0f;
        this.themeScrollHandler = new ScrollHandler(0.0f, 1, null);
        this.selectedThemeProperty = "accent";
        this.tweakedPropertyScrollHandler = new ScrollHandler(0.0f, 1, null);
        this.hexInputHandler = new TextInputHandler("", 9);
        this.hexValid = true;
        ColorMode mode = this.setting.getMode();
        if (mode instanceof ColorMode.Static) {
            Color color = new Color(((ColorMode.Static)mode).getArgb(), true);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            this.staticHue = hsb[0];
            this.staticSaturation = hsb[1];
            this.staticBrightness = hsb[2];
            this.staticOpacity = (float)color.getAlpha() / 255.0f;
            this.hexInputHandler.setText(this.argbToHex(((ColorMode.Static)mode).getArgb()));
        } else if (mode instanceof ColorMode.ThemeColor) {
            this.selectedThemeProperty = ((ColorMode.ThemeColor)mode).getPropertyName();
        } else if (mode instanceof ColorMode.TweakedTheme) {
            this.selectedThemeProperty = ((ColorMode.TweakedTheme)mode).getPropertyName();
        } else {
            Color color = new Color(this.setting.getValue(), true);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            this.staticHue = hsb[0];
            this.staticSaturation = hsb[1];
            this.staticBrightness = hsb[2];
            this.staticOpacity = (float)color.getAlpha() / 255.0f;
            this.hexInputHandler.setText(this.argbToHex(this.setting.getValue()));
        }
    }

    @Override
    public void render() {
        NVGRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 10.0f);
        NVGRenderer.hollowRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        NVGRenderer.text$default(this.setting.getName(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f - 15.5f, 15.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        NVGRenderer.text$default(this.setting.getDescription(), this.getX() + 20.0f, this.getY() + this.getHeight() / 2.0f + 2.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
        NVGRenderer.rect(this.getX() + this.getWidth() - 50.0f, this.getY() + this.getHeight() / 2.0f - 15.0f, 30.0f, 30.0f, this.setting.getValue(), 6.0f);
        NVGRenderer.hollowRect(this.getX() + this.getWidth() - 50.0f, this.getY() + this.getHeight() / 2.0f - 15.0f, 30.0f, 30.0f, 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 6.0f);
    }

    public final void drawColorPicker() {
        float f;
        if (!this.pickerOpen) {
            return;
        }
        float px = this.getX() + this.getWidth() - 360.0f;
        float py = this.getY() + this.getHeight() - 10.0f;
        ColorMode colorMode = this.setting.getMode();
        if (colorMode instanceof ColorMode.Static) {
            f = 450.0f;
        } else if (colorMode instanceof ColorMode.Rainbow || colorMode instanceof ColorMode.SyncedRainbow) {
            f = 360.0f;
        } else if (colorMode instanceof ColorMode.ThemeColor) {
            f = 400.0f;
        } else if (colorMode instanceof ColorMode.TweakedTheme) {
            f = 470.0f;
        } else {
            throw new NoWhenBranchMatchedException();
        }
        float pickerHeight = f;
        NVGRenderer.rect(px + 2.0f, py + 2.0f, 340.0f, pickerHeight, new Color(0, 0, 0, 50).getRGB(), 10.0f);
        NVGRenderer.rect(px, py, 340.0f, pickerHeight, ThemeManager.INSTANCE.getCurrentTheme().getPanel(), 10.0f);
        NVGRenderer.hollowRect(px, py, 340.0f, pickerHeight, 2.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 10.0f);
        this.drawSourceTabs(px, py);
        this.drawEffectToggles(px, py);
        float controlsY = py + 75.0f;
        ColorMode colorMode2 = this.setting.getMode();
        if (colorMode2 instanceof ColorMode.Static) {
            this.drawStaticPanel(px, controlsY);
        } else if (colorMode2 instanceof ColorMode.Rainbow) {
            ColorMode colorMode3 = this.setting.getMode();
            Intrinsics.checkNotNull((Object)colorMode3, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.impl.ColorMode.Rainbow");
            this.drawRainbowPanel(px, controlsY, (ColorMode.Rainbow)colorMode3);
        } else if (colorMode2 instanceof ColorMode.SyncedRainbow) {
            ColorMode colorMode4 = this.setting.getMode();
            Intrinsics.checkNotNull((Object)colorMode4, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.impl.ColorMode.SyncedRainbow");
            this.drawSyncedRainbowPanel(px, controlsY, (ColorMode.SyncedRainbow)colorMode4);
        } else if (colorMode2 instanceof ColorMode.ThemeColor) {
            ColorMode colorMode5 = this.setting.getMode();
            Intrinsics.checkNotNull((Object)colorMode5, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.impl.ColorMode.ThemeColor");
            this.drawThemePanel(px, controlsY, (ColorMode.ThemeColor)colorMode5);
        } else if (colorMode2 instanceof ColorMode.TweakedTheme) {
            ColorMode colorMode6 = this.setting.getMode();
            Intrinsics.checkNotNull((Object)colorMode6, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.impl.ColorMode.TweakedTheme");
            this.drawTweakedPanel(px, controlsY, (ColorMode.TweakedTheme)colorMode6);
        } else {
            throw new NoWhenBranchMatchedException();
        }
        this.drawPreviewSwatch(px, py + pickerHeight - 70.0f);
    }

    private final void drawSourceTabs(float px, float py) {
        float bx = px + 10.0f;
        float by = py + 10.0f;
        float totalWidth = 320.0f;
        float tabWidth = (totalWidth - 10.0f) / 2.0f;
        float tabHeight = 28.0f;
        boolean isCustom = !(this.setting.getMode() instanceof ColorMode.ThemeColor) && !(this.setting.getMode() instanceof ColorMode.TweakedTheme);
        int customColor = isCustom ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg();
        NVGRenderer.rect(bx, by, tabWidth, tabHeight, customColor, 5.0f);
        NVGRenderer.hollowRect(bx, by, tabWidth, tabHeight, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 5.0f);
        String customText = "Custom";
        float customTextWidth = NVGRenderer.textWidth$default(customText, 13.0f, null, 4, null);
        int customTextColor = isCustom ? ThemeManager.INSTANCE.getCurrentTheme().getWhite() : ThemeManager.INSTANCE.getCurrentTheme().getText();
        NVGRenderer.text$default(customText, bx + (tabWidth - customTextWidth) / 2.0f, by + 9.0f, 13.0f, customTextColor, null, 32, null);
        float themeX = bx + tabWidth + 10.0f;
        int themeColor = !isCustom ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg();
        NVGRenderer.rect(themeX, by, tabWidth, tabHeight, themeColor, 5.0f);
        NVGRenderer.hollowRect(themeX, by, tabWidth, tabHeight, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 5.0f);
        String themeText = "Theme";
        float themeTextWidth = NVGRenderer.textWidth$default(themeText, 13.0f, null, 4, null);
        int themeTextColor = !isCustom ? ThemeManager.INSTANCE.getCurrentTheme().getWhite() : ThemeManager.INSTANCE.getCurrentTheme().getText();
        NVGRenderer.text$default(themeText, themeX + (tabWidth - themeTextWidth) / 2.0f, by + 9.0f, 13.0f, themeTextColor, null, 32, null);
    }

    private final void drawEffectToggles(float px, float py) {
        boolean isCustom;
        float bx = px + 10.0f;
        float by = py + 48.0f;
        float checkboxSize = 20.0f;
        boolean bl = isCustom = !(this.setting.getMode() instanceof ColorMode.ThemeColor) && !(this.setting.getMode() instanceof ColorMode.TweakedTheme);
        if (isCustom) {
            boolean isRainbow = this.setting.getMode() instanceof ColorMode.Rainbow || this.setting.getMode() instanceof ColorMode.SyncedRainbow;
            this.drawCheckbox(bx, by, checkboxSize, isRainbow, "Rainbow");
            float syncX = bx + 100.0f;
            boolean isSynced = this.setting.getMode() instanceof ColorMode.SyncedRainbow;
            this.drawCheckbox(syncX, by, checkboxSize, isSynced, "Sync");
        } else {
            boolean isAdjusted = this.setting.getMode() instanceof ColorMode.TweakedTheme;
            this.drawCheckbox(bx, by, checkboxSize, isAdjusted, "Adjust");
        }
    }

    private final void drawCheckbox(float x, float y, float size, boolean checked, String label) {
        int bgColor = checked ? ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg();
        int borderColor = checked ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getControlBorder();
        NVGRenderer.rect(x, y, size, size, bgColor, 5.0f);
        NVGRenderer.hollowRect(x, y, size, size, 1.0f, borderColor, 5.0f);
        if (checked) {
            NVGRenderer.image(checkmarkIcon, x + 2.0f, y + 2.0f, size - 4.0f, size - 4.0f, 0.0f, ThemeManager.INSTANCE.getCurrentTheme().getAccent());
        }
        NVGRenderer.text$default(label, x + size + 5.0f, y + 5.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
    }

    private final void drawStaticPanel(float px, float py) {
        float bx = px + 10.0f;
        float by = py + 10.0f;
        float boxWidth = 320.0f;
        float boxHeight = 180.0f;
        int hueColor = Color.HSBtoRGB(this.staticHue, 1.0f, 1.0f);
        NVGRenderer.pushScissor(bx, by, boxWidth, boxHeight);
        NVGRenderer.rect(bx, by, boxWidth, boxHeight, hueColor, 6.0f);
        NVGRenderer.gradientRect(bx, by, boxWidth, boxHeight, ThemeManager.INSTANCE.getCurrentTheme().getWhite(), ThemeManager.INSTANCE.getCurrentTheme().getTransparent(), Gradient.LeftToRight, 6.0f);
        NVGRenderer.gradientRect(bx, by, boxWidth, boxHeight, ThemeManager.INSTANCE.getCurrentTheme().getTransparent(), ThemeManager.INSTANCE.getCurrentTheme().getBlack(), Gradient.TopToBottom, 6.0f);
        NVGRenderer.popScissor();
        NVGRenderer.hollowRect(bx, by, boxWidth, boxHeight, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 6.0f);
        float selectorX = bx + this.staticSaturation * boxWidth;
        float selectorY = by + (1.0f - this.staticBrightness) * boxHeight;
        int currentRgb = Color.HSBtoRGB(this.staticHue, this.staticSaturation, this.staticBrightness);
        NVGRenderer.circle(selectorX, selectorY, 7.0f, ThemeManager.INSTANCE.getCurrentTheme().getWhite());
        NVGRenderer.circle(selectorX, selectorY, 5.0f, currentRgb);
        float hueY = py + boxHeight + 20.0f;
        float sliderWidth = 320.0f;
        for (int i = 0; i < 36; ++i) {
            float x1 = bx + sliderWidth / 36.0f * (float)i;
            float x2 = bx + sliderWidth / 36.0f * (float)(i + 1);
            int color1 = Color.HSBtoRGB((float)i / 36.0f, 1.0f, 1.0f);
            int color2 = Color.HSBtoRGB((float)(i + 1) / 36.0f, 1.0f, 1.0f);
            NVGRenderer.gradientRect(x1, hueY, x2 - x1, 6.0f, color1, color2, Gradient.LeftToRight, 0.0f);
        }
        NVGRenderer.hollowRect(bx, hueY, sliderWidth, 6.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 3.0f);
        NVGRenderer.circle(bx + this.staticHue * sliderWidth, hueY + 3.0f, 8.0f, ThemeManager.INSTANCE.getCurrentTheme().getWhite());
        float opacityY = hueY + 20.0f;
        NVGRenderer.rect(bx, opacityY, sliderWidth, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getWhite(), 3.0f);
        int currentColor = Color.HSBtoRGB(this.staticHue, this.staticSaturation, this.staticBrightness);
        int opaqueColor = new Color(currentColor | 0xFF000000, true).getRGB();
        int transparentColor = new Color(currentColor & 0xFFFFFF, true).getRGB();
        NVGRenderer.gradientRect(bx, opacityY, sliderWidth, 6.0f, transparentColor, opaqueColor, Gradient.LeftToRight, 3.0f);
        NVGRenderer.hollowRect(bx, opacityY, sliderWidth, 6.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 3.0f);
        NVGRenderer.circle(bx + this.staticOpacity * sliderWidth, opacityY + 3.0f, 8.0f, ThemeManager.INSTANCE.getCurrentTheme().getWhite());
        float hexY = opacityY + 20.0f;
        NVGRenderer.text$default("Hex Code", bx, hexY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        float inputY = hexY + 20.0f;
        float inputX = bx;
        float inputWidth = 320.0f;
        float inputHeight = 30.0f;
        int borderColor = this.hexFocused ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : (!this.hexValid ? ThemeManager.INSTANCE.getCurrentTheme().getError() : ThemeManager.INSTANCE.getCurrentTheme().getInputBorder());
        NVGRenderer.rect(inputX, inputY, inputWidth, inputHeight, ThemeManager.INSTANCE.getCurrentTheme().getInputBg(), 5.0f);
        NVGRenderer.hollowRect(inputX, inputY, inputWidth, inputHeight, 2.0f, borderColor, 5.0f);
        float textX = inputX + 10.0f;
        float textY = inputY + 9.0f;
        if (this.hexFocused) {
            this.hexInputHandler.updateScroll(300.0f, 13.0f);
        }
        NVGRenderer.pushScissor(inputX + 10.0f, inputY, 300.0f, inputHeight);
        if (this.hexFocused) {
            this.hexInputHandler.renderSelection(textX, textY, 13.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getSelection());
        }
        NVGRenderer.text$default(this.hexInputHandler.getText(), textX - this.hexInputHandler.getTextOffset(), textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        if (this.hexFocused) {
            this.hexInputHandler.renderCursor(textX, textY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText());
        }
        NVGRenderer.popScissor();
    }

    private final void drawRainbowPanel(float px, float py, ColorMode.Rainbow mode) {
        float bx = px + 20.0f;
        this.drawRainbowSliders(bx, py, mode.getSpeed(), mode.getSaturation(), mode.getBrightness(), mode.getOpacity());
    }

    private final void drawSyncedRainbowPanel(float px, float py, ColorMode.SyncedRainbow mode) {
        float bx = px + 20.0f;
        this.drawRainbowSliders(bx, py, mode.getSpeed(), mode.getSaturation(), mode.getBrightness(), mode.getOpacity());
    }

    /*
     * WARNING - void declaration
     */
    private final void drawRainbowSliders(float bx, float by, float speed, float saturation, float brightness, float opacity) {
        Object[] objectArray = new String[]{"Speed", "Saturation", "Brightness", "Opacity"};
        List labels = CollectionsKt.listOf((Object[])objectArray);
        Object[] objectArray2 = new Float[]{Float.valueOf(speed), Float.valueOf(saturation), Float.valueOf(brightness), Float.valueOf(opacity)};
        List values = CollectionsKt.listOf((Object[])objectArray2);
        float sliderWidth = 300.0f;
        Iterable $this$forEachIndexed$iv = values;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            String valueText;
            void value;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            float f = ((Number)item$iv).floatValue();
            int index = n;
            boolean bl = false;
            float sliderY = by + (float)index * 50.0f;
            NVGRenderer.text$default((String)labels.get(index), bx, sliderY + 2.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
            String string = "%.2f";
            Object[] objectArray3 = new Object[]{Float.valueOf((float)value)};
            Intrinsics.checkNotNullExpressionValue((Object)String.format(string, Arrays.copyOf(objectArray3, objectArray3.length)), (String)"format(...)");
            float valueWidth = NVGRenderer.textWidth$default(valueText, 12.0f, null, 4, null);
            NVGRenderer.text$default(valueText, bx + sliderWidth - valueWidth, sliderY + 2.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
            float trackY = sliderY + 24.0f;
            void normalizedValue = index == 0 ? RangesKt.coerceIn((float)(value / 2.0f), (float)0.0f, (float)1.0f) : value;
            float thumbX = bx + normalizedValue * sliderWidth;
            NVGRenderer.rect(bx, trackY, sliderWidth, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderTrack(), 3.0f);
            NVGRenderer.rect(bx, trackY, thumbX - bx, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderFill(), 3.0f);
            NVGRenderer.circle(thumbX, trackY + 3.0f, 8.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderThumb());
        }
    }

    private final void drawThemePanel(float px, float py, ColorMode.ThemeColor mode) {
        float bx = px + 10.0f;
        float by = py + 10.0f;
        float panelWidth = 320.0f;
        float panelHeight = 240.0f;
        NVGRenderer.rect(bx, by, panelWidth, panelHeight, ThemeManager.INSTANCE.getCurrentTheme().getInset(), 5.0f);
        NVGRenderer.hollowRect(bx, by, panelWidth, panelHeight, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 5.0f);
        NVGRenderer.pushScissor(bx, by, panelWidth, panelHeight);
        float currentY = 0.0f;
        currentY = by + 10.0f - this.themeScrollHandler.getOffset();
        float totalHeight = 0.0f;
        totalHeight = 10.0f;
        Map<String, List<String>> $this$forEach$iv = ThemeColorResolver.INSTANCE.getGroups();
        boolean $i$f$forEach = false;
        Iterator<Map.Entry<String, List<String>>> iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> element$iv;
            Map.Entry<String, List<String>> entry = element$iv = iterator.next();
            boolean bl = false;
            String groupName = entry.getKey();
            List<String> properties = entry.getValue();
            NVGRenderer.text$default(groupName, bx + 10.0f, currentY, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
            currentY += 22.0f;
            totalHeight += 22.0f;
            Iterable $this$forEach$iv2 = properties;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                String propertyName = (String)element$iv2;
                boolean bl2 = false;
                boolean isSelected = Intrinsics.areEqual((Object)propertyName, (Object)mode.getPropertyName());
                float itemY = currentY;
                boolean isHovering = ExtensionsKt.isHoveringOver(bx + 5.0f, itemY - 2.0f, panelWidth - 10.0f, 22.0f);
                if (isSelected) {
                    NVGRenderer.rect(bx + 5.0f, itemY - 2.0f, panelWidth - 10.0f, 22.0f, ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay(), 4.0f);
                } else if (isHovering) {
                    NVGRenderer.rect(bx + 5.0f, itemY - 2.0f, panelWidth - 10.0f, 22.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBg(), 4.0f);
                }
                int previewColor = ThemeColorResolver.INSTANCE.resolve(propertyName);
                NVGRenderer.rect(bx + 15.0f, itemY, 18.0f, 18.0f, previewColor, 3.0f);
                NVGRenderer.hollowRect(bx + 15.0f, itemY, 18.0f, 18.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 3.0f);
                int textColor = isSelected ? ThemeManager.INSTANCE.getCurrentTheme().getAccent() : ThemeManager.INSTANCE.getCurrentTheme().getText();
                NVGRenderer.text$default(propertyName, bx + 40.0f, itemY + 4.0f, 12.0f, textColor, null, 32, null);
                currentY += 22.0f;
                totalHeight += 22.0f;
            }
            currentY += 8.0f;
            totalHeight += 8.0f;
        }
        NVGRenderer.popScissor();
        this.themeScrollHandler.setMaxScroll(totalHeight, panelHeight);
        if (this.themeScrollHandler.isScrollable()) {
            float scrollbarX = bx + panelWidth - 7.0f;
            float scrollbarY = by + 3.0f;
            float scrollbarHeight = panelHeight - 6.0f;
            float thumbHeight = panelHeight / totalHeight * scrollbarHeight;
            float thumbY = scrollbarY + this.themeScrollHandler.getOffset() / this.themeScrollHandler.getMaxScroll() * (scrollbarHeight - thumbHeight);
            NVGRenderer.rect(scrollbarX, thumbY, 4.0f, thumbHeight, ThemeManager.INSTANCE.getCurrentTheme().getScrollbarThumb(), 2.0f);
        }
    }

    /*
     * WARNING - void declaration
     */
    private final void drawTweakedPanel(float px, float py, ColorMode.TweakedTheme mode) {
        float bx = px + 20.0f;
        float currentY = 0.0f;
        currentY = py + 10.0f;
        NVGRenderer.text$default("Base Property", bx, currentY, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        float dropdownWidth = 300.0f;
        float dropdownHeight = 30.0f;
        boolean isHovering = ExtensionsKt.isHoveringOver(bx, currentY += 22.0f, dropdownWidth, dropdownHeight);
        int bgColor = isHovering ? ThemeManager.INSTANCE.getCurrentTheme().getSelectedOverlay() : ThemeManager.INSTANCE.getCurrentTheme().getControlBg();
        NVGRenderer.rect(bx, currentY, dropdownWidth, dropdownHeight, bgColor, 5.0f);
        NVGRenderer.hollowRect(bx, currentY, dropdownWidth, dropdownHeight, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 5.0f);
        int previewColor = ThemeColorResolver.INSTANCE.resolve(mode.getPropertyName());
        NVGRenderer.rect(bx + 8.0f, currentY + 6.0f, 18.0f, 18.0f, previewColor, 3.0f);
        NVGRenderer.hollowRect(bx + 8.0f, currentY + 6.0f, 18.0f, 18.0f, 1.0f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 3.0f);
        NVGRenderer.text$default(mode.getPropertyName(), bx + 32.0f, currentY + 8.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        currentY += 40.0f;
        Object[] objectArray = new String[]{"Hue Offset", "Saturation", "Brightness", "Opacity"};
        List labels = CollectionsKt.listOf((Object[])objectArray);
        Object[] objectArray2 = new Float[]{Float.valueOf(mode.getHueOffset() / 180.0f), Float.valueOf(mode.getSaturationMultiplier() / 2.0f), Float.valueOf(mode.getBrightnessMultiplier() / 2.0f), Float.valueOf(mode.getOpacityMultiplier())};
        List values = CollectionsKt.listOf((Object[])objectArray2);
        Iterable $this$forEachIndexed$iv = values;
        boolean $i$f$forEachIndexed = false;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            void normalizedValue;
            String valueText;
            int n;
            if ((n = index$iv++) < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            float f = ((Number)item$iv).floatValue();
            int index = n;
            boolean bl = false;
            float sliderY = currentY + (float)index * 50.0f;
            float displayValue = switch (index) {
                case 0 -> mode.getHueOffset();
                case 1 -> mode.getSaturationMultiplier();
                case 2 -> mode.getBrightnessMultiplier();
                case 3 -> mode.getOpacityMultiplier();
                default -> 0.0f;
            };
            NVGRenderer.text$default((String)labels.get(index), bx, sliderY + 2.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
            String string = "%.2f";
            Object[] objectArray3 = new Object[]{Float.valueOf(displayValue)};
            Intrinsics.checkNotNullExpressionValue((Object)String.format(string, Arrays.copyOf(objectArray3, objectArray3.length)), (String)"format(...)");
            float valueWidth = NVGRenderer.textWidth$default(valueText, 12.0f, null, 4, null);
            NVGRenderer.text$default(valueText, bx + 300.0f - valueWidth, sliderY + 2.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
            float trackY = sliderY + 24.0f;
            float sliderWidth = 300.0f;
            float thumbX = index == 0 ? bx + (normalizedValue + 1.0f) / 2.0f * sliderWidth : bx + normalizedValue * sliderWidth;
            NVGRenderer.rect(bx, trackY, sliderWidth, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderTrack(), 3.0f);
            if (index == 0) {
                float centerX = bx + sliderWidth / 2.0f;
                if (thumbX > centerX) {
                    NVGRenderer.rect(centerX, trackY, thumbX - centerX, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderFill(), 3.0f);
                } else {
                    NVGRenderer.rect(thumbX, trackY, centerX - thumbX, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderFill(), 3.0f);
                }
            } else {
                NVGRenderer.rect(bx, trackY, thumbX - bx, 6.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderFill(), 3.0f);
            }
            NVGRenderer.circle(thumbX, trackY + 3.0f, 8.0f, ThemeManager.INSTANCE.getCurrentTheme().getSliderThumb());
        }
    }

    private final void drawPreviewSwatch(float px, float py) {
        float panelX = px + 10.0f;
        float panelY = py + 8.0f;
        float panelWidth = 320.0f;
        float panelHeight = 56.0f;
        NVGRenderer.rect(panelX, panelY, panelWidth, panelHeight, ThemeManager.INSTANCE.getCurrentTheme().getInset(), 6.0f);
        float swatchX = panelX + 8.0f;
        float swatchY = panelY + 8.0f;
        float swatchSize = 40.0f;
        NVGRenderer.rect(swatchX, swatchY, swatchSize, swatchSize, this.setting.getValue(), 6.0f);
        NVGRenderer.hollowRect(swatchX, swatchY, swatchSize, swatchSize, 1.5f, ThemeManager.INSTANCE.getCurrentTheme().getControlBorder(), 6.0f);
        NVGRenderer.text$default("Preview", swatchX + 50.0f, swatchY + 14.0f, 13.0f, ThemeManager.INSTANCE.getCurrentTheme().getText(), null, 32, null);
        String hexText = this.argbToHex(this.setting.getValue());
        float hexWidth = NVGRenderer.textWidth$default(hexText, 12.0f, null, 4, null);
        NVGRenderer.text$default(hexText, panelX + panelWidth - hexWidth - 10.0f, swatchY + 15.0f, 12.0f, ThemeManager.INSTANCE.getCurrentTheme().getTextSecondary(), null, 32, null);
    }

    private final String argbToHex(int argb) {
        String string = "#%08X";
        Object[] objectArray = new Object[]{argb};
        String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"format(...)");
        return string2;
    }

    private final Integer parseHexToARGB(String hex) {
        String string = StringsKt.removePrefix((String)hex, (CharSequence)"#").toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toUpperCase(...)");
        String stripped = string;
        return switch (stripped.length()) {
            case 3 -> {
                String r = StringsKt.repeat((CharSequence)String.valueOf(stripped.charAt(0)), (int)2);
                String g = StringsKt.repeat((CharSequence)String.valueOf(stripped.charAt(1)), (int)2);
                String b = StringsKt.repeat((CharSequence)String.valueOf(stripped.charAt(2)), (int)2);
                yield 0xFF000000 | Integer.parseInt(r, CharsKt.checkRadix((int)16)) << 16 | Integer.parseInt(g, CharsKt.checkRadix((int)16)) << 8 | Integer.parseInt(b, CharsKt.checkRadix((int)16));
            }
            case 6 -> 0xFF000000 | Integer.parseInt(stripped, CharsKt.checkRadix((int)16));
            case 8 -> (int)Long.parseLong(stripped, CharsKt.checkRadix((int)16));
            default -> null;
        };
    }

    private final boolean validateHexInput(String hex) {
        String string = StringsKt.removePrefix((String)hex, (CharSequence)"#").toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toUpperCase(...)");
        String stripped = string;
        CharSequence charSequence = stripped;
        return new Regex("^[0-9A-F]{3}$|^[0-9A-F]{6}$|^[0-9A-F]{8}$").matches(charSequence);
    }

    private final void commitHexInput() {
        block0: {
            Integer n = this.parseHexToARGB(this.hexInputHandler.getText());
            if (n == null) break block0;
            int argb = ((Number)n).intValue();
            boolean bl = false;
            Color color = new Color(argb, true);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            this.staticHue = hsb[0];
            this.staticSaturation = hsb[1];
            this.staticBrightness = hsb[2];
            this.staticOpacity = (float)color.getAlpha() / 255.0f;
            this.updateStaticColor();
        }
    }

    private final void updateHexFromCurrentColor() {
        if (!this.hexFocused) {
            int rgb = Color.HSBtoRGB(this.staticHue, this.staticSaturation, this.staticBrightness);
            int alpha = (int)(this.staticOpacity * (float)255);
            int argb = alpha << 24 | rgb & 0xFFFFFF;
            this.hexInputHandler.setText(this.argbToHex(argb));
            this.hexValid = true;
        }
    }

    @Override
    public boolean mouseClicked(int button) {
        boolean bl;
        float py;
        float by;
        float buttonY;
        if (button != 0) {
            return false;
        }
        float buttonX = this.getX() + this.getWidth() - 50.0f;
        if (ExtensionsKt.isHoveringOver(buttonX, buttonY = this.getY() + this.getHeight() / 2.0f - 15.0f, 30.0f, 30.0f)) {
            this.pickerOpen = !this.pickerOpen;
            return true;
        }
        if (!this.pickerOpen) {
            return false;
        }
        float px = this.getX() + this.getWidth() - 360.0f;
        float bx = px + 10.0f;
        if (this.handleTabClicks(bx, by = (py = this.getY() + this.getHeight() - 10.0f) + 10.0f)) {
            return true;
        }
        if (this.handleCheckboxClicks(bx, py)) {
            return true;
        }
        float controlsY = py + 75.0f;
        ColorMode colorMode = this.setting.getMode();
        if (colorMode instanceof ColorMode.Static) {
            bl = this.handleStaticPanelClick(px, controlsY);
        } else if (colorMode instanceof ColorMode.Rainbow) {
            bl = this.handleRainbowPanelClick(px, controlsY);
        } else if (colorMode instanceof ColorMode.SyncedRainbow) {
            bl = this.handleSyncedRainbowPanelClick(px, controlsY);
        } else if (colorMode instanceof ColorMode.ThemeColor) {
            bl = this.handleThemePanelClick(px, controlsY);
        } else if (colorMode instanceof ColorMode.TweakedTheme) {
            bl = this.handleTweakedPanelClick(px, controlsY);
        } else {
            throw new NoWhenBranchMatchedException();
        }
        return bl;
    }

    private final boolean handleTabClicks(float bx, float by) {
        float totalWidth = 320.0f;
        float tabWidth = (totalWidth - 10.0f) / 2.0f;
        float tabHeight = 28.0f;
        if (ExtensionsKt.isHoveringOver(bx, by, tabWidth, tabHeight)) {
            if (this.setting.getMode() instanceof ColorMode.ThemeColor || this.setting.getMode() instanceof ColorMode.TweakedTheme) {
                int rgb = Color.HSBtoRGB(this.staticHue, this.staticSaturation, this.staticBrightness);
                int alpha = (int)(this.staticOpacity * (float)255);
                this.setting.setMode(new ColorMode.Static(alpha << 24 | rgb & 0xFFFFFF));
            }
            return true;
        }
        if (ExtensionsKt.isHoveringOver(bx + tabWidth + 10.0f, by, tabWidth, tabHeight)) {
            if (!(this.setting.getMode() instanceof ColorMode.ThemeColor) && !(this.setting.getMode() instanceof ColorMode.TweakedTheme)) {
                this.setting.setMode(new ColorMode.ThemeColor(this.selectedThemeProperty));
            }
            return true;
        }
        return false;
    }

    private final boolean handleCheckboxClicks(float bx, float py) {
        float checkboxY = py + 48.0f;
        float checkboxSize = 20.0f;
        boolean isCustom = !(this.setting.getMode() instanceof ColorMode.ThemeColor) && !(this.setting.getMode() instanceof ColorMode.TweakedTheme);
        return isCustom ? this.handleCustomCheckboxClicks(bx, checkboxY, checkboxSize) : this.handleThemeCheckboxClicks(bx, checkboxY, checkboxSize);
    }

    private final boolean handleCustomCheckboxClicks(float bx, float checkboxY, float checkboxSize) {
        if (ExtensionsKt.isHoveringOver(bx, checkboxY, checkboxSize + 60.0f, checkboxSize)) {
            this.toggleRainbowMode();
            return true;
        }
        float syncX = bx + 100.0f;
        if (ExtensionsKt.isHoveringOver(syncX, checkboxY, checkboxSize + 40.0f, checkboxSize)) {
            this.toggleSyncedMode();
            return true;
        }
        return false;
    }

    private final void toggleRainbowMode() {
        boolean isRainbow;
        boolean bl = isRainbow = this.setting.getMode() instanceof ColorMode.Rainbow || this.setting.getMode() instanceof ColorMode.SyncedRainbow;
        if (isRainbow) {
            int rgb = Color.HSBtoRGB(this.staticHue, this.staticSaturation, this.staticBrightness);
            int alpha = (int)(this.staticOpacity * (float)255);
            this.setting.setMode(new ColorMode.Static(alpha << 24 | rgb & 0xFFFFFF));
        } else {
            this.setting.setMode(new ColorMode.Rainbow(0.0f, 0.0f, 0.0f, 0.0f, 15, null));
        }
    }

    private final void toggleSyncedMode() {
        ColorMode current = this.setting.getMode();
        if (current instanceof ColorMode.SyncedRainbow) {
            this.setting.setMode(new ColorMode.Rainbow(((ColorMode.SyncedRainbow)current).getSpeed(), ((ColorMode.SyncedRainbow)current).getSaturation(), ((ColorMode.SyncedRainbow)current).getBrightness(), ((ColorMode.SyncedRainbow)current).getOpacity()));
        } else if (current instanceof ColorMode.Rainbow) {
            this.setting.setMode(new ColorMode.SyncedRainbow(((ColorMode.Rainbow)current).getSpeed(), ((ColorMode.Rainbow)current).getSaturation(), ((ColorMode.Rainbow)current).getBrightness(), ((ColorMode.Rainbow)current).getOpacity()));
        } else {
            this.setting.setMode(new ColorMode.SyncedRainbow(0.0f, 0.0f, 0.0f, 0.0f, 15, null));
        }
    }

    private final boolean handleThemeCheckboxClicks(float bx, float checkboxY, float checkboxSize) {
        if (ExtensionsKt.isHoveringOver(bx, checkboxY, checkboxSize + 60.0f, checkboxSize)) {
            boolean isAdjusted = this.setting.getMode() instanceof ColorMode.TweakedTheme;
            this.setting.setMode(isAdjusted ? (ColorMode)new ColorMode.ThemeColor(this.selectedThemeProperty) : (ColorMode)new ColorMode.TweakedTheme(this.selectedThemeProperty, 0.0f, 0.0f, 0.0f, 0.0f, 30, null));
            return true;
        }
        return false;
    }

    private final boolean handleStaticPanelClick(float px, float py) {
        float bx = px + 10.0f;
        float by = py + 10.0f;
        if (ExtensionsKt.isHoveringOver(bx, by, 320.0f, 180.0f)) {
            this.draggingStaticColor = true;
            this.updateStaticColorFromBox(bx, by);
            return true;
        }
        float hueY = py + 200.0f;
        if (ExtensionsKt.isHoveringOver(bx, hueY, 320.0f, 6.0f)) {
            this.draggingStaticHue = true;
            this.updateStaticHueFromSlider(bx);
            return true;
        }
        float opacityY = hueY + 20.0f;
        if (ExtensionsKt.isHoveringOver(bx, opacityY, 320.0f, 6.0f)) {
            this.draggingStaticOpacity = true;
            this.updateStaticOpacityFromSlider(bx);
            return true;
        }
        float hexInputY = opacityY + 40.0f;
        if (ExtensionsKt.isHoveringOver(bx, hexInputY, 320.0f, 30.0f)) {
            this.hexFocused = true;
            this.hexDragging = true;
            boolean $i$f$getMouseX = false;
            this.hexInputHandler.startSelection((float)class_310.method_1551().field_1729.method_1603(), bx + 10.0f, 13.0f);
            return true;
        }
        if (this.hexFocused) {
            if (this.hexValid) {
                this.commitHexInput();
            }
            this.hexFocused = false;
            return true;
        }
        return false;
    }

    private final boolean handleRainbowPanelClick(float px, float py) {
        float bx = px + 20.0f;
        float sliderWidth = 300.0f;
        for (int i = 0; i < 4; ++i) {
            float sliderY = py + (float)i * 50.0f + 24.0f;
            if (!ExtensionsKt.isHoveringOver(bx, sliderY - 5.0f, sliderWidth, 16.0f)) continue;
            switch (i) {
                case 0: {
                    this.draggingSpeed = true;
                    break;
                }
                case 1: {
                    this.draggingSaturation = true;
                    break;
                }
                case 2: {
                    this.draggingBrightness = true;
                    break;
                }
                case 3: {
                    this.draggingOpacity = true;
                }
            }
            this.updateRainbowSlider(i, bx, sliderWidth);
            return true;
        }
        return false;
    }

    private final boolean handleSyncedRainbowPanelClick(float px, float py) {
        return this.handleRainbowPanelClick(px, py);
    }

    private final boolean handleThemePanelClick(float px, float py) {
        float bx = px + 10.0f;
        float by = py + 10.0f;
        float panelWidth = 320.0f;
        float panelHeight = 240.0f;
        if (!ExtensionsKt.isHoveringOver(bx, by, panelWidth, panelHeight)) {
            return false;
        }
        float currentY = 0.0f;
        currentY = by + 10.0f - this.themeScrollHandler.getOffset();
        Map<String, List<String>> $this$forEach$iv = ThemeColorResolver.INSTANCE.getGroups();
        boolean $i$f$forEach = false;
        Iterator<Map.Entry<String, List<String>>> iterator = $this$forEach$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> element$iv;
            Map.Entry<String, List<String>> entry = element$iv = iterator.next();
            boolean bl = false;
            List<String> properties = entry.getValue();
            currentY += 22.0f;
            Iterable $this$forEach$iv2 = properties;
            boolean $i$f$forEach2 = false;
            for (Object element$iv2 : $this$forEach$iv2) {
                String propertyName = (String)element$iv2;
                boolean bl2 = false;
                float itemY = currentY;
                if (ExtensionsKt.isHoveringOver(bx + 5.0f, itemY - 2.0f, panelWidth - 10.0f, 22.0f)) {
                    this.selectedThemeProperty = propertyName;
                    this.setting.setMode(new ColorMode.ThemeColor(propertyName));
                    return true;
                }
                currentY += 22.0f;
            }
            currentY += 8.0f;
        }
        return true;
    }

    private final boolean handleTweakedPanelClick(float px, float py) {
        float bx = px + 20.0f;
        float sliderWidth = 300.0f;
        for (int i = 0; i < 4; ++i) {
            float sliderY = py + 102.0f + (float)i * 50.0f;
            if (!ExtensionsKt.isHoveringOver(bx, sliderY - 5.0f, sliderWidth, 16.0f)) continue;
            switch (i) {
                case 0: {
                    this.draggingHueOffset = true;
                    break;
                }
                case 1: {
                    this.draggingSaturationMult = true;
                    break;
                }
                case 2: {
                    this.draggingBrightnessMult = true;
                    break;
                }
                case 3: {
                    this.draggingOpacityMult = true;
                }
            }
            this.updateTweakedSlider(i, bx, sliderWidth);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(int button, double offsetX, double offsetY) {
        if (button != 0 || !this.pickerOpen) {
            return false;
        }
        float px = this.getX() + this.getWidth() - 360.0f;
        float py = this.getY() + this.getHeight() - 10.0f;
        float controlsY = py + 75.0f;
        ColorMode colorMode = this.setting.getMode();
        if (colorMode instanceof ColorMode.Static) {
            float bx = px + 10.0f;
            if (this.draggingStaticColor) {
                this.updateStaticColorFromBox(bx, controlsY + 10.0f);
            } else if (this.draggingStaticHue) {
                this.updateStaticHueFromSlider(bx);
            } else if (this.draggingStaticOpacity) {
                this.updateStaticOpacityFromSlider(bx);
            } else {
                if (this.hexDragging && this.hexFocused) {
                    boolean $i$f$getMouseX = false;
                    this.hexInputHandler.updateSelection((float)class_310.method_1551().field_1729.method_1603(), bx + 10.0f, 13.0f);
                    return true;
                }
                return false;
            }
            return true;
        }
        if (colorMode instanceof ColorMode.Rainbow || colorMode instanceof ColorMode.SyncedRainbow) {
            float bx = px + 20.0f;
            float sliderWidth = 300.0f;
            if (this.draggingSpeed) {
                this.updateRainbowSlider(0, bx, sliderWidth);
                return true;
            }
            if (this.draggingSaturation) {
                this.updateRainbowSlider(1, bx, sliderWidth);
                return true;
            }
            if (this.draggingBrightness) {
                this.updateRainbowSlider(2, bx, sliderWidth);
                return true;
            }
            if (this.draggingOpacity) {
                this.updateRainbowSlider(3, bx, sliderWidth);
                return true;
            }
        } else if (colorMode instanceof ColorMode.TweakedTheme) {
            float bx = px + 20.0f;
            float sliderWidth = 300.0f;
            if (this.draggingHueOffset) {
                this.updateTweakedSlider(0, bx, sliderWidth);
                return true;
            }
            if (this.draggingSaturationMult) {
                this.updateTweakedSlider(1, bx, sliderWidth);
                return true;
            }
            if (this.draggingBrightnessMult) {
                this.updateTweakedSlider(2, bx, sliderWidth);
                return true;
            }
            if (this.draggingOpacityMult) {
                this.updateTweakedSlider(3, bx, sliderWidth);
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button) {
        if (button == 0) {
            this.draggingStaticHue = false;
            this.draggingStaticOpacity = false;
            this.draggingStaticColor = false;
            this.draggingSpeed = false;
            this.draggingSaturation = false;
            this.draggingBrightness = false;
            this.draggingOpacity = false;
            this.draggingHueOffset = false;
            this.draggingSaturationMult = false;
            this.draggingBrightnessMult = false;
            this.draggingOpacityMult = false;
            this.hexDragging = false;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double horizontalAmount, double verticalAmount) {
        float panelHeight;
        float panelWidth;
        float py;
        float by;
        float px;
        float bx;
        if (!this.pickerOpen) {
            return false;
        }
        if (this.setting.getMode() instanceof ColorMode.ThemeColor && ExtensionsKt.isHoveringOver(bx = (px = this.getX() + this.getWidth() - 360.0f) + 10.0f, by = (py = this.getY() + this.getHeight() - 10.0f) + 60.0f, panelWidth = 320.0f, panelHeight = 240.0f)) {
            this.themeScrollHandler.handleScroll(verticalAmount);
            return true;
        }
        return false;
    }

    private final void updateStaticColorFromBox(float boxX, float boxY) {
        boolean $i$f$getMouseX = false;
        this.staticSaturation = RangesKt.coerceIn((float)(((float)class_310.method_1551().field_1729.method_1603() - boxX) / 320.0f), (float)0.0f, (float)1.0f);
        boolean $i$f$getMouseY = false;
        this.staticBrightness = RangesKt.coerceIn((float)(1.0f - ((float)class_310.method_1551().field_1729.method_1604() - boxY) / 180.0f), (float)0.0f, (float)1.0f);
        this.updateStaticColor();
    }

    private final void updateStaticHueFromSlider(float sliderX) {
        boolean $i$f$getMouseX = false;
        this.staticHue = RangesKt.coerceIn((float)(((float)class_310.method_1551().field_1729.method_1603() - sliderX) / 320.0f), (float)0.0f, (float)1.0f);
        this.updateStaticColor();
    }

    private final void updateStaticOpacityFromSlider(float sliderX) {
        boolean $i$f$getMouseX = false;
        this.staticOpacity = RangesKt.coerceIn((float)(((float)class_310.method_1551().field_1729.method_1603() - sliderX) / 320.0f), (float)0.0f, (float)1.0f);
        this.updateStaticColor();
    }

    private final void updateStaticColor() {
        int rgb = Color.HSBtoRGB(this.staticHue, this.staticSaturation, this.staticBrightness);
        int alpha = (int)(this.staticOpacity * (float)255);
        this.setting.setMode(new ColorMode.Static(alpha << 24 | rgb & 0xFFFFFF));
        this.updateHexFromCurrentColor();
    }

    private final void updateRainbowSlider(int index, float bx, float sliderWidth) {
        boolean $i$f$getMouseX = false;
        float normalized = RangesKt.coerceIn((float)(((float)class_310.method_1551().field_1729.method_1603() - bx) / sliderWidth), (float)0.0f, (float)1.0f);
        ColorMode mode = this.setting.getMode();
        this.setting.setMode(mode instanceof ColorMode.Rainbow ? (ColorMode)this.updateRainbowModeValues((ColorMode.Rainbow)mode, index, normalized) : (mode instanceof ColorMode.SyncedRainbow ? (ColorMode)this.updateSyncedRainbowModeValues((ColorMode.SyncedRainbow)mode, index, normalized) : mode));
    }

    private final void updateTweakedSlider(int index, float bx, float sliderWidth) {
        boolean $i$f$getMouseX = false;
        float normalized = RangesKt.coerceIn((float)(((float)class_310.method_1551().field_1729.method_1603() - bx) / sliderWidth), (float)0.0f, (float)1.0f);
        ColorMode colorMode = this.setting.getMode();
        ColorMode.TweakedTheme tweakedTheme = colorMode instanceof ColorMode.TweakedTheme ? (ColorMode.TweakedTheme)colorMode : null;
        if (tweakedTheme == null) {
            return;
        }
        ColorMode.TweakedTheme mode = tweakedTheme;
        ColorMode.TweakedTheme newMode = switch (index) {
            case 0 -> ColorMode.TweakedTheme.copy$default(mode, null, (normalized - 0.5f) * 360.0f, 0.0f, 0.0f, 0.0f, 29, null);
            case 1 -> ColorMode.TweakedTheme.copy$default(mode, null, 0.0f, normalized * 2.0f, 0.0f, 0.0f, 27, null);
            case 2 -> ColorMode.TweakedTheme.copy$default(mode, null, 0.0f, 0.0f, normalized * 2.0f, 0.0f, 23, null);
            case 3 -> ColorMode.TweakedTheme.copy$default(mode, null, 0.0f, 0.0f, 0.0f, normalized, 15, null);
            default -> mode;
        };
        this.setting.setMode(newMode);
    }

    private final ColorMode.Rainbow updateRainbowModeValues(ColorMode.Rainbow mode, int index, float normalized) {
        return switch (index) {
            case 0 -> ColorMode.Rainbow.copy$default(mode, normalized * 2.0f, 0.0f, 0.0f, 0.0f, 14, null);
            case 1 -> ColorMode.Rainbow.copy$default(mode, 0.0f, normalized, 0.0f, 0.0f, 13, null);
            case 2 -> ColorMode.Rainbow.copy$default(mode, 0.0f, 0.0f, normalized, 0.0f, 11, null);
            case 3 -> ColorMode.Rainbow.copy$default(mode, 0.0f, 0.0f, 0.0f, normalized, 7, null);
            default -> mode;
        };
    }

    private final ColorMode.SyncedRainbow updateSyncedRainbowModeValues(ColorMode.SyncedRainbow mode, int index, float normalized) {
        return switch (index) {
            case 0 -> ColorMode.SyncedRainbow.copy$default(mode, normalized * 2.0f, 0.0f, 0.0f, 0.0f, 14, null);
            case 1 -> ColorMode.SyncedRainbow.copy$default(mode, 0.0f, normalized, 0.0f, 0.0f, 13, null);
            case 2 -> ColorMode.SyncedRainbow.copy$default(mode, 0.0f, 0.0f, normalized, 0.0f, 11, null);
            case 3 -> ColorMode.SyncedRainbow.copy$default(mode, 0.0f, 0.0f, 0.0f, normalized, 7, null);
            default -> mode;
        };
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public boolean charTyped(@NotNull class_11905 input) {
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!(this.hexFocused && this.pickerOpen && this.setting.getMode() instanceof ColorMode.Static)) {
            return false;
        }
        char = (char)input.comp_4793();
        if ('0' <= char ? char < ':' : false) ** GOTO lbl-1000
        if ('a' <= char ? char < 'g' : false) ** GOTO lbl-1000
        v0 = 'A' <= char ? char < 'G' : false;
        if (v0 || char == '#') lbl-1000:
        // 3 sources

        {
            v1 = true;
        } else {
            v1 = false;
        }
        isHexChar = v1;
        v2 = isPrintable = char >= ' ' && char != '\u007f';
        if (isHexChar && isPrintable) {
            this.hexInputHandler.insertText(String.valueOf(char));
            this.hexValid = this.validateHexInput(this.hexInputHandler.getText());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(@NotNull class_11908 input) {
        boolean handled;
        boolean shift;
        Intrinsics.checkNotNullParameter((Object)input, (String)"input");
        if (!(this.hexFocused && this.pickerOpen && this.setting.getMode() instanceof ColorMode.Static)) {
            return false;
        }
        boolean ctrl = (input.comp_4797() & 2) != 0;
        boolean bl = shift = (input.comp_4797() & 1) != 0;
        if (ctrl && (handled = this.handleCtrlKeyCombo(input.comp_4795()))) {
            return true;
        }
        return this.handleHexInputKey(input.comp_4795(), shift);
    }

    private final boolean handleCtrlKeyCombo(int key) {
        return switch (key) {
            case 65 -> {
                this.hexInputHandler.selectAll();
                yield true;
            }
            case 67 -> {
                String v1 = this.hexInputHandler.copy();
                if (v1 != null) {
                    String it = v1;
                    boolean $i$a$-let-UIColorSetting$handleCtrlKeyCombo$1 = false;
                    class_310.method_1551().field_1774.method_1455(it);
                }
                yield true;
            }
            case 88 -> {
                String v2 = this.hexInputHandler.cut();
                if (v2 != null) {
                    String it = v2;
                    boolean $i$a$-let-UIColorSetting$handleCtrlKeyCombo$2 = false;
                    class_310.method_1551().field_1774.method_1455(it);
                }
                this.hexValid = this.validateHexInput(this.hexInputHandler.getText());
                yield true;
            }
            case 86 -> {
                String v3 = class_310.method_1551().field_1774.method_1460();
                Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"getClipboard(...)");
                String clipboard = v3;
                if (((CharSequence)clipboard).length() > 0) {
                    this.hexInputHandler.insertText(clipboard);
                    this.hexValid = this.validateHexInput(this.hexInputHandler.getText());
                }
                yield true;
            }
            default -> false;
        };
    }

    private final boolean handleHexInputKey(int key, boolean shift) {
        return switch (key) {
            case 256, 257 -> {
                if (this.hexValid) {
                    this.commitHexInput();
                }
                this.hexFocused = false;
                yield true;
            }
            case 259 -> {
                this.hexInputHandler.backspace();
                this.hexValid = this.validateHexInput(this.hexInputHandler.getText());
                yield true;
            }
            case 261 -> {
                this.hexInputHandler.delete();
                this.hexValid = this.validateHexInput(this.hexInputHandler.getText());
                yield true;
            }
            case 263 -> {
                this.hexInputHandler.moveCursorLeft(shift);
                yield true;
            }
            case 262 -> {
                this.hexInputHandler.moveCursorRight(shift);
                yield true;
            }
            case 268 -> {
                this.hexInputHandler.moveCursorToStart(shift);
                yield true;
            }
            case 269 -> {
                this.hexInputHandler.moveCursorToEnd(shift);
                yield true;
            }
            default -> false;
        };
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0005\u001a\u00020\u00048\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/internal/ui/components/settings/UIColorSetting$Companion;", "", "<init>", "()V", "Lorg/cobalt/api/util/ui/helper/Image;", "checkmarkIcon", "Lorg/cobalt/api/util/ui/helper/Image;", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

