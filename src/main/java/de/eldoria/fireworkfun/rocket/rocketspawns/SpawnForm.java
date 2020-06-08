package de.eldoria.fireworkfun.rocket.rocketspawns;


import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public enum SpawnForm {
    CYCLE, RANDOM_SQUARE, RANDOM_CYCLE, RANDOM_SPHERE;

    public Vector getSpawnPosition(int points, int point, int radius) {
        switch (this) {
            case CYCLE:
                return getCyclePoint(points, point, radius);
            case RANDOM_SQUARE:
                return getRandomSquarePoint(points, point, radius);
            case RANDOM_CYCLE:
                return getRandomCyclePoint(points, point, radius);
            case RANDOM_SPHERE:
                return getRandomSpherePoint(points, point, radius);
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    private Vector getCyclePoint(int points, int point, double radius) {
        double degreePerPoint = 360d / points;
        double degrees = degreePerPoint * point;
        double x = 0 + (1 * Math.cos(degrees * Math.PI / 180));
        double y = 0 + (1 * Math.sin(degrees * Math.PI / 180));
        return new Vector(x, 0, y).normalize().multiply(radius);
    }

    private Vector getRandomSquarePoint(int points, int point, double radius) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        double x = current.nextDouble(-radius, radius);
        double z = current.nextDouble(-radius, radius);
        return new Vector(x, 0, z);
    }

    private Vector getRandomCyclePoint(int points, int point, double radius) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double degrees = random.nextInt(360);
        double x = 0 + (1 * Math.cos(degrees * Math.PI / 180));
        double y = 0 + (1 * Math.sin(degrees * Math.PI / 180));
        return new Vector(x, 0, y).normalize().multiply(random.nextDouble(radius));
    }

    private Vector getRandomSpherePoint(int points, int point, int radius) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double x = random.nextDouble(-1, 1);
        double y = random.nextDouble(-1, 1);
        double z = random.nextDouble(-1, 1);
        return new Vector(x, y, z).normalize().multiply(random.nextDouble(radius));
    }

    public static SpawnForm parse(String value) {
        for (SpawnForm spawnForm : values()) {
            if (spawnForm.toString().equalsIgnoreCase(value)) {
                return spawnForm;
            }
        }
        return null;
    }
}
