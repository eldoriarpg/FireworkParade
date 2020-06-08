package de.eldoria.fireworkfun.rocket.rockettypes;

import de.eldoria.fireworkfun.rocket.rocketspawns.RocketSpawn;
import de.eldoria.fireworkfun.rocket.rocketspawns.SingleSpawn;
import de.eldoria.fireworkfun.util.SerializationUtil;
import de.eldoria.fireworkfun.util.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Firework;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Getter
@SerializableAs("burstRocket")
public class BurstRocket extends ColoredRocket {

    private final double spread;
    private final BurstDirection direction;

    protected BurstRocket(int height, double spread, BurstDirection direction, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn) {
        super(height, colors, fade, flicker, spawn, FireworkEffect.Type.BURST);
        this.spread = spread;
        this.direction = direction;
    }

    public static BurstRocket newBurstRocket(int height, double spread, BurstDirection direction, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn) {
        return new BurstRocket(height, spread, direction, colors, fade, flicker, spawn);
    }

    @Override
    public void detonate(Location location) {
        // Get the points where the rockets should be spawned
        List<Vector> spawnPoints = getRocketSpawn().getSpawnPoints();
        for (Vector spawnPoint : spawnPoints) {
            // Spawn a new firework at spawn location and height
            Firework firework = spawnFirework(location.clone().add(spawnPoint).add(0, getHeight(), 0));
            // set burst direction
            BurstDirection direction = getDirection();
            if (!(getRocketSpawn() instanceof SingleSpawn)) {
                firework.setVelocity(direction.getDirectionVector(spawnPoint).multiply(getSpread()));
            } else {
                switch (direction) {
                    case INSIDE:
                    case FUZZY_INSIDE:
                    case OUTSIDE:
                    case FUZZY_OUTSIDE:
                        firework.setVelocity(BurstDirection.RANDOM.getDirectionVector(spawnPoint).multiply(getSpread()));
                        break;
                    case DOWN:
                    case FUZZY_DOWN:
                    case UP:
                    case FUZZY_UP:
                    case RANDOM:
                        firework.setVelocity(direction.getDirectionVector(spawnPoint).multiply(getSpread()));
                        break;
                }
            }
            buildEffectAndDetonate(firework);
        }
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> serialize = super.serialize();
        serialize.put("spread", spread);
        serialize.put("direction", direction.toString());
        return serialize;
    }

    public static BurstRocket deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        int height = resolvingMap.getValue("height");
        double spread = resolvingMap.getValue("spread");
        List<Color> colors = resolvingMap.getValue("colors");
        List<Color> fadeColors = resolvingMap.getValue("fadeColors");
        boolean flicker = resolvingMap.getValue("flicker");
        RocketSpawn spawn = resolvingMap.getValue("spawn");
        BurstDirection burstDirection = resolvingMap.getValue("direction", s -> BurstDirection.valueOf(s.toUpperCase()));

        return new BurstRocket(height, spread, burstDirection, colors.toArray(new Color[0]), fadeColors.toArray(new Color[0]), flicker, spawn);

    }
}
