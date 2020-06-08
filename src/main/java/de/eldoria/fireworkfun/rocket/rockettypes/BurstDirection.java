package de.eldoria.fireworkfun.rocket.rockettypes;

import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public enum BurstDirection {
    INSIDE(getInside()),
    FUZZY_INSIDE(fuzzyInside()),
    OUTSIDE(getOutside()),
    FUZZY_OUTSIDE(fuzzyOutside()),
    DOWN(getDown()),
    FUZZY_DOWN(fuzzyDown()),
    UP(getUp()),
    FUZZY_UP(fuzzyUp()),
    RANDOM(getRandom());

    private final Function<Vector, Vector> functions;

    BurstDirection(Function<Vector, Vector> functions) {
        this.functions = functions;
    }

    public Vector getDirectionVector(Vector vector) {
        Vector normalize = this.functions.apply(vector).normalize();
        return new Vector(Math.round(normalize.getX() * 100) / 100d, 0, Math.round(normalize.getZ() * 100) / 100d);
    }

    private static Function<Vector, Vector> getInside() {
        return (targetVector) -> targetVector.multiply(-1);
    }

    private static Function<Vector, Vector> getOutside() {
        return (targetVector) -> targetVector;
    }

    private static Function<Vector, Vector> getUp() {
        return (targetVector) -> new Vector(0, -1, 0);
    }

    private static Function<Vector, Vector> getDown() {
        return (targetVector) -> new Vector(0, -1, 0);
    }

    private static Function<Vector, Vector> getRandom() {
        return (targetVector) -> {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            double x = random.nextInt(-100, 100);
            double y = random.nextInt(-100, 100);
            double z = random.nextInt(-100, 100);
            return new Vector(x, y, z);
        };
    }

    private static Function<Vector, Vector> fuzzyInside() {
        return (targetVector) -> {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Vector vector = getInside().apply(targetVector);
            vector.add(new Vector(0, getFuzzyDouble(), 0));
            return vector;
        };
    }

    private static Function<Vector, Vector> fuzzyOutside() {
        return (targetVector) -> {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Vector vector = getOutside().apply(targetVector);
            vector.add(new Vector(0, getFuzzyDouble(), 0));
            return vector;
        };
    }

    private static Function<Vector, Vector> fuzzyDown() {
        return (targetVector) -> {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Vector vector = getDown().apply(targetVector);
            vector.add(new Vector(getFuzzyDouble(), vector.getY(), getFuzzyDouble()));
            return vector;
        };
    }

    private static Function<Vector, Vector> fuzzyUp() {
        return (targetVector) -> {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            Vector vector = getUp().apply(targetVector);
            vector.add(new Vector(getFuzzyDouble(), vector.getY(), getFuzzyDouble()));
            return vector;
        };
    }

    private static double getFuzzyDouble() {
        return ThreadLocalRandom.current().nextInt(-200, 200) / 1000d;
    }

    public static BurstDirection parse(String value) {
        for (BurstDirection burstDirection : values()) {
            if (burstDirection.toString().equalsIgnoreCase(value)) {
                return burstDirection;
            }
        }
        return null;
    }
}
