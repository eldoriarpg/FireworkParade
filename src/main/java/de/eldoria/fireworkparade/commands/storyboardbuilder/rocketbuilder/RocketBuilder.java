package de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder;

import de.eldoria.fireworkparade.MessageSender;
import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rockettypes.Rocket;
import de.eldoria.fireworkparade.util.C;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.bukkit.entity.Player;

public abstract class RocketBuilder {
    protected final int height;
    protected final RocketType type;
    protected RocketValue currentValue;

    public RocketBuilder(int height, RocketType type, RocketValue initialState) {
        this.height = height;
        this.type = type;
        currentValue = initialState;
    }

    public abstract boolean setValue(String[] value);

    public final boolean buildable() {
        return currentValue == RocketValue.DONE;
    }

    public abstract Rocket build();

    public RocketValue getCurrentValue() {
        return currentValue;
    }

    public void sendStateMessage(Player player) {
        TextComponent.Builder builder = TextComponent.builder().color(TextColor.GOLD);
        switch (currentValue) {
            case IMAGE:
                MessageSender.sendCommandSuggestion(player, "Please provide a image name.", C.addValueCommand);
                break;
            case COLOR:
                MessageSender.sendCommandSuggestion(player, "Please provide one or more colors.", C.addValueCommand);
                break;
            case FADE_COLOR:
                MessageSender.sendCommandSuggestion(player, "Please provide fade colors or leave empty.", C.addValueCommand);
                break;
            case FLICKER:
                MessageSender.sendCommandSuggestion(player, "Should the rocket flicker or not.", C.addValueCommand);
                break;
            case SPREAD:
                MessageSender.sendCommandSuggestion(player, "Please provide a spread value for burst.", C.addValueCommand);
                break;
            case BURST_DIRECTION:
                MessageSender.sendCommandSuggestion(player, "Please provide a burst direction.", C.addValueCommand);
                break;
            case SPAWN:
                MessageSender.sendCommandSuggestion(player, "Please provide a spawn method.", C.addValueCommand);
                break;
            case DONE:
                MessageSender.sendMessage(player, "Rocket is created and saved.");
                MessageSender.sendCommandSuggestion(player, "Add new Rocket.", C.addRocketCommand);
                MessageSender.sendCommandSuggestion(player, "Add new Stage.", C.addStageCommand);
                MessageSender.sendCommandExecution(player, "Save.", C.saveCommand);
                return;
        }
        MessageSender.sendCommandExecution(player, "Cancel rocket creation.", C.cancelRocketCommand);
    }
}
