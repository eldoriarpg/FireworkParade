package de.eldoria.fireworkfun.rocket.rockettypes;

import de.eldoria.fireworkfun.rocket.rocketspawns.RocketSpawn;
import de.eldoria.fireworkfun.util.SerializationUtil;
import de.eldoria.fireworkfun.util.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Getter
@SerializableAs("coloredRocket")
public class ColoredRocket extends UncoloredRocket {
    private final Color[] colors;
    private final Color[] fade;
    private final boolean flicker;
    private final FireworkEffect.Type type;

    protected ColoredRocket(int height, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn, FireworkEffect.Type type) {
        super(height, spawn);
        this.colors = colors;
        this.fade = fade;
        this.flicker = flicker;
        this.type = type;
    }

    public static ColoredRocket newBallRocket(int height, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn) {
        return new ColoredRocket(height, colors, fade, flicker, spawn, FireworkEffect.Type.BALL);
    }

    public static ColoredRocket newLargeBallRocket(int height, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn) {
        return new ColoredRocket(height, colors, fade, flicker, spawn, FireworkEffect.Type.BALL_LARGE);
    }

    public static ColoredRocket newCreeperRocket(int height, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn) {
        return new ColoredRocket(height, colors, fade, flicker, spawn, FireworkEffect.Type.CREEPER);
    }

    public static ColoredRocket newStarRocket(int height, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn) {
        return new ColoredRocket(height, colors, fade, flicker, spawn, FireworkEffect.Type.STAR);
    }

    protected void buildEffectAndDetonate(Firework firework) {
        FireworkMeta meta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .with(type)
                .withColor(getColors())
                .withFade(getFade())
                .flicker(isFlicker())
                .build();

        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        firework.detonate();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("height", getHeight())
                .add("colors", colors)
                .add("fadeColors", fade)
                .add("flicker", flicker)
                .add("type", type)
                .add("spawn", getRocketSpawn())
                .build();
    }

    public static ColoredRocket deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        int height = resolvingMap.getValue("height");
        List<Color> colors = resolvingMap.getValue("colors");
        List<Color> fadeColors = resolvingMap.getValue("fadeColors");
        boolean flicker = resolvingMap.getValue("flicker");
        RocketSpawn spawn = resolvingMap.getValue("spawn");
        FireworkEffect.Type type = resolvingMap.getValue("type", s -> FireworkEffect.Type.valueOf(s.toUpperCase()));
        return new ColoredRocket(height, colors.toArray(new Color[0]), fadeColors.toArray(new Color[0]), flicker, spawn, type);
    }

    @Override
    public void detonate(Location location) {
        for (Vector spawnPoint : getRocketSpawn().getSpawnPoints()) {
            Location spawn = location.clone().add(spawnPoint).add(0, getHeight(), 0);
            buildEffectAndDetonate(spawnFirework(spawn));
        }
    }
}
