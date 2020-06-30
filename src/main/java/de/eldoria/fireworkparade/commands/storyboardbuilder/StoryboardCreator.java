package de.eldoria.fireworkparade.commands.storyboardbuilder;

import de.eldoria.fireworkparade.MessageSender;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.BurstRocketBuilder;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.ColoredRocketBuilder;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.ImageRocketBuilder;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.RocketBuilder;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.RocketValue;
import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStage;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStoryboard;
import de.eldoria.fireworkparade.util.C;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.Style;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StoryboardCreator {

    private final String name;
    private final double cooldown;
    private StoryboardCreatorState state;

    private final Map<Integer, RocketStage> stages = new HashMap<>();

    private RocketStage currentStage = null;

    private RocketBuilder currentRocket;


    private StoryboardCreator(String name, double cooldown) {
        this.name = name;
        this.cooldown = cooldown;
    }

    /**
     * Create a new storyboard creator
     *
     * @param name     name of rocket with color codes
     * @param cooldown cooldown of rocket
     * @return storyboard creator with name and cooldown set.
     */
    public static StoryboardCreator newCreator(String name, double cooldown) {
        return new StoryboardCreator(name.replace("$", "§"), cooldown);
    }

    /**
     * Create a new StoryboardCreator based on a storyboard to edit it.
     *
     * @param storyboard storyboard to edit
     * @return Storyboard creator instance with values of storyboard applied
     */
    public static StoryboardCreator newCreatorFromStoryboard(RocketStoryboard storyboard) {
        StoryboardCreator creator = new StoryboardCreator(storyboard.getName(), storyboard.getCooldown());
        storyboard.getStages().forEach(s -> creator.stages.put(s.getTicks(), s));
        return creator;
    }

    public boolean isStageAlreadyDefined(int ticks) {
        return stages.containsKey(ticks);
    }

    /**
     * Creates a new stage or loads a existing stage.
     *
     * @param ticks ticks of stage
     */
    public void getOrCreateStage(int ticks) {
        // save previous stage
        if (currentStage != null) {
            stages.put(currentStage.getTicks(), currentStage);
        }
        if (stages.containsKey(ticks)) {
            // remove stage to save it with another tick if changed.
            currentStage = stages.remove(ticks);
            return;
        }
        currentStage = new RocketStage(ticks);
        stages.put(ticks, currentStage);
    }

    /**
     * Create a new rocket in the current selected stage
     *
     * @param height height of rocket
     * @param type   type of rocket
     * @return rocket builder with type and height set
     */
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
        if (currentStage == null) return true;
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

    /**
     * Build a rocket and adds it to the current stage.
     */
    public void buildRocket() {
        currentStage.addRocket(currentRocket.build());
        currentRocket = null;
    }

    /**
     * sends the state message of the builder
     *
     * @param player player which should receive the message
     */
    public void sendStateMessage(Player player) {
        if (currentRocket != null) {
            currentRocket.sendStateMessage(player);
            return;
        }

        if (currentStage == null) {
            MessageSender.sendCommandSuggestion(player, "Add a stage", C.addStageCommand);
            return;
        }

        if (currentStage.getRockets().isEmpty()) {
            MessageSender.sendCommandSuggestion(player, "Add a new rocket.", C.addRocketCommand);
            MessageSender.sendCommandSuggestion(player, "Cancel stage.", C.cancelStageCommand);
            return;
        }

        MessageSender.sendCommandSuggestion(player, "Add a new stage.", C.addStageCommand);
        MessageSender.sendCommandSuggestion(player, "Add a new rocket.", C.addRocketCommand);
        MessageSender.sendCommandExecution(player, "Stage info.", C.stageInfo);
        MessageSender.sendCommandExecution(player, "Storyboard info", C.storyboardInfo);
        MessageSender.sendCommandExecution(player, "Save storyboard.", C.saveCommand);
    }

    /**
     * Sets a value to the current rocket builder
     *
     * @param args args to set
     * @return true if the value was successfuly set.
     */
    public boolean setRocketValue(String[] args) {
        return currentRocket.setValue(args);
    }

    /**
     * Build the storyboard.
     *
     * @return Rocket storyboard
     */
    public RocketStoryboard build() {
        RocketStoryboard rocketStoryboard = new RocketStoryboard(name, cooldown);
        rocketStoryboard.addRocket(stages.values());
        return rocketStoryboard;
    }

    /**
     * Cancel creation of current rocket.
     */
    public void cancelRocket() {
        currentRocket = null;
    }

    /**
     * Get the stages of the rocket.
     *
     * @return
     */
    public Collection<RocketStage> getStages() {
        return stages.values();
    }

    /**
     * Check if this stage already exists.
     *
     * @param ticks ticks to check
     * @return true if stage exists
     */
    public boolean hasStage(int ticks) {
        return stages.containsKey(ticks);
    }

    /**
     * Get the current rocket stage.
     *
     * @return current rocket stage
     */
    public RocketStage getCurrentStage() {
        return currentStage;
    }

    /**
     * Delete a stage.
     *
     * @param ticks ticks of stage which should be deleted.
     */
    public void deleteStage(int ticks) {
        if (currentStage.getTicks() == ticks) {
            currentStage = null;
        }
        stages.remove(ticks);
    }

    /**
     * Cancel creation of a stage
     */
    public void cancelStage() {
        currentStage = null;
    }

    public void sendStoryboardInfo(Player player){
        List<RocketStage> stages = new ArrayList<>(getStages());
        stages.sort(Comparator.comparingInt(RocketStage::getTicks));
        List<TextComponent> textComponents = new ArrayList<>();
        for (RocketStage stage : stages) {
            TextComponent stageTicks = TextComponent
                    .builder(stage.getTicks() + " Ticks", TextColor.DARK_GREEN)
                    .hoverEvent(HoverEvent.
                            showText(TextComponent
                                    .builder(stage.getRockets().size() + " rockets. ", TextColor.GREEN)
                                    .build()))
                    .build();
            TextComponent select = TextComponent
                    .builder("[select]")
                    .style(Style.builder().color(TextColor.AQUA).clickEvent(ClickEvent.runCommand("/fpc addStage " + stage.getTicks())).decoration(TextDecoration.UNDERLINED, true).build())
                    .build();
            TextComponent remove = TextComponent
                    .builder("[remove]")
                    .style(Style.builder().color(TextColor.RED).clickEvent(ClickEvent.runCommand("/fpc removeStage " + stage.getTicks())).decoration(TextDecoration.UNDERLINED, true).build())
                    .build();
            MessageSender.sendTextComponents(player, stageTicks, select, TextComponent.builder(" ").build(), remove);
        }

    }

    public RocketBuilder getCurrentRocket() {
        return currentRocket;
    }
}
