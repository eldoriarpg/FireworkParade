package de.eldoria.fireworkparade.listener;

import de.eldoria.fireworkparade.rocket.rockettypes.Detonable;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class FireworkScheduler implements Runnable {

    private int bukkitTaskId = Integer.MIN_VALUE;
    private final Plugin plugin;

    private int currentTick;
    private final Queue<ScheduledFireworkAction> fireworkQueue =
            new PriorityQueue<>(Comparator.comparingInt(ScheduledFireworkAction::getExplosionTick));


    public FireworkScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    void start() {
        if (bukkitTaskId == Integer.MIN_VALUE) {
            bukkitTaskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 1, 1).getTaskId();
            plugin.getLogger().info("Scheduler started");
        }
    }

    void cancel() {
        if (bukkitTaskId != Integer.MIN_VALUE) {
            Bukkit.getScheduler().cancelTask(bukkitTaskId);
            bukkitTaskId = Integer.MIN_VALUE;
            plugin.getLogger().info("Scheduler stopped.");
        }
    }

    @Override
    public void run() {
        if (fireworkQueue.isEmpty()) {
            cancel();
            return;
        }

        while (!fireworkQueue.isEmpty() && fireworkQueue.peek().getExplosionTick() == currentTick) {
            fireworkQueue.poll().detonate();
        }
        currentTick++;
    }

    public void scheduleRocket(int explosionTick, Location location, Vector launchVelocity, FireworkEffect effect) {
        schedule(new ScheduledFirework(explosionTick + currentTick, location, launchVelocity, effect));
    }

    public void scheduleRocket(int explosionTick, Location location, FireworkEffect effect) {
        schedule(new ScheduledFirework(explosionTick + currentTick, location, effect));

    }

    public void scheduleImage(int tickDelay, Location location, ParticleMap map) {
        schedule(new ScheduledImage(tickDelay + currentTick, location, map));
    }

    private void schedule(ScheduledFireworkAction action) {
        fireworkQueue.add(action);
        start();
    }

    @Getter
    private static class ScheduledFirework extends ScheduledFireworkAction {
        private final Vector launchVelocity;
        private final FireworkEffect effect;

        public ScheduledFirework(int explosionTick, Location location, Vector launchVelocity, FireworkEffect effect) {
            super(explosionTick, location);
            this.launchVelocity = launchVelocity;
            this.effect = effect;
        }

        public ScheduledFirework(int explosionTick, Location location, FireworkEffect effect) {
            super(explosionTick, location);
            this.launchVelocity = new Vector();
            this.effect = effect;
        }

        @Override
        public void detonate() {
            Firework firework = spawnFirework(getLocation());
            firework.setVelocity(launchVelocity);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.addEffect(effect);
            firework.setFireworkMeta(meta);
            firework.detonate();
        }
    }

    private static class ScheduledImage extends ScheduledFireworkAction {
        private final ParticleMap map;

        public ScheduledImage(int explosionTick, Location location, ParticleMap map) {
            super(explosionTick, location);
            this.map = map;
        }

        @Override
        public void detonate() {
            map.spawnMap(getLocation());
        }
    }

    private abstract static class ScheduledFireworkAction implements Detonable {
        @Getter
        private final int explosionTick;
        @Getter
        private final Location location;

        public ScheduledFireworkAction(int explosionTick, Location location) {
            this.explosionTick = explosionTick;
            this.location = location;
        }
    }
}
