package de.eldoria.fireworkfun.rocket.storyboard;

import de.eldoria.fireworkfun.util.SerializationUtil;
import de.eldoria.fireworkfun.util.TypeResolvingMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@SerializableAs("storyboard")
public class RocketStoryboard implements ConfigurationSerializable {
    private final String name;
    private final double cooldown;
    private final List<RocketStage> stages;

    private RocketStoryboard(String name, double cooldown, List<RocketStage> stages) {
        this.name = name;
        this.cooldown = cooldown;
        this.stages = stages;
    }

    public RocketStoryboard(String name, double cooldown) {
        this.name = name;
        this.cooldown = cooldown;
        this.stages = new ArrayList<>();
    }

    public void fire(Location location) {
        ExecutedRocketStoryboard.createAndExecute(location, this);
    }

    public List<RocketStage> getStages() {
        return stages;
    }

    public void addRocket(RocketStage stage) {
        stages.add(stage);
    }


    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("name", name)
                .add("cooldown", cooldown)
                .add("stages", stages)
                .build();
    }

    public static RocketStoryboard deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        String name = resolvingMap.getValue("name");
        double cooldown = resolvingMap.getValue("cooldown");
        List<RocketStage> stages = resolvingMap.getValue("stages");
        return new RocketStoryboard(name,cooldown, stages);
    }
}
