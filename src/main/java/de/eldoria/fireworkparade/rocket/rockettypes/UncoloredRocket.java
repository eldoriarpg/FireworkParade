package de.eldoria.fireworkparade.rocket.rockettypes;

import de.eldoria.fireworkparade.rocket.rocketspawns.RocketSpawn;
import lombok.Getter;

@Getter
public abstract class UncoloredRocket extends Rocket {
    private final RocketSpawn rocketSpawn;

    public UncoloredRocket(int height, RocketSpawn rocketSpawn) {
        super(height);
        this.rocketSpawn = rocketSpawn;
    }
}
