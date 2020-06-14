package de.eldoria.fireworkparade.rocket.rocketspawns;

import de.eldoria.fireworkparade.util.C;
import de.eldoria.fireworkparade.util.SerializationUtil;
import de.eldoria.fireworkparade.util.TypeResolvingMap;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SerializableAs("radiusSpawn")
public final class RadiusSpawn implements RocketSpawn {
    private final SpawnForm spawnForm;
    private final int count;
    private final int radius;

    private RadiusSpawn(SpawnForm spawnForm, int count, int radius) {
        this.spawnForm = spawnForm;
        this.count = count;
        this.radius = radius;
    }

    public static RadiusSpawn newSpawn(SpawnForm spawnForm, int count, int radius) {
        return new RadiusSpawn(spawnForm, count, radius);
    }

    @Override
    public List<SpawnData> getSpawnPoints() {
        List<SpawnData> vectors = new ArrayList<>();
        for (int point = 0; point < count; point++) {
            vectors.add(new SpawnData(spawnForm.getSpawnPosition(count, point, radius), 0));
        }
        return vectors;
    }

    public SpawnForm getSpawnForm() {
        return spawnForm;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("type", spawnForm)
                .add("count", count)
                .add("radius", radius)
                .build();
    }

    public static RadiusSpawn deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        SpawnForm type = resolvingMap.getValue("type", s -> SpawnForm.valueOf(s.toUpperCase()));
        int count = resolvingMap.getValue("count");
        int radius = resolvingMap.getValue("radius");
        return new RadiusSpawn(type, count, radius);
    }

    @Override
    public String toString() {
        return "Radius spawn: " + spawnForm + C.NEW_LINE
                + "Radius: " + radius + C.NEW_LINE
                + "Rocket Count: " + count;
    }
}
