package de.eldoria.fireworkparade.listener;

import de.eldoria.fireworkparade.FireworkParade;
import de.eldoria.fireworkparade.rocket.storyboard.RocketStoryboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StartListener implements Listener {

    private final Plugin plugin;
    private ImageLib imageLib;
    private final StoryboardLib storyboardLib;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    private final Map<UUID, Instant> cooldown = new HashMap<>();

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
        if (itemMeta == null || !itemMeta.hasDisplayName() || !itemMeta.hasLore() || itemMeta.getLore().isEmpty()) {
            return;
        }

        String displayName = itemMeta.getDisplayName().replace(" ", "_");
        List<String> lore = itemMeta.getLore();

        RocketStoryboard storyboard = storyboardLib.getStoryboard(displayName);

        if (storyboard == null) return;
        Instant instant = cooldown.get(event.getPlayer().getUniqueId());
        if (instant != null) {
            if (!instant.isBefore(Instant.now())) {
                event.setCancelled(true);
                return;
            }
        }

        storyboard.fire(event.getClickedBlock().getLocation());
        int cooldown = (int) (storyboard.getCooldown() * 20);
        event.getPlayer().setCooldown(Material.FIREWORK_ROCKET, cooldown);

        scheduler.runTaskLater(
                FireworkParade.getInstance(),
                () -> event.getPlayer().setCooldown(Material.FIREWORK_ROCKET, 0),
                cooldown);

        this.cooldown.put(event.getPlayer().getUniqueId(),
                Instant.now().plus((long) (storyboard.getCooldown() * 1000), ChronoUnit.MILLIS));

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            //event.getItem().setAmount(event.getItem().getAmount() - 1);
        }
    }
}
