package de.eldoria.fireworkparade.listener;

import de.eldoria.fireworkparade.rocket.storyboard.RocketStoryboard;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class StartListener implements Listener {

    private final Plugin plugin;
    private ImageLib imageLib;
    private final StoryboardLib storyboardLib;

    public StartListener(StoryboardLib storyboardLib, Plugin plugin) {
        this.plugin = plugin;
        this.storyboardLib = storyboardLib;
    }

    @EventHandler
    public void onFireworkExplode(FireworkExplodeEvent event) {
        FireworkMeta fireworkMeta = event.getEntity().getFireworkMeta();
        if (fireworkMeta.getPower() != 6) return;
        if (fireworkMeta.hasDisplayName() && !storyboardLib.exists(fireworkMeta.getDisplayName())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        EquipmentSlot hand = event.getHand();
        if (hand == null) return;
        if (hand != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getItem() == null) return;
        ItemStack item = event.getItem();

        if (item.getItemMeta() == null) return;
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName() || !itemMeta.hasLore() || itemMeta.getLore().isEmpty()) return;

        String displayName = itemMeta.getDisplayName().replace(" ", "_");
        List<String> lore = itemMeta.getLore();

        RocketStoryboard storyboard = storyboardLib.getStoryboard(displayName);


        if (storyboard == null) return;

        storyboard.fire(event.getClickedBlock().getLocation());

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            //event.getItem().setAmount(event.getItem().getAmount() - 1);
        }
    }
}
