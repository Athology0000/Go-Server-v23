/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.class_310
 */
package org.cobalt.internal.ui.util;

import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.class_310;

@Metadata(mv={2, 3, 0}, k=2, xi=48, d1={"\u0000\u0018\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0006\u001a-\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0001\u001a\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u00002\u0006\u0010\u0003\u001a\u00020\u00002\u0006\u0010\u0004\u001a\u00020\u0000\u00a2\u0006\u0004\b\u0006\u0010\u0007\"\u0012\u0010\u000b\u001a\u00020\b8\u00c6\u0002\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\n\"\u0012\u0010\r\u001a\u00020\b8\u00c6\u0002\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\n\u00a8\u0006\u000e"}, d2={"", "x", "y", "width", "height", "", "isHoveringOver", "(FFFF)Z", "", "getMouseX", "()D", "mouseX", "getMouseY", "mouseY", "cobalt"})
@SourceDebugExtension(value={"SMAP\nExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n1#1,13:1\n6#1,4:14\n*S KotlinDebug\n*F\n+ 1 Extensions.kt\norg/cobalt/internal/ui/util/ExtensionsKt\n*L\n12#1:14,4\n*E\n"})
public final class ExtensionsKt {
    public static final double getMouseX() {
        boolean $i$f$getMouseX = false;
        return class_310.method_1551().field_1729.method_1603();
    }

    public static final double getMouseY() {
        boolean $i$f$getMouseY = false;
        return class_310.method_1551().field_1729.method_1604();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final boolean isHoveringOver(float x, float y, float width, float height) {
        boolean $i$f$getMouseX = false;
        if (!(class_310.method_1551().field_1729.method_1603() >= (double)x)) return false;
        $i$f$getMouseX = false;
        if (!(class_310.method_1551().field_1729.method_1603() <= (double)(x + width))) return false;
        boolean $i$f$getMouseY = false;
        if (!(class_310.method_1551().field_1729.method_1604() >= (double)y)) return false;
        $i$f$getMouseY = false;
        if (!(class_310.method_1551().field_1729.method_1604() <= (double)(y + height))) return false;
        return true;
    }
}

