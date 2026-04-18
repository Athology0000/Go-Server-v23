/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmStatic
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.api.util.ui;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\f\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R(\u0010\u0005\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0018\n\u0004\b\u0005\u0010\u0006\u0012\u0004\b\u000b\u0010\u0003\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR(\u0010\f\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0018\n\u0004\b\f\u0010\u0006\u0012\u0004\b\u000f\u0010\u0003\u001a\u0004\b\r\u0010\b\"\u0004\b\u000e\u0010\n\u00a8\u0006\u0010"}, d2={"Lorg/cobalt/api/util/ui/TextureTracker;", "", "<init>", "()V", "", "prevActiveTexture", "I", "getPrevActiveTexture", "()I", "setPrevActiveTexture", "(I)V", "getPrevActiveTexture$annotations", "prevBoundTexture", "getPrevBoundTexture", "setPrevBoundTexture", "getPrevBoundTexture$annotations", "cobalt"})
public final class TextureTracker {
    @NotNull
    public static final TextureTracker INSTANCE = new TextureTracker();
    private static int prevActiveTexture = -1;
    private static int prevBoundTexture = -1;

    private TextureTracker() {
    }

    public static final int getPrevActiveTexture() {
        return prevActiveTexture;
    }

    public static final void setPrevActiveTexture(int n) {
        prevActiveTexture = n;
    }

    @JvmStatic
    public static /* synthetic */ void getPrevActiveTexture$annotations() {
    }

    public static final int getPrevBoundTexture() {
        return prevBoundTexture;
    }

    public static final void setPrevBoundTexture(int n) {
        prevBoundTexture = n;
    }

    @JvmStatic
    public static /* synthetic */ void getPrevBoundTexture$annotations() {
    }
}

