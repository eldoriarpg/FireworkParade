package de.eldoria.fireworkparade.rocket.rockettypes;

import de.eldoria.fireworkparade.FireworkParade;
import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rocketspawns.RocketSpawn;
import de.eldoria.fireworkparade.rocket.rocketspawns.SpawnData;
import de.eldoria.fireworkparade.util.C;
import de.eldoria.fireworkparade.util.ColorUtil;
import de.eldoria.fireworkparade.util.serialization.SerializationUtil;
import de.eldoria.fireworkparade.util.serialization.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@SerializableAs("coloredRocket")
public class ColoredRocket extends UncoloredRocket {
    private final Color[] colors;
    private final Color[] fade;
    private final boolean flicker;
    private final FireworkEffect.Type type;

    protected ColoredRocket(int height, Color[] colors, Color[] fade, boolean flicker, RocketSpawn spawn, FireworkEffect.Type type) {
        super(height, spawn, RocketType.wrap(type));
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

    protected void buildEffectAndSchedule(int tick, Location spawn) {
        buildEffectAndSchedule(tick, spawn, new Vector());
    }

    protected void buildEffectAndSchedule(int tick, Location location, Vector velocity) {
        FireworkEffect effect = FireworkEffect.builder()
                .with(type)
                .withColor(getColors())
                .withFade(getFade())
                .flicker(isFlicker())
                .build();

        FireworkParade.getScheduler().scheduleRocket(tick, location, velocity, effect);
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
    public void detonate(int tick, Location location) {
        for (SpawnData spawnPoint : getRocketSpawn().getSpawnPoints()) {
            Location spawn = location.clone().add(spawnPoint.getPosition()).add(0, getHeight(), 0);
            buildEffectAndSchedule(tick + spawnPoint.getTickDelay(), spawn);
        }
    }

    @Override
    public String getDescription() {
        return "Colors: " + Arrays.stream(colors).map(ColorUtil::colorToString).collect(Collectors.joining(",")) + C.NEW_LINE
                + "Fade Colors: " + Arrays.stream(fade).map(ColorUtil::colorToString).collect(Collectors.joining(",")) + C.NEW_LINE
                + "Flicker: " + flicker + C.NEW_LINE
                + getRocketSpawn().toString();
    }
}
