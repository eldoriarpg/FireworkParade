package de.eldoria.fireworkparade.rocket.rockettypes;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;

public interface Detonable {
    default void detonate(int tick, Location location) {
        throw new NotImplementedException();
    }

    default void detonate(Location location) {
        throw new NotImplementedException();
    }

    default void detonate() {
        throw new NotImplementedException();
    }

    default Firework spawnFirework(Location loc) {
        return (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
    }

}
