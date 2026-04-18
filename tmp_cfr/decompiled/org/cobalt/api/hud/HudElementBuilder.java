/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.hud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.cobalt.api.hud.HudAnchor;
import org.cobalt.api.hud.HudElement;
import org.cobalt.api.module.setting.Setting;
import org.cobalt.api.module.setting.SettingsContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u001b\n\u0002\u0010!\n\u0002\b\u0003\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0006\u0010\u0007J+\u0010\f\u001a\u00020\u000b2\u001a\u0010\n\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\t0\b\"\u0006\u0012\u0002\b\u00030\tH\u0016\u00a2\u0006\u0004\b\f\u0010\rJ\u0019\u0010\u000f\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\t0\u000eH\u0016\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u001b\u0010\u0014\u001a\u00020\u000b2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u00a2\u0006\u0004\b\u0014\u0010\u0015J\u001b\u0010\u0016\u001a\u00020\u000b2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u00a2\u0006\u0004\b\u0016\u0010\u0015J+\u0010\u0019\u001a\u00028\u0001\"\u0004\b\u0000\u0010\u0017\"\u000e\b\u0001\u0010\u0018*\b\u0012\u0004\u0012\u00028\u00000\t2\u0006\u0010\u0019\u001a\u00028\u0001\u00a2\u0006\u0004\b\u0019\u0010\u001aJZ\u0010!\u001a\u00020\u000b2K\u0010 \u001aG\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001d\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001e\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001f\u0012\u0004\u0012\u00020\u000b0\u001b\u00a2\u0006\u0004\b!\u0010\"JZ\u0010#\u001a\u00020\u000b2K\u0010 \u001aG\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001d\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001e\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001f\u0012\u0004\u0012\u00020\u000b0\u001b\u00a2\u0006\u0004\b#\u0010\"JZ\u0010$\u001a\u00020\u000b2K\u0010 \u001aG\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001d\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001e\u0012\u0013\u0012\u00110\u0012\u00a2\u0006\f\b\u001c\u0012\b\b\u0004\u0012\u0004\b\b(\u001f\u0012\u0004\u0012\u00020\u000b0\u001b\u00a2\u0006\u0004\b$\u0010\"J\r\u0010&\u001a\u00020%\u00a2\u0006\u0004\b&\u0010'R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010(R\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010(R\u0014\u0010\u0005\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0005\u0010(R\u001c\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00120\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b)\u0010*R\u001c\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00120\u00118\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b+\u0010*R\"\u0010-\u001a\u00020,8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b-\u0010.\u001a\u0004\b/\u00100\"\u0004\b1\u00102R\"\u00103\u001a\u00020\u00128\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b3\u00104\u001a\u0004\b5\u00106\"\u0004\b7\u00108R\"\u00109\u001a\u00020\u00128\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b9\u00104\u001a\u0004\b:\u00106\"\u0004\b;\u00108R\"\u0010\u001f\u001a\u00020\u00128\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u001f\u00104\u001a\u0004\b<\u00106\"\u0004\b=\u00108R\"\u0010>\u001a\u00020\u00128\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b>\u00104\u001a\u0004\b?\u00106\"\u0004\b@\u00108R\"\u0010A\u001a\u00020\u00128\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\bA\u00104\u001a\u0004\bB\u00106\"\u0004\bC\u00108R0\u0010D\u001a\u001c\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bD\u0010ER0\u0010F\u001a\u001c\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bF\u0010ER0\u0010G\u001a\u001c\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u001b8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\bG\u0010ER\u001e\u0010I\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\t0H8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\bI\u0010J\u00a8\u0006K"}, d2={"Lorg/cobalt/api/hud/HudElementBuilder;", "Lorg/cobalt/api/module/setting/SettingsContainer;", "", "id", "name", "description", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "", "Lorg/cobalt/api/module/setting/Setting;", "settings", "", "addSetting", "([Lorg/cobalt/api/module/setting/Setting;)V", "", "getSettings", "()Ljava/util/List;", "Lkotlin/Function0;", "", "provider", "width", "(Lkotlin/jvm/functions/Function0;)V", "height", "T", "S", "setting", "(Lorg/cobalt/api/module/setting/Setting;)Lorg/cobalt/api/module/setting/Setting;", "Lkotlin/Function3;", "Lkotlin/ParameterName;", "screenX", "screenY", "scale", "block", "preRender", "(Lkotlin/jvm/functions/Function3;)V", "render", "postRender", "Lorg/cobalt/api/hud/HudElement;", "build", "()Lorg/cobalt/api/hud/HudElement;", "Ljava/lang/String;", "widthProvider", "Lkotlin/jvm/functions/Function0;", "heightProvider", "Lorg/cobalt/api/hud/HudAnchor;", "anchor", "Lorg/cobalt/api/hud/HudAnchor;", "getAnchor", "()Lorg/cobalt/api/hud/HudAnchor;", "setAnchor", "(Lorg/cobalt/api/hud/HudAnchor;)V", "offsetX", "F", "getOffsetX", "()F", "setOffsetX", "(F)V", "offsetY", "getOffsetY", "setOffsetY", "getScale", "setScale", "minScale", "getMinScale", "setMinScale", "maxScale", "getMaxScale", "setMaxScale", "preRenderLambda", "Lkotlin/jvm/functions/Function3;", "renderLambda", "postRenderLambda", "", "settingsList", "Ljava/util/List;", "cobalt"})
public final class HudElementBuilder
implements SettingsContainer {
    @NotNull
    private final String id;
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private Function0<Float> widthProvider;
    @NotNull
    private Function0<Float> heightProvider;
    @NotNull
    private HudAnchor anchor;
    private float offsetX;
    private float offsetY;
    private float scale;
    private float minScale;
    private float maxScale;
    @Nullable
    private Function3<? super Float, ? super Float, ? super Float, Unit> preRenderLambda;
    @Nullable
    private Function3<? super Float, ? super Float, ? super Float, Unit> renderLambda;
    @Nullable
    private Function3<? super Float, ? super Float, ? super Float, Unit> postRenderLambda;
    @NotNull
    private final List<Setting<?>> settingsList;

    public HudElementBuilder(@NotNull String id, @NotNull String name, @NotNull String description) {
        Intrinsics.checkNotNullParameter((Object)id, (String)"id");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        this.id = id;
        this.name = name;
        this.description = description;
        this.widthProvider = HudElementBuilder::widthProvider$lambda$0;
        this.heightProvider = HudElementBuilder::heightProvider$lambda$0;
        this.anchor = HudAnchor.TOP_LEFT;
        this.offsetX = 10.0f;
        this.offsetY = 10.0f;
        this.scale = 1.0f;
        this.minScale = 0.5f;
        this.maxScale = 3.0f;
        this.settingsList = new ArrayList();
    }

    public /* synthetic */ HudElementBuilder(String string, String string2, String string3, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 4) != 0) {
            string3 = "";
        }
        this(string, string2, string3);
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

    public final void setScale(float f) {
        this.scale = f;
    }

    public final float getMinScale() {
        return this.minScale;
    }

    public final void setMinScale(float f) {
        this.minScale = f;
    }

    public final float getMaxScale() {
        return this.maxScale;
    }

    public final void setMaxScale(float f) {
        this.maxScale = f;
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

    public final void width(@NotNull Function0<Float> provider) {
        Intrinsics.checkNotNullParameter(provider, (String)"provider");
        this.widthProvider = provider;
    }

    public final void height(@NotNull Function0<Float> provider) {
        Intrinsics.checkNotNullParameter(provider, (String)"provider");
        this.heightProvider = provider;
    }

    @NotNull
    public final <T, S extends Setting<T>> S setting(@NotNull S setting) {
        Intrinsics.checkNotNullParameter(setting, (String)"setting");
        Setting[] settingArray = new Setting[]{setting};
        this.addSetting(settingArray);
        return setting;
    }

    public final void preRender(@NotNull Function3<? super Float, ? super Float, ? super Float, Unit> block) {
        Intrinsics.checkNotNullParameter(block, (String)"block");
        this.preRenderLambda = block;
    }

    public final void render(@NotNull Function3<? super Float, ? super Float, ? super Float, Unit> block) {
        Intrinsics.checkNotNullParameter(block, (String)"block");
        this.renderLambda = block;
    }

    public final void postRender(@NotNull Function3<? super Float, ? super Float, ? super Float, Unit> block) {
        Intrinsics.checkNotNullParameter(block, (String)"block");
        this.postRenderLambda = block;
    }

    @NotNull
    public final HudElement build() {
        Function3 function3 = this.renderLambda;
        if (function3 == null) {
            function3 = HudElementBuilder::build$lambda$0;
        }
        Function3 capturedRender = function3;
        Function0<Float> capturedWidth = this.widthProvider;
        Function0<Float> capturedHeight = this.heightProvider;
        List capturedSettings = CollectionsKt.toList((Iterable)this.settingsList);
        HudAnchor capturedAnchor = this.anchor;
        float capturedOffsetX = this.offsetX;
        float capturedOffsetY = this.offsetY;
        float capturedScale = this.scale;
        float capturedMinScale = this.minScale;
        float capturedMaxScale = this.maxScale;
        Function3<? super Float, ? super Float, ? super Float, Unit> capturedPreRender = this.preRenderLambda;
        Function3<? super Float, ? super Float, ? super Float, Unit> capturedPostRender = this.postRenderLambda;
        String string = this.id;
        String string2 = this.name;
        String string3 = this.description;
        return new HudElement(capturedAnchor, capturedOffsetX, capturedOffsetY, capturedScale, capturedMinScale, capturedMaxScale, capturedSettings, capturedWidth, capturedHeight, capturedPreRender, (Function3<? super Float, ? super Float, ? super Float, Unit>)capturedRender, capturedPostRender, string, string2, string3){
            private final HudAnchor defaultAnchor;
            private final float defaultOffsetX;
            private final float defaultOffsetY;
            private final float defaultScale;
            private final float minScale;
            private final float maxScale;
            final /* synthetic */ Function0<Float> $capturedWidth;
            final /* synthetic */ Function0<Float> $capturedHeight;
            final /* synthetic */ Function3<Float, Float, Float, Unit> $capturedPreRender;
            final /* synthetic */ Function3<Float, Float, Float, Unit> $capturedRender;
            final /* synthetic */ Function3<Float, Float, Float, Unit> $capturedPostRender;
            {
                this.$capturedWidth = $capturedWidth;
                this.$capturedHeight = $capturedHeight;
                this.$capturedPreRender = $capturedPreRender;
                this.$capturedRender = $capturedRender;
                this.$capturedPostRender = $capturedPostRender;
                super($super_call_param$1, $super_call_param$2, $super_call_param$3);
                this.defaultAnchor = $capturedAnchor;
                this.defaultOffsetX = $capturedOffsetX;
                this.defaultOffsetY = $capturedOffsetY;
                this.defaultScale = $capturedScale;
                this.minScale = $capturedMinScale;
                this.maxScale = $capturedMaxScale;
                Iterable $this$forEach$iv = $capturedSettings;
                boolean $i$f$forEach = false;
                for (T element$iv : $this$forEach$iv) {
                    Setting it = (Setting)element$iv;
                    boolean bl = false;
                    Setting[] settingArray = new Setting[]{it};
                    this.addSetting(settingArray);
                }
                this.resetPosition();
            }

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

            public float getMinScale() {
                return this.minScale;
            }

            public float getMaxScale() {
                return this.maxScale;
            }

            public float getBaseWidth() {
                return ((Number)this.$capturedWidth.invoke()).floatValue();
            }

            public float getBaseHeight() {
                return ((Number)this.$capturedHeight.invoke()).floatValue();
            }

            public void renderPre(float screenX, float screenY, float scale) {
                block0: {
                    Function3<Float, Float, Float, Unit> function3 = this.$capturedPreRender;
                    if (function3 == null) break block0;
                    function3.invoke((Object)Float.valueOf(screenX), (Object)Float.valueOf(screenY), (Object)Float.valueOf(scale));
                }
            }

            public void render(float screenX, float screenY, float scale) {
                this.$capturedRender.invoke((Object)Float.valueOf(screenX), (Object)Float.valueOf(screenY), (Object)Float.valueOf(scale));
            }

            public void renderPost(float screenX, float screenY, float scale) {
                block0: {
                    Function3<Float, Float, Float, Unit> function3 = this.$capturedPostRender;
                    if (function3 == null) break block0;
                    function3.invoke((Object)Float.valueOf(screenX), (Object)Float.valueOf(screenY), (Object)Float.valueOf(scale));
                }
            }
        };
    }

    private static final float widthProvider$lambda$0() {
        return 100.0f;
    }

    private static final float heightProvider$lambda$0() {
        return 20.0f;
    }

    private static final Unit build$lambda$0(float f, float f2, float f3) {
        return Unit.INSTANCE;
    }
}

