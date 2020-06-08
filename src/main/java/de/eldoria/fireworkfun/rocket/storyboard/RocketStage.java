package de.eldoria.fireworkfun.rocket.storyboard;

import de.eldoria.fireworkfun.FireworkFun;
import de.eldoria.fireworkfun.rocket.rockettypes.Rocket;
import de.eldoria.fireworkfun.util.SerializationUtil;
import de.eldoria.fireworkfun.util.TypeResolvingMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a rocket stage, which will be scheduled with the given tick amount.
 */
@SerializableAs("stage")
public class RocketStage implements ConfigurationSerializable {
    private final List<Rocket> rockets;
    private final int ticks;

    private RocketStage(List<Rocket> rockets, int ticks) {
        this.rockets = rockets;
        this.ticks = ticks;
    }

    public RocketStage(int ticks) {
        this.rockets = new ArrayList<>();
        this.ticks = ticks;
    }

    public void schedule(Location loc) {
        for (Rocket rocket : rockets) {
            Bukkit.getScheduler().runTaskLater(FireworkFun.getInstance(), () -> rocket.detonate(loc), ticks);
        }
    }

    public void addRocket(Rocket rocket) {
        rockets.add(rocket);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("tick", ticks)
                .add("rockets", rockets)
                .build();
    }

    public static RocketStage deserialize(Map<String, Object> map) {
        TypeResolvingMap resolvingMap = SerializationUtil.mapOf(map);
        List<Rocket> rockets = resolvingMap.getValue("rockets");
        int tick = resolvingMap.getValue("tick");
        return new RocketStage(rockets, tick);
    }
}
