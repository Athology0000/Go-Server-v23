/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 */
package org.cobalt.api.util.helper;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_3675;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\t\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u00a2\u0006\u0004\b\u0004\u0010\u0005J\r\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\u0004\b\u0007\u0010\bR\"\u0010\u0003\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0003\u0010\t\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\u0005R\u0016\u0010\r\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lorg/cobalt/api/util/helper/KeyBind;", "", "", "keyCode", "<init>", "(I)V", "", "isPressed", "()Z", "I", "getKeyCode", "()I", "setKeyCode", "wasPressed", "Z", "cobalt"})
public final class KeyBind {
    private int keyCode;
    private boolean wasPressed;

    public KeyBind(int keyCode) {
        this.keyCode = keyCode;
    }

    public /* synthetic */ KeyBind(int n, int n2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n2 & 1) != 0) {
            n = -1;
        }
        this(n);
    }

    public final int getKeyCode() {
        return this.keyCode;
    }

    public final void setKeyCode(int n) {
        this.keyCode = n;
    }

    public final boolean isPressed() {
        boolean bl;
        if (this.keyCode == -1) {
            return false;
        }
        class_310 class_3102 = class_310.method_1551();
        Intrinsics.checkNotNullExpressionValue((Object)class_3102, (String)"getInstance(...)");
        class_310 mc = class_3102;
        boolean isPressed = mc.field_1755 == null && class_3675.method_15987((class_1041)mc.method_22683(), (int)this.keyCode);
        boolean it = bl = isPressed && !this.wasPressed;
        boolean bl2 = false;
        this.wasPressed = isPressed;
        return bl;
    }

    public KeyBind() {
        this(0, 1, null);
    }
}

