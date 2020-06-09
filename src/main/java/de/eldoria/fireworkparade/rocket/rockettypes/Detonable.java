package de.eldoria.fireworkparade.rocket.rockettypes;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;

public interface Detonable {
    void detonate(Location location);

    default Firework spawnFirework(Location loc) {
        return (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
    }

}
