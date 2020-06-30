package de.eldoria.fireworkparade.commands;

import de.eldoria.fireworkparade.MessageSender;
import de.eldoria.fireworkparade.listener.StoryboardLib;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStoryboard;
import de.eldoria.fireworkparade.util.ArrayUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FireworkCommand implements TabExecutor {
    private final StoryboardLib storyboardLib;

    public FireworkCommand(StoryboardLib storyboardLib) {
        this.storyboardLib = storyboardLib;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if ("give".equalsIgnoreCase(args[0])) {
            int amount = 1;
            if (args.length > 2) {
                try {
                    amount = Integer.parseInt(args[2]);
                    if (amount > 64 || amount < 1) {
                        MessageSender.sendError(player, "Invalid amount");
                        return true;
                    }
                } catch (NumberFormatException e) {
                    MessageSender.sendError(player, "Invalid amount");
                    return true;
                }
            }


            ItemStack item = new ItemStack(Material.FIREWORK_ROCKET, amount);

            FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
            fireworkMeta.setPower(6);
            fireworkMeta.addEffect(FireworkEffect.builder().trail(true).withColor(Color.BLACK).build());

            String name = args[1];

            RocketStoryboard storyboard = storyboardLib.getStoryboard(name);
            Player target;
            if (args.length == 4) {
                target = Bukkit.getPlayer(args[3]);
                if (target == null) {
                    target = player;
                }
            } else {
                target = player;
            }

            if (storyboard == null) {
                MessageSender.sendError(player, "Invalid storyboard name");
                return true;
            }

            fireworkMeta.setDisplayName(storyboard.getName().replace("_", " "));
            fireworkMeta.setLore(Arrays.asList("", "Cooldown " + storyboard.getCooldown()));
            item.setItemMeta(fireworkMeta);
            target.getInventory().addItem(new ItemStack(item));
            if (target == player) {
                MessageSender.sendMessage(player, "Received " + amount + " " + storyboard.getName() + "§r.");
            } else {
                MessageSender.sendMessage(target, "Received " + amount + " " + storyboard.getName() + "§r.");
                MessageSender.sendMessage(player, "Gave " + amount + " " + storyboard.getName() + "§r to " + target.getName() + ".");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ("give".equalsIgnoreCase(args[0])) {
            if (args.length == 2) {
                return storyboardLib.getMatchingStoryboard(args[1]);
            }

            if (args.length == 3) {
                return Collections.singletonList("<amount>");
            }

            if (args.length == 4) {
                if (args[3].isEmpty()) {
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                }
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        return ArrayUtil.startingWithInArray(args[0], new String[] {"give"}).collect(Collectors.toList());
    }
}
