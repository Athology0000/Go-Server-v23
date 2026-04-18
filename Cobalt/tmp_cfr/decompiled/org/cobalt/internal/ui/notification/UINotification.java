/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.ui.notification;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.cobalt.api.ui.theme.ThemeManager;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.ui.UIComponent;
import org.cobalt.internal.ui.animation.BounceAnimation;
import org.cobalt.internal.ui.animation.EaseOutAnimation;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0012\b\u0000\u0018\u0000 22\u00020\u0001:\u00012B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\u0005\u00a2\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\r\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\u0005\u00a2\u0006\u0004\b\r\u0010\fJ\u0015\u0010\u0010\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0010\u0010\u0011J\u000f\u0010\u0012\u001a\u00020\nH\u0016\u00a2\u0006\u0004\b\u0012\u0010\u0013J\u0015\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0014\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0015\u0010\u0016J\r\u0010\u0018\u001a\u00020\u0017\u00a2\u0006\u0004\b\u0018\u0010\u0019R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0003\u0010\u001aR\u0014\u0010\u0004\u001a\u00020\u00028\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0004\u0010\u001aR\u0017\u0010\u0006\u001a\u00020\u00058\u0006\u00a2\u0006\f\n\u0004\b\u0006\u0010\u001b\u001a\u0004\b\u001c\u0010\u001dR\u0016\u0010\u001f\u001a\u00020\u001e8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 R\u0016\u0010\"\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\"\u0010#R\u0016\u0010$\u001a\u00020!8\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b$\u0010#R\"\u0010%\u001a\u00020\u000e8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b%\u0010&\u001a\u0004\b'\u0010(\"\u0004\b)\u0010\u0011R\"\u0010*\u001a\u00020\u000e8\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b*\u0010&\u001a\u0004\b+\u0010(\"\u0004\b,\u0010\u0011R\u0016\u0010-\u001a\u00020\u00178\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b-\u0010.R\u0016\u0010/\u001a\u00020\u00058\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b/\u0010\u001bR\u0011\u00101\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b0\u0010(\u00a8\u00063"}, d2={"Lorg/cobalt/internal/ui/notification/UINotification;", "Lorg/cobalt/internal/ui/UIComponent;", "", "title", "description", "", "duration", "<init>", "(Ljava/lang/String;Ljava/lang/String;J)V", "currentTime", "", "start", "(J)V", "checkExpiry", "", "newTargetY", "moveTo", "(F)V", "render", "()V", "screenWidth", "xOffset", "(F)F", "", "isDone", "()Z", "Ljava/lang/String;", "J", "getDuration", "()J", "Lorg/cobalt/internal/ui/animation/BounceAnimation;", "slideInAnim", "Lorg/cobalt/internal/ui/animation/BounceAnimation;", "Lorg/cobalt/internal/ui/animation/EaseOutAnimation;", "slideDownAnim", "Lorg/cobalt/internal/ui/animation/EaseOutAnimation;", "slideOutAnim", "targetY", "F", "getTargetY", "()F", "setTargetY", "previousY", "getPreviousY", "setPreviousY", "isExpired", "Z", "expiryTime", "getYOffset", "yOffset", "Companion", "cobalt"})
@SourceDebugExtension(value={"SMAP\nUINotification.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UINotification.kt\norg/cobalt/internal/ui/notification/UINotification\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,93:1\n1#2:94\n*E\n"})
public final class UINotification
extends UIComponent {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final String title;
    @NotNull
    private final String description;
    private final long duration;
    @NotNull
    private BounceAnimation slideInAnim;
    @NotNull
    private EaseOutAnimation slideDownAnim;
    @NotNull
    private EaseOutAnimation slideOutAnim;
    private float targetY;
    private float previousY;
    private boolean isExpired;
    private long expiryTime;

    public UINotification(@NotNull String title, @NotNull String description, long duration) {
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)description, (String)"description");
        super(0.0f, 0.0f, 350.0f, UINotification.Companion.calculateHeight(title, description));
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.slideInAnim = new BounceAnimation(600L);
        this.slideDownAnim = new EaseOutAnimation(400L);
        this.slideOutAnim = new EaseOutAnimation(400L);
    }

    public final long getDuration() {
        return this.duration;
    }

    public final float getTargetY() {
        return this.targetY;
    }

    public final void setTargetY(float f) {
        this.targetY = f;
    }

    public final float getPreviousY() {
        return this.previousY;
    }

    public final void setPreviousY(float f) {
        this.previousY = f;
    }

    /*
     * WARNING - void declaration
     */
    public final void start(long currentTime) {
        void $this$start_u24lambda_u240;
        BounceAnimation bounceAnimation;
        BounceAnimation bounceAnimation2 = bounceAnimation = new BounceAnimation(600L);
        UINotification uINotification = this;
        boolean bl = false;
        $this$start_u24lambda_u240.start();
        uINotification.slideInAnim = bounceAnimation;
        this.slideDownAnim = new EaseOutAnimation(200L);
        this.slideOutAnim = new EaseOutAnimation(400L);
        this.expiryTime = currentTime + this.duration + 600L;
        this.isExpired = false;
    }

    /*
     * WARNING - void declaration
     */
    public final void checkExpiry(long currentTime) {
        if (!this.isExpired && currentTime >= this.expiryTime) {
            void $this$checkExpiry_u24lambda_u240;
            EaseOutAnimation easeOutAnimation;
            this.isExpired = true;
            EaseOutAnimation easeOutAnimation2 = easeOutAnimation = new EaseOutAnimation(400L);
            UINotification uINotification = this;
            boolean bl = false;
            $this$checkExpiry_u24lambda_u240.start();
            uINotification.slideOutAnim = easeOutAnimation;
        }
    }

    /*
     * WARNING - void declaration
     */
    public final void moveTo(float newTargetY) {
        if (!(newTargetY == this.targetY)) {
            void $this$moveTo_u24lambda_u240;
            EaseOutAnimation easeOutAnimation;
            this.previousY = this.targetY + this.slideDownAnim.get(this.previousY - this.targetY, 0.0f, false).floatValue();
            this.targetY = newTargetY;
            EaseOutAnimation easeOutAnimation2 = easeOutAnimation = new EaseOutAnimation(200L);
            UINotification uINotification = this;
            boolean bl = false;
            $this$moveTo_u24lambda_u240.start();
            uINotification.slideDownAnim = easeOutAnimation;
        }
    }

    @Override
    public void render() {
        NVGRenderer.rect(0.0f, 0.0f, this.getWidth(), this.getHeight(), ThemeManager.INSTANCE.getCurrentTheme().getNotificationBackground(), 5.0f);
        float titleHeight = NVGRenderer.getWrappedStringHeight$default(this.title, this.getWidth() - 30.0f, 16.0f, null, 8, null);
        NVGRenderer.drawWrappedString$default(this.title, 15.0f, 15.0f, this.getWidth() - 30.0f, 16.0f, ThemeManager.INSTANCE.getCurrentTheme().getNotificationText(), null, 0.0f, 192, null);
        NVGRenderer.drawWrappedString$default(this.description, 15.0f, 15.0f + titleHeight + 10.0f, this.getWidth() - 30.0f, 14.0f, ThemeManager.INSTANCE.getCurrentTheme().getNotificationTextSecondary(), NVGRenderer.INSTANCE.getInterFont(), 0.0f, 128, null);
    }

    public final float xOffset(float screenWidth) {
        return this.isExpired ? this.slideOutAnim.get(screenWidth - this.getWidth() - 10.0f, screenWidth, false).floatValue() : this.slideInAnim.get(screenWidth, screenWidth - this.getWidth() - 10.0f, false).floatValue();
    }

    public final float getYOffset() {
        return this.targetY + this.slideDownAnim.get(this.previousY - this.targetY, 0.0f, false).floatValue();
    }

    public final boolean isDone() {
        return this.isExpired && !this.slideOutAnim.isAnimating();
    }

    @Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\b\u001a\u00020\u00072\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004H\u0002\u00a2\u0006\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2={"Lorg/cobalt/internal/ui/notification/UINotification$Companion;", "", "<init>", "()V", "", "title", "description", "", "calculateHeight", "(Ljava/lang/String;Ljava/lang/String;)F", "cobalt"})
    public static final class Companion {
        private Companion() {
        }

        private final float calculateHeight(String title, String description) {
            float titleHeight = NVGRenderer.getWrappedStringHeight$default(title, 320.0f, 16.0f, null, 8, null);
            float descHeight = NVGRenderer.getWrappedStringHeight$default(description, 320.0f, 14.0f, null, 8, null);
            return Math.max(100.0f, titleHeight + descHeight + 45.0f);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

