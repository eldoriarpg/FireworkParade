package de.eldoria.fireworkparade.commands;

import de.eldoria.fireworkparade.MessageSender;
import de.eldoria.fireworkparade.commands.rocketbuilder.BurstRocketBuilder;
import de.eldoria.fireworkparade.commands.rocketbuilder.ColoredRocketBuilder;
import de.eldoria.fireworkparade.commands.rocketbuilder.ImageRocketBuilder;
import de.eldoria.fireworkparade.commands.rocketbuilder.RocketBuilder;
import de.eldoria.fireworkparade.commands.rocketbuilder.RocketValue;
import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStage;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStoryboard;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StoryboardCreator {

    private final String name;
    private final int cooldown;
    private StoryboardCreatorState state;

    private final Map<Integer, RocketStage> stages = new HashMap<>();

    private RocketStage currentStage = null;

    private RocketBuilder currentRocket;


    private StoryboardCreator(String name, int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
    }

    public static StoryboardCreator newCreator(String name, int cooldown) {
        return new StoryboardCreator(name, cooldown);
    }

    public boolean isStageAlreadyDefined(int ticks) {
        return stages.containsKey(ticks);
    }

    public void newStage(int ticks) {
        if (currentStage != null) {
            stages.put(currentStage.getTicks(), currentStage);
        }
        if (stages.containsKey(ticks)) {
            currentStage = stages.get(ticks);
            return;
        }
        currentStage = new RocketStage(ticks);
        stages.put(ticks, currentStage);
    }

    public RocketBuilder newRocket(int height, RocketType type) {
        switch (type) {
            case IMAGE:
                currentRocket = new ImageRocketBuilder(height);
                break;
                //TODO implement rain
            //case RAIN:
            //    break;
            case BURST:
                currentRocket = BurstRocketBuilder.newRocketBuilder(height);
                break;
            case BALL:
            case BALL_LARGE:
            case CREEPER:
            case STAR:
                currentRocket = ColoredRocketBuilder.newRocketBuilder(height, type);
                break;
        }
        return currentRocket;
    }

    /**
     * A stage is defined when no rocket inside this stage is in creation process.
     *
     * @return true if the stage is defined and has at least one rocket.
     */
    public boolean isStageDefined() {
        return currentRocket == null && !currentStage.getRockets().isEmpty();
    }

    /**
     * A storyboard can be build if {@link #isStageDefined()} is true and at least one stage is defined.
     *
     * @return true if the storyboard can be build.
     */
    public boolean canBuild() {
        return isStageDefined() && !stages.isEmpty();
    }

    /**
     * Returns the current requested value of the rocket builder.
     *
     * @return requested value or null if no build is in progress
     */
    public RocketValue getCurrentRocketState() {
        if (currentRocket == null) return null;
        return currentRocket.getCurrentValue();
    }

    public void buildRocket() {
        currentStage.addRocket(currentRocket.build());
        currentRocket = null;
    }

    public void sendStateMessage(Player player) {
        if (currentRocket != null) {
            currentRocket.sendStateMessage(player);
            MessageSender.sendCommandExecution(player, "Cancel rocket creation.", C.cancelRocketCommand);
            return;
        }

        if (currentStage == null) {
            MessageSender.sendCommandSuggestion(player, "Add a stage", C.addStageCommand);
            return;
        }

        if (currentStage.getRockets().isEmpty()) {
            MessageSender.sendCommandSuggestion(player, "Add a new rocket.", C.addRocketCommand);
            return;
        }

        MessageSender.sendCommandSuggestion(player, "Add a new stage.", C.addStageCommand);
        MessageSender.sendCommandSuggestion(player, "Add a new rocket.", C.addRocketCommand);
        MessageSender.sendCommandExecution(player, "Save storyboard.", C.saveCommand);
    }

    public boolean setRocketValue(String[] args) {
        return currentRocket.setValue(args);
    }

    public RocketStoryboard build() {
        RocketStoryboard rocketStoryboard = new RocketStoryboard(name, cooldown);
        rocketStoryboard.addRocket(stages.values());
        return rocketStoryboard;
    }

    public void cancelRocket() {
        currentRocket = null;
    }

    public Collection<RocketStage> getStages() {
        return stages.values();
    }
}
