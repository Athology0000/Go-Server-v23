/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.cobalt.internal.spotify;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0013\b\u0082\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\u0007\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0002\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\u0002\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0010\u0010\u000fJ\u0010\u0010\u0011\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0011\u0010\u000fJ\u0010\u0010\u0012\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0012\u0010\u000fJ\u0010\u0010\u0013\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0013\u0010\u000fJ\u0010\u0010\u0014\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0014\u0010\u000fJ\u0010\u0010\u0015\u001a\u00020\tH\u00c6\u0003\u00a2\u0006\u0004\b\u0015\u0010\u0016J\u0010\u0010\u0017\u001a\u00020\u0002H\u00c6\u0003\u00a2\u0006\u0004\b\u0017\u0010\u000fJ`\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\u0002H\u00c6\u0001\u00a2\u0006\u0004\b\u0018\u0010\u0019J\u001b\u0010\u001c\u001a\u00020\u001b2\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004\u00a2\u0006\u0004\b\u001c\u0010\u001dJ\u0011\u0010\u001e\u001a\u00020\tH\u00d6\u0081\u0004\u00a2\u0006\u0004\b\u001e\u0010\u0016J\u0011\u0010 \u001a\u00020\u001fH\u00d6\u0081\u0004\u00a2\u0006\u0004\b \u0010!R\"\u0010\u0003\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0003\u0010\"\u001a\u0004\b#\u0010\u000f\"\u0004\b$\u0010%R\"\u0010\u0004\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0004\u0010\"\u001a\u0004\b&\u0010\u000f\"\u0004\b'\u0010%R\"\u0010\u0005\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0005\u0010\"\u001a\u0004\b(\u0010\u000f\"\u0004\b)\u0010%R\"\u0010\u0006\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0006\u0010\"\u001a\u0004\b*\u0010\u000f\"\u0004\b+\u0010%R\"\u0010\u0007\u001a\u00020\u00028\u0006@\u0006X\u0086\u000e\u00a2\u0006\u0012\n\u0004\b\u0007\u0010\"\u001a\u0004\b,\u0010\u000f\"\u0004\b-\u0010%R\u0017\u0010\b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\b\u0010\"\u001a\u0004\b.\u0010\u000fR\u0017\u0010\n\u001a\u00020\t8\u0006\u00a2\u0006\f\n\u0004\b\n\u0010/\u001a\u0004\b0\u0010\u0016R\u0017\u0010\u000b\u001a\u00020\u00028\u0006\u00a2\u0006\f\n\u0004\b\u000b\u0010\"\u001a\u0004\b1\u0010\u000f\u00a8\u00062"}, d2={"Lorg/cobalt/internal/spotify/Particle;", "", "", "x", "y", "vx", "vy", "life", "decayRate", "", "baseColor", "size", "<init>", "(FFFFFFIF)V", "component1", "()F", "component2", "component3", "component4", "component5", "component6", "component7", "()I", "component8", "copy", "(FFFFFFIF)Lorg/cobalt/internal/spotify/Particle;", "other", "", "equals", "(Ljava/lang/Object;)Z", "hashCode", "", "toString", "()Ljava/lang/String;", "F", "getX", "setX", "(F)V", "getY", "setY", "getVx", "setVx", "getVy", "setVy", "getLife", "setLife", "getDecayRate", "I", "getBaseColor", "getSize", "cobalt"})
final class Particle {
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float life;
    private final float decayRate;
    private final int baseColor;
    private final float size;

    public Particle(float x, float y, float vx, float vy, float life, float decayRate, int baseColor, float size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.decayRate = decayRate;
        this.baseColor = baseColor;
        this.size = size;
    }

    public final float getX() {
        return this.x;
    }

    public final void setX(float f) {
        this.x = f;
    }

    public final float getY() {
        return this.y;
    }

    public final void setY(float f) {
        this.y = f;
    }

    public final float getVx() {
        return this.vx;
    }

    public final void setVx(float f) {
        this.vx = f;
    }

    public final float getVy() {
        return this.vy;
    }

    public final void setVy(float f) {
        this.vy = f;
    }

    public final float getLife() {
        return this.life;
    }

    public final void setLife(float f) {
        this.life = f;
    }

    public final float getDecayRate() {
        return this.decayRate;
    }

    public final int getBaseColor() {
        return this.baseColor;
    }

    public final float getSize() {
        return this.size;
    }

    public final float component1() {
        return this.x;
    }

    public final float component2() {
        return this.y;
    }

    public final float component3() {
        return this.vx;
    }

    public final float component4() {
        return this.vy;
    }

    public final float component5() {
        return this.life;
    }

    public final float component6() {
        return this.decayRate;
    }

    public final int component7() {
        return this.baseColor;
    }

    public final float component8() {
        return this.size;
    }

    @NotNull
    public final Particle copy(float x, float y, float vx, float vy, float life, float decayRate, int baseColor, float size) {
        return new Particle(x, y, vx, vy, life, decayRate, baseColor, size);
    }

    public static /* synthetic */ Particle copy$default(Particle particle, float f, float f2, float f3, float f4, float f5, float f6, int n, float f7, int n2, Object object) {
        if ((n2 & 1) != 0) {
            f = particle.x;
        }
        if ((n2 & 2) != 0) {
            f2 = particle.y;
        }
        if ((n2 & 4) != 0) {
            f3 = particle.vx;
        }
        if ((n2 & 8) != 0) {
            f4 = particle.vy;
        }
        if ((n2 & 0x10) != 0) {
            f5 = particle.life;
        }
        if ((n2 & 0x20) != 0) {
            f6 = particle.decayRate;
        }
        if ((n2 & 0x40) != 0) {
            n = particle.baseColor;
        }
        if ((n2 & 0x80) != 0) {
            f7 = particle.size;
        }
        return particle.copy(f, f2, f3, f4, f5, f6, n, f7);
    }

    @NotNull
    public String toString() {
        return "Particle(x=" + this.x + ", y=" + this.y + ", vx=" + this.vx + ", vy=" + this.vy + ", life=" + this.life + ", decayRate=" + this.decayRate + ", baseColor=" + this.baseColor + ", size=" + this.size + ")";
    }

    public int hashCode() {
        int result = Float.hashCode(this.x);
        result = result * 31 + Float.hashCode(this.y);
        result = result * 31 + Float.hashCode(this.vx);
        result = result * 31 + Float.hashCode(this.vy);
        result = result * 31 + Float.hashCode(this.life);
        result = result * 31 + Float.hashCode(this.decayRate);
        result = result * 31 + Integer.hashCode(this.baseColor);
        result = result * 31 + Float.hashCode(this.size);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Particle)) {
            return false;
        }
        Particle particle = (Particle)other;
        if (Float.compare(this.x, particle.x) != 0) {
            return false;
        }
        if (Float.compare(this.y, particle.y) != 0) {
            return false;
        }
        if (Float.compare(this.vx, particle.vx) != 0) {
            return false;
        }
        if (Float.compare(this.vy, particle.vy) != 0) {
            return false;
        }
        if (Float.compare(this.life, particle.life) != 0) {
            return false;
        }
        if (Float.compare(this.decayRate, particle.decayRate) != 0) {
            return false;
        }
        if (this.baseColor != particle.baseColor) {
            return false;
        }
        return Float.compare(this.size, particle.size) == 0;
    }
}

