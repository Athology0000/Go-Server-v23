/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util.ui.helper;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/cobalt/api/util/ui/helper/Gradient;", "", "<init>", "(Ljava/lang/String;I)V", "LeftToRight", "TopToBottom", "TopLeftToBottomRight", "cobalt"})
public final class Gradient
extends Enum<Gradient> {
    public static final /* enum */ Gradient LeftToRight = new Gradient();
    public static final /* enum */ Gradient TopToBottom = new Gradient();
    public static final /* enum */ Gradient TopLeftToBottomRight = new Gradient();
    private static final /* synthetic */ Gradient[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    public static Gradient[] values() {
        return (Gradient[])$VALUES.clone();
    }

    public static Gradient valueOf(String value) {
        return Enum.valueOf(Gradient.class, value);
    }

    @NotNull
    public static EnumEntries<Gradient> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = gradientArray = new Gradient[]{Gradient.LeftToRight, Gradient.TopToBottom, Gradient.TopLeftToBottomRight};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }
}

