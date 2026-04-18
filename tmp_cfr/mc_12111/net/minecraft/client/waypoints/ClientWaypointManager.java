/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.waypoints.TrackedWaypoint
 *  net.minecraft.world.waypoints.TrackedWaypointManager
 *  net.minecraft.world.waypoints.Waypoint
 */
package net.minecraft.client.waypoints;

import com.mojang.datafixers.util.Either;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.waypoints.TrackedWaypoint;
import net.minecraft.world.waypoints.TrackedWaypointManager;
import net.minecraft.world.waypoints.Waypoint;

@Environment(value=EnvType.CLIENT)
public class ClientWaypointManager
implements TrackedWaypointManager {
    private final Map<Either<UUID, String>, TrackedWaypoint> waypoints = new ConcurrentHashMap<Either<UUID, String>, TrackedWaypoint>();

    public void trackWaypoint(TrackedWaypoint trackedWaypoint) {
        this.waypoints.put((Either<UUID, String>)trackedWaypoint.id(), trackedWaypoint);
    }

    public void updateWaypoint(TrackedWaypoint trackedWaypoint) {
        this.waypoints.get(trackedWaypoint.id()).update(trackedWaypoint);
    }

    public void untrackWaypoint(TrackedWaypoint trackedWaypoint) {
        this.waypoints.remove(trackedWaypoint.id());
    }

    public boolean hasWaypoints() {
        return !this.waypoints.isEmpty();
    }

    public void forEachWaypoint(Entity entity, Consumer<TrackedWaypoint> consumer) {
        this.waypoints.values().stream().sorted(Comparator.comparingDouble(trackedWaypoint -> trackedWaypoint.distanceSquared(entity)).reversed()).forEachOrdered(consumer);
    }

    public /* synthetic */ void untrackWaypoint(Waypoint waypoint) {
        this.untrackWaypoint((TrackedWaypoint)waypoint);
    }

    public /* synthetic */ void trackWaypoint(Waypoint waypoint) {
        this.trackWaypoint((TrackedWaypoint)waypoint);
    }
}

