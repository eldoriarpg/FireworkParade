package de.eldoria.fireworkfun.rocket.storyboard;

import de.eldoria.fireworkfun.FireworkFun;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class ExecutedRocketStoryboard {
    private Firework rocket;
    private final Location location;
    private final RocketStoryboard storyboard;

    private ExecutedRocketStoryboard(Location location, RocketStoryboard storyboard) {
        this.location = location.clone();
        this.storyboard = storyboard;
    }

    private void executeStoryboard() {
        // raise the spawn a bit
        Location spawn = location.clone().add(0, 1.5, 0);

        // Schedule the rockets. Use spawn clone to avoid modification.
        for (RocketStage rocket : storyboard.getStages()) {
            rocket.schedule(spawn.clone());
        }
    }

    public static ExecutedRocketStoryboard createAndExecute(Location location, RocketStoryboard storyboard) {
        ExecutedRocketStoryboard executedStoryboard = new ExecutedRocketStoryboard(location, storyboard);
        executedStoryboard.executeStoryboard();
        return executedStoryboard;
    }
}
