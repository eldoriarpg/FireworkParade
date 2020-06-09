package de.eldoria.fireworkparade.listener;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ParticleMap {
    private final Color[][] colors;
    private final int width;
    private final int height;
    private final String name;

    public ParticleMap(String name) {
        this.name = name;
        colors = new Color[0][0];
        width = 0;
        height = 0;
    }

    public ParticleMap(String name, Color[][] colors) {
        this.name = name;
        this.colors = colors;
        width = colors[1].length;
        height = colors[0].length;
    }

    public void spawnMap(Location location) {
        double angle = (ThreadLocalRandom.current().nextInt(360));
        double halfWidth = this.width / 2d;
        double halfHeight = this.height / 2d;

        List<Location> spawnLocations = new ArrayList<>();

        for (int width = 0; width < this.width; width++) {
            for (int height = 0; height < this.height; height++) {
                if (colors[width][height] == Color.BLACK) continue;
                Location loc = location.clone();
                spawnLocations.add(loc.add(rotateAroundZero(
                        new Vector((width - halfWidth) / 4, 0, (height - halfHeight) / 4), angle)));
            }
        }

        // If a image will be spawned in the same tick where the rockets are spawned the image will disappear directly
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {

        }

        for (Location spawnLocation : spawnLocations) {
            location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, spawnLocation, 0, 0, 0, 0, 0, null, true);
        }

    }

    protected Vector rotateAroundZero(Vector vector, double degree) {
        double angle = degree * (Math.PI / 180);
        double rotatedX = Math.cos(angle) * (vector.getX() - 0) - Math.sin(angle) * (vector.getZ() - 0) + 0;
        double rotatedZ = Math.sin(angle) * (vector.getX() - 0) + Math.cos(angle) * (vector.getZ() - 0) + 0;
        return new Vector(Math.round(rotatedX * 100) / 100d, 0, Math.round(rotatedZ * 100) / 100d);
    }

    public String getName() {
        return name;
    }
}
