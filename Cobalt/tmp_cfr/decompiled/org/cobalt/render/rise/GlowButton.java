/*
 * Decompiled with CFR 0.152.
 */
package org.cobalt.render.rise;

public final class GlowButton {
    private final String label;
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final float radius;
    private final float hoverProgress;

    public GlowButton(String label, float x, float y, float width, float height, float radius, float hoverProgress) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.hoverProgress = hoverProgress;
    }

    public String getLabel() {
        return this.label;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getRadius() {
        return this.radius;
    }

    public float getHoverProgress() {
        return this.hoverProgress;
    }
}

