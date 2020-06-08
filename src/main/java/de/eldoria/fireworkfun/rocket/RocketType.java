package de.eldoria.fireworkfun.rocket;

import org.bukkit.FireworkEffect;

public enum RocketType {
    IMAGE("image"),
    RAIN("rain"),
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
}
