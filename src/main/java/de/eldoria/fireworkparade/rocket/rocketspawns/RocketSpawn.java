package de.eldoria.fireworkparade.rocket.rocketspawns;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import java.util.List;

public interface RocketSpawn extends ConfigurationSerializable {
    public List<Vector> getSpawnPoints();
}

