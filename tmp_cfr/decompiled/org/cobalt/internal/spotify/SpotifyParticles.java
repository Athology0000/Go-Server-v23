/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.random.Random
 *  kotlin.random.RandomKt
 *  kotlin.ranges.RangesKt
 *  org.jetbrains.annotations.NotNull
 */
package org.cobalt.internal.spotify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.random.Random;
import kotlin.random.RandomKt;
import kotlin.ranges.RangesKt;
import org.cobalt.api.util.ui.NVGRenderer;
import org.cobalt.internal.spotify.Particle;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003JE\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\f\u00a2\u0006\u0004\b\u000f\u0010\u0010J\r\u0010\u0011\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0011\u0010\u0003J5\u0010\u0013\u001a\u00020\u000e2\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\t\u00a2\u0006\u0004\b\u0013\u0010\u0014J\r\u0010\u0015\u001a\u00020\u000e\u00a2\u0006\u0004\b\u0015\u0010\u0003R\u001a\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00170\u00168\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u0018\u0010\u0019R\u0016\u0010\u001a\u001a\u00020\u00048\u0002@\u0002X\u0082\u000e\u00a2\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001d\u001a\u00020\u001c8\u0002X\u0082\u0004\u00a2\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0014\u0010\u001f\u001a\u00020\t8\u0002X\u0082T\u00a2\u0006\u0006\n\u0004\b\u001f\u0010 \u00a8\u0006!"}, d2={"Lorg/cobalt/internal/spotify/SpotifyParticles;", "", "<init>", "()V", "", "dt", "barX", "barY", "barFillW", "", "color1", "color2", "", "playing", "", "update", "(FFFFIIZ)V", "render", "barW", "burst", "(FFFII)V", "clear", "", "Lorg/cobalt/internal/spotify/Particle;", "particles", "Ljava/util/List;", "spawnTimer", "F", "Lkotlin/random/Random;", "rng", "Lkotlin/random/Random;", "MAX_PARTICLES", "I", "cobalt"})
public final class SpotifyParticles {
    @NotNull
    public static final SpotifyParticles INSTANCE = new SpotifyParticles();
    @NotNull
    private static final List<Particle> particles = new ArrayList();
    private static float spawnTimer;
    @NotNull
    private static final Random rng;
    private static final int MAX_PARTICLES = 80;

    private SpotifyParticles() {
    }

    public final void update(float dt, float barX, float barY, float barFillW, int color1, int color2, boolean playing) {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle p = iter.next();
            p.setX(p.getX() + p.getVx() * dt);
            p.setY(p.getY() + p.getVy() * dt);
            p.setVy(p.getVy() - 18.0f * dt);
            p.setLife(p.getLife() - p.getDecayRate() * dt);
            if (!(p.getLife() <= 0.0f)) continue;
            iter.remove();
        }
        if (!playing || barFillW <= 0.0f) {
            return;
        }
        spawnTimer += dt;
        while (spawnTimer >= 0.05f && particles.size() < 80) {
            spawnTimer -= 0.05f;
            float x = barX + rng.nextFloat() * barFillW;
            float y = barY + rng.nextFloat() * 6.0f;
            float angle = rng.nextFloat() * (float)Math.PI * 2.0f;
            float speed = rng.nextFloat() * 12.0f + 4.0f;
            float size = rng.nextFloat() * 2.2f + 0.6f;
            float decay = rng.nextFloat() * 0.6f + 0.4f;
            int color = rng.nextBoolean() ? color1 : color2;
            particles.add(new Particle(x, y, (float)Math.cos(angle) * speed, (float)Math.sin(angle) * speed - speed * 0.4f, 1.0f, decay, color, size));
        }
    }

    public final void render() {
        for (Particle p : particles) {
            int alpha = RangesKt.coerceIn((int)((int)(p.getLife() * (float)255)), (int)0, (int)255);
            int col = p.getBaseColor() & 0xFFFFFF | alpha << 24;
            NVGRenderer.circle(p.getX(), p.getY(), p.getSize(), col);
        }
    }

    public final void burst(float barX, float barY, float barW, int color1, int color2) {
        int n = 30;
        for (int i = 0; i < n; ++i) {
            int color;
            int it = i;
            boolean bl = false;
            float x = barX + rng.nextFloat() * barW;
            float y = barY;
            float angle = rng.nextFloat() * (float)Math.PI * 2.0f;
            float speed = rng.nextFloat() * 30.0f + 10.0f;
            float size = rng.nextFloat() * 3.0f + 1.0f;
            int n2 = color = rng.nextBoolean() ? color1 : color2;
            if (particles.size() >= 80) continue;
            particles.add(new Particle(x, y, (float)Math.cos(angle) * speed, (float)Math.sin(angle) * speed - speed * 0.6f, 1.0f, 1.2f, color, size));
        }
    }

    public final void clear() {
        particles.clear();
    }

    static {
        rng = RandomKt.Random((long)System.nanoTime());
    }
}

