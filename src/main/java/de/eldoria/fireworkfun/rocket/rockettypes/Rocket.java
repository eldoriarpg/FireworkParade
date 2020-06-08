package de.eldoria.fireworkfun.rocket.rockettypes;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@Getter
public abstract class Rocket implements ConfigurationSerializable, Detonable {
    private final int height;

    public Rocket(int height) {
        this.height = height;
    }


}
