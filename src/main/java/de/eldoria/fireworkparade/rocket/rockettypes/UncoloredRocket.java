package de.eldoria.fireworkparade.rocket.rockettypes;

import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rocketspawns.RocketSpawn;
import lombok.Getter;

@Getter
public abstract class UncoloredRocket extends Rocket {
    private final RocketSpawn rocketSpawn;

    public UncoloredRocket(int height, RocketSpawn rocketSpawn, RocketType rocketType) {
        super(height, rocketType);
        this.rocketSpawn = rocketSpawn;
    }
}
