/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.hud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingsContainer;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u001d\n\u0002\u0010!\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J+\u0010\f\u001a\u00020\u000b2\u001a\u0010\n\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\t0\b\"\u0006\u0012\u0002\b\u00030\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u0019\u0010\u000f\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\t0\u000eH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u0012\u001a\u00020\u0011H&\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u000f\u0010\u0014\u001a\u00020\u0011H&\u00a2\u0006\u0004\b\u0014\u0010\u0013J'\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b\u0018\u0010\u0019J'\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0011H&\u00a2\u0006\u0004\b\u001a\u0010\u0019J'\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0011H\u0016\u00a2\u0006\u0004\b\u001b\u0010\u0019J\r\u0010\u001c\u001a\u00020\u0011\u00a2\u0006\u0004\b\u001c\u0010\u0013J\r\u0010\u001d\u001a\u00020\u0011\u00a2\u0006\u0004\b\u001d\u0010\u0013J)\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00110 2\u0006\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u0011\u00a2\u0006\u0004\b!\u0010\"J\r\u0010#\u001a\u00020\u000b\u00a2\u0006\u0004\b#\u0010$J\r\u0010%\u001a\u00020\u000b\u00a2\u0006\u0004\b%\u0010$J-\u0010)\u001a\u00020(2\u0006\u0010&\u001a\u00020\u00112\u0006\u0010'\u001a\u00020\u00112\u0006\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u0011\u00a2\u0006\u0004\b)\u0010*R\u0017\u0010\u0003\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0003\u0010+\u001a\u0004\b,\u0010-R\u0017\u0010\u0004\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0004\u0010+\u001a\u0004\b.\u0010-R\u0017\u0010\u0005\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u0005\u0010+\u001a\u0004\b/\u0010-R\"\u00100\u001a\u00020(8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b0\u00101\u001a\u0004\b2\u00103\"\u0004\b4\u00105R\"\u00107\u001a\u0002068\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b7\u00108\u001a\u0004\b9\u0010:\"\u0004\b;\u0010<R\"\u0010=\u001a\u00020\u00118\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b=\u0010>\u001a\u0004\b?\u0010\u0013\"\u0004\b@\u0010AR\"\u0010B\u001a\u00020\u00118\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bB\u0010>\u001a\u0004\bC\u0010\u0013\"\u0004\bD\u0010AR*\u0010\u0017\u001a\u00020\u00112\u0006\u0010E\u001a\u00020\u00118\u0006@FX\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0017\u0010>\u001a\u0004\bF\u0010\u0013\"\u0004\bG\u0010AR\u001a\u0010H\u001a\u00020\u00118\u0016X\u0096D\u00a2\u0006\f\n\u0004\bH\u0010>\u001a\u0004\bI\u0010\u0013R\u001a\u0010J\u001a\u00020\u00118\u0016X\u0096D\u00a2\u0006\f\n\u0004\bJ\u0010>\u001a\u0004\bK\u0010\u0013R\u001a\u0010L\u001a\u0002068\u0014X\u0094\u0004\u00a2\u0006\f\n\u0004\bL\u00108\u001a\u0004\bM\u0010:R\u001a\u0010N\u001a\u00020\u00118\u0014X\u0094D\u00a2\u0006\f\n\u0004\bN\u0010>\u001a\u0004\bO\u0010\u0013R\u001a\u0010P\u001a\u00020\u00118\u0014X\u0094D\u00a2\u0006\f\n\u0004\bP\u0010>\u001a\u0004\bQ\u0010\u0013R\u001a\u0010R\u001a\u00020\u00118\u0014X\u0094D\u00a2\u0006\f\n\u0004\bR\u0010>\u001a\u0004\bS\u0010\u0013R\u001e\u0010U\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\t0T8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bU\u0010V\u00a8\u0006W"}, d2={"Lorg/cobalt/api/hud/HudElement;", "Lorg/cobalt/api/module/setting/SettingsContainer;", "", "id", "name", "description", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "", "Lorg/cobalt/api/module/setting/Setting;", "settings", "", "addSetting", "([Lorg/cobalt/api/module/setting/Setting;)V", "", "getSettings", "()Ljava/util/List;", "", "getBaseWidth", "()F", "getBaseHeight", "screenX", "screenY", "scale", "renderPre", "(FFF)V", "render", "renderPost", "getScaledWidth", "getScaledHeight", "screenWidth", "screenHeight", "Lkotlin/Pair;", "getScreenPosition", "(FF)Lkotlin/Pair;", "resetPosition", "()V", "resetSettings", "px", "py", "", "containsPoint", "(FFFF)Z", "Ljava/lang/String;", "getId", "()Ljava/lang/String;", "getName", "getDescription", "enabled", "Z", "getEnabled", "()Z", "setEnabled", "(Z)V", "Lorg/cobalt/api/hud/HudAnchor;", "anchor", "Lorg/cobalt/api/hud/HudAnchor;", "getAnchor", "()Lorg/cobalt/api/hud/HudAnchor;", "setAnchor", "(Lorg/cobalt/api/hud/HudAnchor;)V", "offsetX", "F", "getOffsetX", "setOffsetX", "(F)V", "offsetY", "getOffsetY", "setOffsetY", "value", "getScale", "setScale", "minScale", "getMinScale", "maxScale", "getMaxScale", "defaultAnchor", "getDefaultAnchor", "defaultOffsetX", "getDefaultOffsetX", "defaultOffsetY", "getDefaultOffsetY", "defaultScale", "getDefaultScale", "", "settingsList", "Ljava/util/List;", "cobalt"})
public abstract class HudElement
implements SettingsContainer {
    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    private boolean enabled;
    @NotNull
    private HudAnchor anchor;
    private float offsetX;
    private float offsetY;
    private float scale;
    private final float minScale;
    private final float maxScale;
    @NotNull
    private final HudAnchor defaultAnchor;
    private final float defaultOffsetX;
    private final float defaultOffsetY;
    private final float defaultScale;
    @NotNull
    private final List<Setting<?>> settingsList;

    public HudElement(@NotNull String id, @NotNull String name, @NotNull String description) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = true;
        this.anchor = HudAnchor.TOP_LEFT;
        this.offsetX = 10.0f;
        this.offsetY = 10.0f;
        this.scale = 1.0f;
        this.minScale = 0.5f;
        this.maxScale = 3.0f;
        this.defaultAnchor = HudAnchor.TOP_LEFT;
        this.defaultOffsetX = 10.0f;
        this.defaultOffsetY = 10.0f;
        this.defaultScale = 1.0f;
        this.settingsList = new ArrayList();
    }

    public /* synthetic */ HudElement(String string, String string2, String string3, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 4) != 0) {
            string3 = "";
        }
        this(string, string2, string3);
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getDescription() {
        return this.description;
    }

    public final boolean getEnabled() {
        return this.enabled;
    }

    public final void setEnabled(boolean bl) {
        this.enabled = bl;
    }

    @NotNull
    public final HudAnchor getAnchor() {
        return this.anchor;
    }

    public final void setAnchor(@NotNull HudAnchor hudAnchor) {
        Intrinsics.checkNotNullParameter((Object)((Object)hudAnchor), (String)"<set-?>");
        this.anchor = hudAnchor;
    }

    public final float getOffsetX() {
        return this.offsetX;
    }

    public final void setOffsetX(float f) {
        this.offsetX = f;
    }

    public final float getOffsetY() {
        return this.offsetY;
    }

    public final void setOffsetY(float f) {
        this.offsetY = f;
    }

    public final float getScale() {
        return this.scale;
    }

    public final void setScale(float value) {
        this.scale = RangesKt.coerceIn((float)value, (float)this.getMinScale(), (float)this.getMaxScale());
    }

    public float getMinScale() {
        return this.minScale;
    }

    public float getMaxScale() {
        return this.maxScale;
    }

    @NotNull
    protected HudAnchor getDefaultAnchor() {
        return this.defaultAnchor;
    }

    protected float getDefaultOffsetX() {
        return this.defaultOffsetX;
    }

    protected float getDefaultOffsetY() {
        return this.defaultOffsetY;
    }

    protected float getDefaultScale() {
        return this.defaultScale;
    }

    @Override
    public void addSetting(Setting<?> ... settings) {
        Intrinsics.checkNotNullParameter(settings, (String)"settings");
        this.settingsList.addAll(CollectionsKt.listOf((Object[])Arrays.copyOf(settings, settings.length)));
    }

    @Override
    @NotNull
    public List<Setting<?>> getSettings() {
        return this.settingsList;
    }

    public abstract float getBaseWidth();

    public abstract float getBaseHeight();

    public void renderPre(float screenX, float screenY, float scale) {
    }

    public abstract void render(float var1, float var2, float var3);

    public void renderPost(float screenX, float screenY, float scale) {
    }

    public final float getScaledWidth() {
        return this.getBaseWidth() * this.scale;
    }

    public final float getScaledHeight() {
        return this.getBaseHeight() * this.scale;
    }

    @NotNull
    public final Pair<Float, Float> getScreenPosition(float screenWidth, float screenHeight) {
        return this.anchor.computeScreenPosition(this.offsetX, this.offsetY, this.getScaledWidth(), this.getScaledHeight(), screenWidth, screenHeight);
    }

    public final void resetPosition() {
        this.anchor = this.getDefaultAnchor();
        this.offsetX = this.getDefaultOffsetX();
        this.offsetY = this.getDefaultOffsetY();
        this.setScale(this.getDefaultScale());
    }

    public final void resetSettings() {
        for (Setting<?> setting : this.getSettings()) {
            Intrinsics.checkNotNull(setting, (String)"null cannot be cast to non-null type org.cobalt.api.module.setting.Setting<kotlin.Any?>");
            Setting<?> typedSetting = setting;
            typedSetting.setValue(typedSetting.getDefaultValue());
        }
    }

    public final boolean containsPoint(float px, float py, float screenWidth, float screenHeight) {
        Pair<Float, Float> pair = this.getScreenPosition(screenWidth, screenHeight);
        float sx = ((Number)pair.component1()).floatValue();
        float sy = ((Number)pair.component2()).floatValue();
        return px >= sx && px <= sx + this.getScaledWidth() && py >= sy && py <= sy + this.getScaledHeight();
    }
}

