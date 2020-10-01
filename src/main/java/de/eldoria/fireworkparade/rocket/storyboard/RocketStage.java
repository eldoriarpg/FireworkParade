package de.eldoria.fireworkparade.rocket.storyboard;

import de.eldoria.fireworkparade.MessageSender;
import de.eldoria.fireworkparade.rocket.rockettypes.Rocket;
import de.eldoria.fireworkparade.util.serialization.SerializationUtil;
import de.eldoria.fireworkparade.util.serialization.TypeResolvingMap;
import lombok.Getter;
import lombok.Setter;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.Style;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a rocket stage, which will be scheduled with the given tick amount.
 */
@SerializableAs("stage")
@Getter
public class RocketStage implements ConfigurationSerializable {
    private final List<Rocket> rockets;
    @Setter
    private int ticks;

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
            rocket.detonate(ticks, loc.clone());
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

    public void removeRocket(int id) {
        rockets.remove(id);
    }

    public void sendStageInfo(Player p){
        int id = 0;
        for (Rocket rocket : getRockets()) {
            TextComponent rocketType = TextComponent
                    .builder(rocket.getRocketType() + " rocket at height: " + rocket.getHeight(), TextColor.DARK_GREEN)
                    .hoverEvent(HoverEvent.
                            showText(TextComponent
                                    .builder(rocket.getDescription(), TextColor.GREEN)
                                    .build()))
                    .build();
            TextComponent remove = TextComponent
                    .builder("[remove]")
                    .style(Style.builder().color(TextColor.RED).clickEvent(ClickEvent.runCommand("/fpc removeRocket " + id)).decoration(TextDecoration.UNDERLINED, true).build())
                    .build();
            MessageSender.sendTextComponents(p, rocketType, TextComponent.builder(" ").build(), remove);
            id++;
        }

    }
}
