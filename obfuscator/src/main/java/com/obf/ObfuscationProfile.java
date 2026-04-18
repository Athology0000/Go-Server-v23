package com.obf;

public enum ObfuscationProfile {
    DEFAULT,
    AGGRESSIVE;

    public static ObfuscationProfile parse(String value) {
        return switch (value.trim().toLowerCase()) {
            case "default", "safe", "compat" -> DEFAULT;
            case "aggressive", "hard", "strong" -> AGGRESSIVE;
            default -> throw new IllegalArgumentException("Unknown profile: " + value);
        };
    }
}
