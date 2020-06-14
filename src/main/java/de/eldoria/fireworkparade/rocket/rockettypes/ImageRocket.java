package de.eldoria.fireworkparade.rocket.rockettypes;

import de.eldoria.fireworkparade.FireworkParade;
import de.eldoria.fireworkparade.listener.ParticleMap;
import de.eldoria.fireworkparade.rocket.RocketType;
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
        super(height, RocketType.IMAGE);
        this.map = map;
    }

    @Override
    public void detonate(int tick, Location location) {
        if (map == null) {
            FireworkParade.getInstance().getLogger().warning("Image is missing for rocket.");
            return;
        }

        FireworkParade.getScheduler().scheduleImage(tick, location.add(0, getHeight(), 0), map);
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

    @Override
    public String getDescription() {
        return "Image: " + map.getName();
    }
}
