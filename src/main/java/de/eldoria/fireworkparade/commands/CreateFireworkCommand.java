package de.eldoria.fireworkparade.commands;

import de.eldoria.fireworkparade.FireworkParade;
import de.eldoria.fireworkparade.MessageSender;
import de.eldoria.fireworkparade.commands.storyboardbuilder.StoryboardCreator;
import de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder.RocketValue;
import de.eldoria.fireworkparade.listener.StoryboardLib;
import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rocketspawns.SpawnForm;
import de.eldoria.fireworkparade.rocket.rockettypes.BurstDirection;
import de.eldoria.fireworkparade.rocket.rockettypes.Rocket;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStage;
import de.eldoria.fireworkparade.util.ArrayUtil;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

    public CreateFireworkCommand(StoryboardLib storyboardLib) {
        this.storyboardLib = storyboardLib;
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

                boolean success = creator.setRocketValue(values);

                if (!success) {
                    MessageSender.sendError(player, "Invalid input.");
                    return true;
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
                    creator.newStage(ticks);
                    MessageSender.sendMessage(player, "This stage is already defined. You can now add more rockets.");
                }

                creator.newStage(ticks);
                MessageSender.sendMessage(player, "Stage " + ticks + " created.");
                creator.sendStateMessage(player);
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
                storyboardLib.addStoryboard(creator.build());
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
                List<RocketStage> stages = new ArrayList<>(creator.getStages());
                stages.sort(Comparator.comparingInt(RocketStage::getTicks));
                List<TextComponent> textComponents = new ArrayList<>();
                for (RocketStage stage : stages) {
                    TextComponent stageTicks = TextComponent
                            .builder(stage.getTicks() + " Ticks", TextColor.DARK_GREEN)
                            .hoverEvent(HoverEvent.
                                    showText(TextComponent
                                            .builder(stage.getRockets().size() + " rockets", TextColor.GREEN)
                                            .build()))
                            .build();
                    TextComponent select = TextComponent
                            .builder(" [select]", TextColor.AQUA)
                            .clickEvent(ClickEvent.runCommand("/fpc addStage " + stage.getTicks()))
                            .build();
                    TextComponent remove = TextComponent
                            .builder(" [remove]", TextColor.RED)
                            .clickEvent(ClickEvent.runCommand("/fpc removeStage " + stage.getTicks()))
                            .build();
                    MessageSender.sendTextComponents(player, stageTicks, select, remove);
                }
            }

            if ("stageInfo".equalsIgnoreCase(cmd)) {
                RocketStage currentStage = creator.getCurrentStage();
                if (currentStage == null) {
                    MessageSender.sendError(player, "No current stage selected.");
                    return true;
                }
                int id = 0;
                for (Rocket rocket : currentStage.getRockets()) {
                    TextComponent rocketType = TextComponent
                            .builder(rocket.getRocketType() + " rocket at height: " + rocket.getHeight(), TextColor.DARK_GREEN)
                            .hoverEvent(HoverEvent.
                                    showText(TextComponent
                                            .builder(rocket.getDescription(), TextColor.GREEN)
                                            .build()))
                            .build();
                    TextComponent remove = TextComponent
                            .builder(" [remove]", TextColor.RED)
                            .clickEvent(ClickEvent.runCommand("/fpc removeRocket " + id))
                            .build();
                    MessageSender.sendTextComponents(player, rocketType, remove);
                    id++;
                }
            }
        }

        if (values.length != 2) {
            return true;
        }

        String name = values[0];
        double cooldown;
        try {
            cooldown = Double.parseDouble(values[1]);
        } catch (NumberFormatException e) {
            MessageSender.sendError(player, "Invalid cooldown value");
            return true;
        }

        if (creatorMap.containsKey(player.getUniqueId())) {
            MessageSender.sendError(player, "You are already creating a rocket.");
            return true;
        }
        if (storyboardLib.exists(name)) {
            MessageSender.sendMessage(player, "This rocket name is already in use. Storyboard loaded.");
            StoryboardCreator creator = StoryboardCreator.newCreatorFromStoryboard(storyboardLib.getStoryboard(name));
            creatorMap.put(player.getUniqueId(), creator);
            creator.sendStateMessage(player);
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
                    return suggestions;
                }

                switch (creator.getCurrentRocketState()) {
                    case IMAGE:
                        if (args.length == 2) {
                            suggestions.add("<imageName>");
                        }
                        break;
                    case COLOR:
                    case FADE_COLOR:
                        return Arrays.asList("§00", "§11", "§22", "§33", "§44", "§55", "§66", "§77", "§88", "§99",
                                "§aa", "§bb", "§cc", "§dd", "§ee", "§ff");
                    case FLICKER:
                        if (args.length == 2) {
                            return Arrays.asList("true", "false");
                        }
                        break;
                    case SPREAD:
                        if (args.length == 2) {
                            suggestions.add("<number>");
                        }
                        break;
                    case BURST_DIRECTION:
                        if (args.length == 2) {
                            suggestions.addAll(ArrayUtil.startingWithInArray(args[0], BurstDirection.asStringArray())
                                    .collect(Collectors.toList()));
                            return suggestions;
                        }
                        break;
                    case SPAWN:
                        if ("center".equalsIgnoreCase(args[1])) {
                            suggestions.add("offset as <x,y,z>");
                            return suggestions;
                        }
                        if (ArrayUtil.arrayContains(SpawnForm.asStringArray())) {
                            suggestions.add("<radius> <count>");
                            return suggestions;
                        }
                        String[] centers = (String[]) ArrayUtils.add(SpawnForm.asStringArray(), "CENTER");
                        return ArrayUtil.startingWithInArray(args[1], centers).collect(Collectors.toList());
                    case DONE:
                        break;
                }
                return suggestions;
            }

            if ("addStage".equalsIgnoreCase(subcommand)) {
                suggestions.add("<ticks>");
                return suggestions;
            }

            if ("addRocket".equalsIgnoreCase(subcommand)) {
                if (args.length == 2) {
                    suggestions.add("<height>");
                } else if (args.length == 3) {
                    suggestions.addAll(ArrayUtil.startingWithInArray(args[2], RocketType.asStringArray())
                            .collect(Collectors.toList()));
                }
                return suggestions;
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
                return suggestions;
            }

            if ("cancel".equalsIgnoreCase(subcommand)) {
                return suggestions;
            }

            if ("cancelRocket".equalsIgnoreCase(subcommand)) {
                return suggestions;
            }

            suggestions.add("storyboardInfo");
            suggestions.add("stageInfo");

            if (creator.getCurrentRocketState() != null) {
                suggestions.add("setValue");
                suggestions.add("cancelRocket");
                suggestions.add("cancel");
                return suggestions;
            }

            if (creator.isStageDefined()) {
                suggestions.add("addStage");
                suggestions.add("addRocket");
                suggestions.add("removeRocket");
                suggestions.add("RemoveStage");
                suggestions.add("save");
                suggestions.add("cancel");
                return suggestions;
            }

            if (creator.getStages().isEmpty()) {
                suggestions.add("addStage");
                suggestions.add("addRocket");
                suggestions.add("cancel");
                return suggestions;
            }

        } else {
            if (args.length >= 1) {
                if (storyboardLib.exists(args[0])) {
                    suggestions.add("This name is already in use.");
                }
            }
            suggestions.add("<rocketname> <cooldown>");
        }
        return suggestions;
    }
}
