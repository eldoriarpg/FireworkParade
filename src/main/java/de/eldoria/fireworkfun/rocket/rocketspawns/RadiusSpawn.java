package de.eldoria.fireworkfun.rocket.rocketspawns;

import de.eldoria.fireworkfun.util.SerializationUtil;
import de.eldoria.fireworkfun.util.TypeResolvingMap;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SerializableAs("radiusSpawn")
public class RadiusSpawn implements RocketSpawn {
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
    public List<Vector> getSpawnPoints() {
        List<Vector> vectors = new ArrayList<>();
        for (int point = 0; point < count; point++) {
            vectors.add(spawnForm.getSpawnPosition(count, point, radius));
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
}
