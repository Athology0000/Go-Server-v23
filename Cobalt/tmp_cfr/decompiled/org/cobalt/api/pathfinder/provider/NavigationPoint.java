/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 */
package org.cobalt.api.pathfinder.provider;

import kotlin.Metadata;

@Metadata(mv={2, 3, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u000f\u0010\u0003\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\u0003\u0010\u0004J\u000f\u0010\u0005\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\u0005\u0010\u0004J\u000f\u0010\u0007\u001a\u00020\u0006H&\u00a2\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\t\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\t\u0010\u0004J\u000f\u0010\n\u001a\u00020\u0002H&\u00a2\u0006\u0004\b\n\u0010\u0004\u00a8\u0006\u000b\u00c0\u0006\u0003"}, d2={"Lorg/cobalt/api/pathfinder/provider/NavigationPoint;", "", "", "isTraversable", "()Z", "hasFloor", "", "getFloorLevel", "()D", "isClimbable", "isLiquid", "cobalt"})
public interface NavigationPoint {
    public boolean isTraversable();

    public boolean hasFloor();

    public double getFloorLevel();

    public boolean isClimbable();

    public boolean isLiquid();
}

