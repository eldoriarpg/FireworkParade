package de.eldoria.fireworkparade.commands;

import de.eldoria.fireworkparade.listener.StoryboardLib;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStoryboard;
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
import java.util.List;

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

        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);

        FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
        fireworkMeta.setPower(6);
        fireworkMeta.addEffect(FireworkEffect.builder().trail(true).withColor(Color.BLACK).build());

        String name = String.join(" ", args);

        RocketStoryboard storyboard = storyboardLib.getStoryboard(name);

        fireworkMeta.setDisplayName(storyboard.getName());
        fireworkMeta.setLore(Arrays.asList("", "Cooldown " + storyboard.getCooldown()));
        item.setItemMeta(fireworkMeta);
        player.getInventory().addItem(new ItemStack(item));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
