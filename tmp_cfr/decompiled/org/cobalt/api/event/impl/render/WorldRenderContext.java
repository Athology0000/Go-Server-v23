/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4604
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.api.event.impl.render;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003R$\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\"\u0010\f\u001a\u00020\u000b8\u0006@\u0006X\u0086.\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\"\u0010\u0013\u001a\u00020\u00128\u0006@\u0006X\u0086.\u00a2\u0006\u0012\n\u0004\b\u0013\u0010\u0014\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\"\u0010\u001a\u001a\u00020\u00198\u0006@\u0006X\u0086.\u00a2\u0006\u0012\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001f\u00a8\u0006 "}, d2={"Lorg/cobalt/api/event/impl/render/WorldRenderContext;", "", "<init>", "()V", "Lnet/minecraft/class_4587;", "matrixStack", "Lnet/minecraft/class_4587;", "getMatrixStack", "()Lnet/minecraft/class_4587;", "setMatrixStack", "(Lnet/minecraft/class_4587;)V", "Lnet/minecraft/class_4597;", "consumers", "Lnet/minecraft/class_4597;", "getConsumers", "()Lnet/minecraft/class_4597;", "setConsumers", "(Lnet/minecraft/class_4597;)V", "Lnet/minecraft/class_4184;", "camera", "Lnet/minecraft/class_4184;", "getCamera", "()Lnet/minecraft/class_4184;", "setCamera", "(Lnet/minecraft/class_4184;)V", "Lnet/minecraft/class_4604;", "frustum", "Lnet/minecraft/class_4604;", "getFrustum", "()Lnet/minecraft/class_4604;", "setFrustum", "(Lnet/minecraft/class_4604;)V", "cobalt"})
public final class WorldRenderContext {
    @Nullable
    private class_4587 matrixStack;
    public class_4597 consumers;
    public class_4184 camera;
    public class_4604 frustum;

    @Nullable
    public final class_4587 getMatrixStack() {
        return this.matrixStack;
    }

    public final void setMatrixStack(@Nullable class_4587 class_45872) {
        this.matrixStack = class_45872;
    }

    @NotNull
    public final class_4597 getConsumers() {
        class_4597 class_45972 = this.consumers;
        if (class_45972 != null) {
            return class_45972;
        }
        Intrinsics.throwUninitializedPropertyAccessException((String)"consumers");
        return null;
    }

    public final void setConsumers(@NotNull class_4597 class_45972) {
        Intrinsics.checkNotNullParameter((Object)class_45972, (String)"<set-?>");
        this.consumers = class_45972;
    }

    @NotNull
    public final class_4184 getCamera() {
        class_4184 class_41842 = this.camera;
        if (class_41842 != null) {
            return class_41842;
        }
        Intrinsics.throwUninitializedPropertyAccessException((String)"camera");
        return null;
    }

    public final void setCamera(@NotNull class_4184 class_41842) {
        Intrinsics.checkNotNullParameter((Object)class_41842, (String)"<set-?>");
        this.camera = class_41842;
    }

    @NotNull
    public final class_4604 getFrustum() {
        class_4604 class_46042 = this.frustum;
        if (class_46042 != null) {
            return class_46042;
        }
        Intrinsics.throwUninitializedPropertyAccessException((String)"frustum");
        return null;
    }

    public final void setFrustum(@NotNull class_4604 class_46042) {
        Intrinsics.checkNotNullParameter((Object)class_46042, (String)"<set-?>");
        this.frustum = class_46042;
    }
}

