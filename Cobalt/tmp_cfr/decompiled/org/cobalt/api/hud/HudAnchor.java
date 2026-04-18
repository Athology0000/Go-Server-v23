/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.hud;

import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003JI\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u000b2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u0004\u00a2\u0006\u0004\b\f\u0010\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016\u00a8\u0006\u0017"}, d2={"Lorg/cobalt/api/hud/HudAnchor;", "", "<init>", "(Ljava/lang/String;I)V", "", "offsetX", "offsetY", "moduleWidth", "moduleHeight", "screenWidth", "screenHeight", "Lkotlin/Pair;", "computeScreenPosition", "(FFFFFF)Lkotlin/Pair;", "TOP_LEFT", "TOP_CENTER", "TOP_RIGHT", "CENTER_LEFT", "CENTER", "CENTER_RIGHT", "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT", "cobalt"})
public final class HudAnchor
extends Enum<HudAnchor> {
    public static final /* enum */ HudAnchor TOP_LEFT = new HudAnchor();
    public static final /* enum */ HudAnchor TOP_CENTER = new HudAnchor();
    public static final /* enum */ HudAnchor TOP_RIGHT = new HudAnchor();
    public static final /* enum */ HudAnchor CENTER_LEFT = new HudAnchor();
    public static final /* enum */ HudAnchor CENTER = new HudAnchor();
    public static final /* enum */ HudAnchor CENTER_RIGHT = new HudAnchor();
    public static final /* enum */ HudAnchor BOTTOM_LEFT = new HudAnchor();
    public static final /* enum */ HudAnchor BOTTOM_CENTER = new HudAnchor();
    public static final /* enum */ HudAnchor BOTTOM_RIGHT = new HudAnchor();
    private static final /* synthetic */ HudAnchor[] $VALUES;
    private static final /* synthetic */ EnumEntries $ENTRIES;

    @NotNull
    public final Pair<Float, Float> computeScreenPosition(float offsetX, float offsetY, float moduleWidth, float moduleHeight, float screenWidth, float screenHeight) {
        float x = switch (WhenMappings.$EnumSwitchMapping$0[this.ordinal()]) {
            case 1, 2, 3 -> offsetX;
            case 4, 5, 6 -> screenWidth / 2.0f - moduleWidth / 2.0f + offsetX;
            case 7, 8, 9 -> screenWidth - moduleWidth - offsetX;
            default -> throw new NoWhenBranchMatchedException();
        };
        float y = switch (WhenMappings.$EnumSwitchMapping$0[this.ordinal()]) {
            case 1, 4, 7 -> offsetY;
            case 2, 5, 8 -> screenHeight / 2.0f - moduleHeight / 2.0f + offsetY;
            case 3, 6, 9 -> screenHeight - moduleHeight - offsetY;
            default -> throw new NoWhenBranchMatchedException();
        };
        return new Pair((Object)Float.valueOf(x), (Object)Float.valueOf(y));
    }

    public static HudAnchor[] values() {
        return (HudAnchor[])$VALUES.clone();
    }

    public static HudAnchor valueOf(String value) {
        return Enum.valueOf(HudAnchor.class, value);
    }

    @NotNull
    public static EnumEntries<HudAnchor> getEntries() {
        return $ENTRIES;
    }

    static {
        $VALUES = hudAnchorArray = new HudAnchor[]{HudAnchor.TOP_LEFT, HudAnchor.TOP_CENTER, HudAnchor.TOP_RIGHT, HudAnchor.CENTER_LEFT, HudAnchor.CENTER, HudAnchor.CENTER_RIGHT, HudAnchor.BOTTOM_LEFT, HudAnchor.BOTTOM_CENTER, HudAnchor.BOTTOM_RIGHT};
        $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    }

    @Metadata(mv={2, 3, 0}, k=3, xi=48)
    public static final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[HudAnchor.values().length];
            try {
                nArray[HudAnchor.TOP_LEFT.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.CENTER_LEFT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.BOTTOM_LEFT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.TOP_CENTER.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.CENTER.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.BOTTOM_CENTER.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.TOP_RIGHT.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.CENTER_RIGHT.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[HudAnchor.BOTTOM_RIGHT.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

