package de.eldoria.fireworkparade.rocket.rockettypes;

import de.eldoria.fireworkparade.FireworkParade;
import de.eldoria.fireworkparade.listener.ParticleMap;
import de.eldoria.fireworkparade.util.SerializationUtil;
import de.eldoria.fireworkparade.util.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@Getter
@SerializableAs("imageRocket")
public class ImageRocket extends Rocket implements ConfigurationSerializable {
    private final ParticleMap map;

    public ImageRocket(int height, ParticleMap map) {
        super(height);
        this.map = map;
    }

    @Override
    public void detonate(Location location) {
        map.spawnMap(location.add(0, getHeight(), 0));
    }

    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("height", getHeight())
                .add("name", map.getName())
                .build();
    }

    public static ImageRocket deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        ParticleMap particleMap = FireworkParade.getImageLib().getParticleMap(resolvingMap.getValue("name"));
        int height = resolvingMap.getValue("height");
        return new ImageRocket(height, particleMap);
    }
}
