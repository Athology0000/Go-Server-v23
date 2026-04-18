/*
 * Decompiled with CFR 0.152.
 */
package org.cobalt.pathfinder;

import org.cobalt.pathfinder.NativeLoader;

public class NativePathfinderBridge {
    public static native long createEngine();

    public static native void destroyEngine(long var0);

    public static native void setRoute(long var0, double[] var2, boolean var3, int var4);

    public static native void setTarget(long var0, double var2, double var4, double var6);

    public static native void setTargetWithRadius(long var0, double var2, double var4, double var6, double var8);

    public static native int[] update(long var0, byte[] var2, int var3, int var4, int var5, double var6, double var8, double var10, float var12, float var13, boolean var14);

    public static native void stop(long var0);

    public static native int getStatus(long var0);

    public static native float[] getPathNodes(long var0);

    static {
        try {
            String path = NativeLoader.extract("natives/windows/cobalt_pathfinder.dll");
            System.load(path);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load cobalt_pathfinder.dll", e);
        }
    }
}

