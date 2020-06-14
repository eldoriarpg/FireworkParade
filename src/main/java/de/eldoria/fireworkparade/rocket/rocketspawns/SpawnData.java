package de.eldoria.fireworkparade.rocket.rocketspawns;

import lombok.Data;
import org.bukkit.util.Vector;


@Data
public class SpawnData {
    private final Vector position;
    private final int tickDelay;
}
