package de.eldoria.fireworkparade.rocket;

import org.bukkit.FireworkEffect;

import java.util.Arrays;

public enum RocketType {
    IMAGE("image"),
    //RAIN("rain"),
    BURST("burst", FireworkEffect.Type.BURST),
    BALL("ball", FireworkEffect.Type.BALL),
    BALL_LARGE("ballLarge", FireworkEffect.Type.BALL_LARGE),
    CREEPER("creeper", FireworkEffect.Type.CREEPER),
    STAR("star", FireworkEffect.Type.STAR);

    private final FireworkEffect.Type form;
    private final String configName;

    RocketType(String configName) {
        this.configName = configName;
        form = null;
    }

    RocketType(String configName, FireworkEffect.Type type) {
        this.configName = configName;
        form = type;
    }

    public static RocketType wrap(FireworkEffect.Type type) {
        switch (type) {
            case BALL:
                return BALL;
            case BALL_LARGE:
                return BALL_LARGE;
            case STAR:
                return STAR;
            case BURST:
                return BURST;
            case CREEPER:
                return CREEPER;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * Get the form of the firework.
     *
     * @return form of firework or null if explode form is {@link #IMAGE}
     */
    public FireworkEffect.Type getForm() {
        return this.form;
    }

    @Override
    public String toString() {
        return configName;
    }

    public static RocketType parse(String value) {
        for (RocketType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) return type;
        }
        return null;
    }

    public static String[] asStringArray() {
        return Arrays.stream(values()).map(RocketType::toString).toArray(String[]::new);
    }

}
