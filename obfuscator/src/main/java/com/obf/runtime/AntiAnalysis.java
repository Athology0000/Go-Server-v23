package com.obf.runtime;

/** Injected into target JAR. Native methods implemented in the output DLL. */
public class AntiAnalysis {
    public static native void checkAgents();
    public static native boolean isCorrupted();
}
