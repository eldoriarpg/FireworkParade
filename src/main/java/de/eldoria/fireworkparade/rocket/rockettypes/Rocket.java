package de.eldoria.fireworkparade.rocket.rockettypes;

import de.eldoria.fireworkparade.rocket.RocketType;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@Getter
public abstract class Rocket implements ConfigurationSerializable, Detonable {
    private final int height;
    private final RocketType rocketType;

    public Rocket(int height, RocketType rocketType) {
        this.height = height;
        this.rocketType = rocketType;
    }

    public abstract String getDescription();
}
