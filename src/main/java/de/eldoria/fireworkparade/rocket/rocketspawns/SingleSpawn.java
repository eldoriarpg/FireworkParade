package de.eldoria.fireworkparade.rocket.rocketspawns;

import de.eldoria.fireworkparade.util.SerializationUtil;
import de.eldoria.fireworkparade.util.TypeResolvingMap;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SerializableAs("singleSpawn")
public class SingleSpawn implements RocketSpawn {
    private final Vector offset;

    public SingleSpawn(Vector offset) {
        this.offset = offset;
    }

    @Override
    public List<SpawnData> getSpawnPoints() {
        return Collections.singletonList(new SpawnData(offset, 0));
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder().add("offset", offset).build();
    }

    public static SingleSpawn deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        Vector offset = resolvingMap.getValue("offset");
        return new SingleSpawn(offset);
    }
}
