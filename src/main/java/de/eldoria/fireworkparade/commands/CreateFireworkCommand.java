package de.eldoria.fireworkparade.commands;

import de.eldoria.fireworkparade.FireworkParade;
import de.eldoria.fireworkparade.MessageSender;
import de.eldoria.fireworkparade.commands.storyboardbuilder.StoryboardCreator;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.ColoredRocketBuilder;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.RocketValue;
import de.eldoria.fireworkparade.listener.ImageLib;
import de.eldoria.fireworkparade.listener.StoryboardLib;
import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rocketspawns.RadiusSpawn;
import de.eldoria.fireworkparade.rocket.rocketspawns.RocketSpawn;
import de.eldoria.fireworkparade.rocket.rocketspawns.SingleSpawn;
import de.eldoria.fireworkparade.rocket.rocketspawns.SpawnForm;
import de.eldoria.fireworkparade.rocket.rockettypes.BurstDirection;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStage;
import de.eldoria.fireworkparade.util.ArrayUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CreateFireworkCommand implements TabExecutor {

    private final Map<UUID, StoryboardCreator> creatorMap = new HashMap<>();
    private final Logger logger = FireworkParade.getInstance().getLogger();
    private final StoryboardLib storyboardLib;
    private final ImageLib imageLib;

    private static final String[] COLORS = {"black", "dark_blue", "dark_green", "dark_aqua", "dark_red",
            "dark_purple", "gold", "gray", "dark_gray", "blue",
            "green", "aqua", "red", "light_purple", "yellow", "white"};

    public CreateFireworkCommand(StoryboardLib storyboardLib, ImageLib imageLib) {
        this.storyboardLib = storyboardLib;
        this.imageLib = imageLib;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            logger.info("This command can not be executed by console.");
            return true;
        }
        if (args.length == 0) {
            return false;
        }

        Player player = (Player) sender;

        String cmd = args[0];

        String[] values = Arrays.copyOfRange(args, 1, args.length);

        if (creatorMap.containsKey(player.getUniqueId())) {
            StoryboardCreator creator = creatorMap.get(player.getUniqueId());
            if ("setValue".equalsIgnoreCase(cmd)) {
                if (creator.getCurrentRocketState() == null) {
                    MessageSender.sendError(player, "You are currently not creating a rocket.");
                    return true;
                }

                RocketValue lastState = creator.getCurrentRocketState();

                boolean success = creator.setRocketValue(values);

                if (!success) {
                    MessageSender.sendError(player, "Invalid input.");
                    return true;
                }


                switch (lastState) {
                    case IMAGE:
                        MessageSender.sendMessage(player, "Image set to §3" + String.join(" ", values));
                        break;
                    case COLOR:
                        MessageSender.sendMessage(player, "Color set to §3" + String.join(", ", values));
                        break;
                    case FADE_COLOR:
                        if (values.length == 0) {
                            MessageSender.sendMessage(player, "No fade colors set.");
                        } else {
                            MessageSender.sendMessage(player, "Fade colors set to §3" + String.join(", ", values));
                        }
                        break;
                    case FLICKER:
                        MessageSender.sendMessage(player, "Rocket flicker set to §3" + values[0]);
                        break;
                    case SPREAD:
                        MessageSender.sendMessage(player, "Spread set to §3" + values[0]);
                        break;
                    case BURST_DIRECTION:
                        MessageSender.sendMessage(player, "Burst direction set to §3" + values[0].toLowerCase());
                        break;
                    case SPAWN:
                        if (creator.getCurrentRocket() instanceof ColoredRocketBuilder) {
                            ColoredRocketBuilder currentRocket = (ColoredRocketBuilder) creator.getCurrentRocket();
                            RocketSpawn spawn = currentRocket.getSpawn();
                            if (spawn instanceof SingleSpawn) {
                                MessageSender.sendMessage(player, "New §3center spawn created");
                            }
                            if (spawn instanceof RadiusSpawn) {
                                MessageSender.sendMessage(player, "New §3"
                                        + ((RadiusSpawn) spawn).getSpawnForm().name().toLowerCase()
                                        + "§r spawn created");
                            }
                        }
                        break;
                }

                if (creator.getCurrentRocketState() == RocketValue.DONE) {
                    creator.sendStateMessage(player);
                    creator.buildRocket();
                    return true;
                }

                creator.sendStateMessage(player);
                return true;
            }

            if ("addStage".equalsIgnoreCase(cmd)) {
                if (values.length != 1) {
                    MessageSender.sendError(player, "Please provide a stage tick");
                    return true;
                }

                if (!creator.isStageDefined()) {
                    MessageSender.sendError(player, "The current stage is incomplete. You have to complete it first.");
                    creator.sendStateMessage(player);
                    return true;
                }

                int ticks;
                try {
                    ticks = Integer.parseInt(values[0]);
                    if (ticks < 0 || ticks > 7200) {
                        MessageSender.sendError(player, "This is not a valid tick value.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    MessageSender.sendError(player, "This is not a valid tick value.");
                    return true;
                }

                if (creator.isStageAlreadyDefined(ticks)) {
                    creator.getOrCreateStage(ticks);
                    MessageSender.sendMessage(player, "Selected stage §3" + ticks + "§r.");
                    creator.getCurrentStage().sendStageInfo(player);
                    creator.sendStateMessage(player);
                    return true;
                }

                creator.getOrCreateStage(ticks);
                MessageSender.sendMessage(player, "Stage §3" + ticks + "§2 created.");
                creator.sendStateMessage(player);
                return true;
            }

            if ("addRocket".equalsIgnoreCase(cmd)) {
                if (creator.getCurrentRocketState() != null) {
                    MessageSender.sendError(player, "You are currently defining a rocket.");
                    creator.sendStateMessage(player);
                    return true;
                }

                if (values.length != 2) {
                    MessageSender.sendError(player, "Invalid values.");
                    return true;
                }

                int height;
                try {
                    height = Integer.parseInt(values[0]);
                    if (height < 0 || height > 255) {
                        MessageSender.sendError(player, "Invalid height value.");
                        return true;
                    }

                } catch (NumberFormatException e) {
                    MessageSender.sendError(player, "Invalid height value.");
                    return true;
                }

                RocketType type = RocketType.parse(values[1]);

                if (type == null) {
                    MessageSender.sendError(player, "Invalid rocket type");
                    return true;
                }
                MessageSender.sendMessage(player, "Creating a new " + type.toString().toLowerCase() + " rocket.");
                creator.newRocket(height, type).sendStateMessage(player);
                return true;
            }

            if ("removeStage".equalsIgnoreCase(cmd)) {
                if (values.length != 1) {
                    MessageSender.sendError(player, "Invalid input.");
                    return true;
                }

                int ticks;
                try {
                    ticks = Integer.parseInt(values[0]);
                    if (ticks < 0) {
                        MessageSender.sendError(player, "Invalid tick value.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    MessageSender.sendError(player, "Invalid tick value.");
                    return true;
                }
                if (creator.hasStage(ticks)) {
                    creator.deleteStage(ticks);
                } else {
                    MessageSender.sendMessage(player, "This stage is not defined.");
                }
                return true;
            }

            if ("removeRocket".equalsIgnoreCase(cmd)) {
                if (values.length != 1) {
                    MessageSender.sendError(player, "Invalid input.");
                    return true;
                }

                int id;
                try {
                    id = Integer.parseInt(values[0]);
                    if (id < 0 || id >= creator.getCurrentStage().getRockets().size()) {
                        MessageSender.sendError(player, "Invalid ID.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    MessageSender.sendError(player, "Invalid ID.");
                    return true;
                }

                creator.getCurrentStage().removeRocket(id);
                MessageSender.sendMessage(player, "Rocket " + id + " removed.");
                return true;
            }

            if ("save".equals(cmd)) {
                storyboardLib.addStoryboard(creatorMap.remove(player.getUniqueId()).build());
                storyboardLib.save();
                MessageSender.sendMessage(player, "Storyboard was saved.");
                return true;
            }

            if ("cancel".equalsIgnoreCase(cmd)) {
                StoryboardCreator remove = creatorMap.remove(player.getUniqueId());
                MessageSender.sendMessage(player, "Creation of storyboard aborted.");
                return true;
            }

            if ("cancelRocket".equalsIgnoreCase(cmd)) {
                if (creator.getCurrentRocketState() == null) {
                    MessageSender.sendMessage(player, "You are currently not creating a rocket.");
                    return true;
                }
                MessageSender.sendMessage(player, "Rocket creation canceled.");
                creator.cancelRocket();
                creator.sendStateMessage(player);
                return true;
            }

            if ("cancelStage".equalsIgnoreCase(cmd)) {
                if (creator.getCurrentStage().getRockets().isEmpty()) {
                    creator.cancelStage();
                } else {
                    MessageSender.sendError(player, "This stage is already created. Remove the stage if you want.");
                }
                return true;
            }

            if ("storyboardInfo".equalsIgnoreCase(cmd)) {
                creator.sendStoryboardInfo(player);
                return true;
            }

            if ("stageInfo".equalsIgnoreCase(cmd)) {
                RocketStage currentStage = creator.getCurrentStage();
                if (currentStage == null) {
                    MessageSender.sendError(player, "No current stage selected.");
                    return true;
                }
                currentStage.sendStageInfo(player);
                return true;
            }

            if ("changeStageTick".equalsIgnoreCase(cmd)) {
                if (values.length != 1) {
                    MessageSender.sendError(player, "Invalid input.");
                    return true;
                }
                int ticks;
                try {
                    ticks = Integer.parseInt(values[0]);
                    if (ticks < 0) {
                        MessageSender.sendError(player, "Invalid tick value.");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    MessageSender.sendError(player, "Invalid tick value.");
                    return true;
                }
                creator.getCurrentStage().setTicks(ticks);
                return true;
            }
        }

        String name = args[0];

        if (creatorMap.containsKey(player.getUniqueId())) {
            MessageSender.sendError(player, "You are already creating a rocket.");
            return true;
        }
        if (storyboardLib.exists(name)) {
            MessageSender.sendMessage(player, "Storyboard loaded.");
            StoryboardCreator creator = StoryboardCreator.newCreatorFromStoryboard(storyboardLib.getStoryboard(name));
            creatorMap.put(player.getUniqueId(), creator);
            creator.sendStoryboardInfo(player);
            creator.sendStateMessage(player);
            return true;
        }
        if (args.length != 2) {
            MessageSender.sendError(player, "Invalid input");
            return true;
        }

        double cooldown;
        try {
            cooldown = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid cooldown value");
            return true;
        }

        StoryboardCreator creator = StoryboardCreator.newCreator(name, cooldown);
        creatorMap.put(player.getUniqueId(), creator);
        MessageSender.sendMessage(player, "Storyboard creation started.");
        creator.sendStateMessage(player);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        List<String> suggestions = new ArrayList<>();
        if (creatorMap.containsKey(player.getUniqueId())) {
            StoryboardCreator creator = creatorMap.get(player.getUniqueId());
            String subcommand = args[0];
            if ("setValue".equalsIgnoreCase(subcommand)) {
                if (creator.getCurrentRocketState() == null) {
                    return Collections.emptyList();
                }

                switch (creator.getCurrentRocketState()) {
                    case IMAGE:
                        if (args.length == 2) {
                            return imageLib.getMatchingImages(args[1]);
                        }
                        break;
                    case COLOR:
                    case FADE_COLOR:
                        if (args.length > 1) {
                            return ArrayUtil.startingWithInArray(args[args.length - 1], COLORS).collect(Collectors.toList());
                        }
                        return Collections.emptyList();
                    case FLICKER:
                        if (args.length == 2) {
                            return ArrayUtil.startingWithInArray(args[1], new String[] {"true", "false"}).collect(Collectors.toList());
                        }
                        break;
                    case SPREAD:
                        if (args.length == 2) {
                            suggestions.add("<number>");
                        }
                        break;
                    case BURST_DIRECTION:
                        if (args.length == 2) {
                            suggestions.addAll(ArrayUtil.startingWithInArray(args[1], BurstDirection.asStringArray())
                                    .collect(Collectors.toList()));
                            return suggestions;
                        }
                        break;
                    case SPAWN:
                        if (args.length == 1) return Collections.emptyList();
                        if ("center".equalsIgnoreCase(args[1])) {
                            suggestions.add("offset as <x,y,z>");
                            return suggestions;
                        }
                        if (ArrayUtil.arrayContains(args[1], SpawnForm.asStringArray())) {
                            if (args.length == 3) {
                                return Collections.singletonList("<radius>");
                            }
                            if (args.length == 4) {
                                return Collections.singletonList("<count>");
                            }
                            return Collections.emptyList();
                        }
                        String[] centers = (String[]) ArrayUtils.add(SpawnForm.asStringArray(), "CENTER");
                        return ArrayUtil.startingWithInArray(args[1], centers).collect(Collectors.toList());
                    case DONE:
                        break;
                }
                return suggestions;
            }

            if ("addStage".equalsIgnoreCase(subcommand)) {
                if (args.length == 2) {
                    suggestions.add("<ticks>");
                }
                return suggestions;
            }

            if ("addRocket".equalsIgnoreCase(subcommand)) {
                if (args.length == 2) {
                    return Collections.singletonList("<height>");
                } else if (args.length == 3) {
                    return ArrayUtil.startingWithInArray(args[2], RocketType.asStringArray())
                            .collect(Collectors.toList());
                }
                return Collections.emptyList();
            }

            if ("removeRocket".equalsIgnoreCase(subcommand)) {
                int size = creator.getCurrentStage().getRockets().size();
                for (int i = 0; i < size; i++) {
                    suggestions.add(Integer.toString(i));
                }
                return suggestions;
            }

            if ("removeStage".equalsIgnoreCase(subcommand)) {
                for (RocketStage stage : creator.getStages()) {
                    suggestions.add(Integer.toString(stage.getTicks()));
                }
                return suggestions;
            }

            if ("save".equalsIgnoreCase(subcommand)) {
                return Collections.emptyList();
            }

            if ("cancel".equalsIgnoreCase(subcommand)) {
                return Collections.emptyList();
            }

            if ("cancelRocket".equalsIgnoreCase(subcommand)) {
                return Collections.emptyList();
            }

            suggestions.add("storyboardInfo");
            suggestions.add("stageInfo");

            if (creator.getCurrentRocketState() != null) {
                if (args.length == 1) {
                    return ArrayUtil.startingWithInArray(subcommand, new String[] {"setValue", "cancelRocket", "cancel"}).collect(Collectors.toList());
                }
                return Collections.emptyList();
            }

            if (creator.isStageDefined()) {
                if (args.length == 1) {
                    return ArrayUtil.startingWithInArray(subcommand, new String[] {"changeStageTick", "addStage", "addRocket", "removeRocket", "removeStage", "save", "cancel"}).collect(Collectors.toList());
                }
                return Collections.emptyList();
            }

            if (creator.getStages().isEmpty()) {
                if (args.length == 1) {
                    return ArrayUtil.startingWithInArray(subcommand, new String[] {"addStage", "addRocket", "cancel"}).collect(Collectors.toList());
                }
                return Collections.emptyList();
            }

        } else {
            if (args.length >= 1) {
                if (storyboardLib.exists(args[0])) {
                    return Collections.singletonList("This name is already in use. Press enter to edit.");
                }
            }
            if (args.length == 1) {
                List<String> matchingStoryboard = storyboardLib.getMatchingStoryboard(args[0]);
                matchingStoryboard.add("<rocketname>");
                return matchingStoryboard;
            }
            if (args.length == 2) {
                return Collections.singletonList("<cooldown>");
            }
        }
        return Collections.emptyList();
    }
}
