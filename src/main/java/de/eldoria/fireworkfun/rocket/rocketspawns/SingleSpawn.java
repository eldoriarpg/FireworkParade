package de.eldoria.fireworkfun.rocket.rocketspawns;

import de.eldoria.fireworkfun.util.SerializationUtil;
import de.eldoria.fireworkfun.util.TypeResolvingMap;
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
    public List<Vector> getSpawnPoints() {
        return Collections.singletonList(offset);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder().add("offset", offset).build();
    }

    public static SingleSpawn deserialize(Map<String, Object> map){
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        Vector offset = resolvingMap.getValue("offset");
        return new SingleSpawn(offset);
    }
}
