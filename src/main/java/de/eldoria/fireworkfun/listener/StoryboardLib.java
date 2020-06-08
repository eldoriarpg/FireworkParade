package de.eldoria.fireworkfun.listener;

import de.eldoria.fireworkfun.rocket.storyboard.RocketStoryboard;
import org.bukkit.Color;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class StoryboardLib {

    private final Map<String, RocketStoryboard> storyboards;
    private final Logger logger;
    private final Plugin plugin;
    private final ImageLib imageLib;

    public StoryboardLib(Plugin plugin, ImageLib imageLib) {
        logger = plugin.getLogger();
        this.plugin = plugin;
        storyboards = new HashMap<>();
        this.imageLib = imageLib;
        load();
    }

    public void addStoryboard(RocketStoryboard storyboard) {
        storyboards.put(storyboard.getName(), storyboard);
    }

    public void load() {
        storyboards.clear();
        @SuppressWarnings("unchecked")
        List<RocketStoryboard> storyboards = (List<RocketStoryboard>) plugin.getConfig().getList("rockets");
        if (storyboards == null) {
            logger.warning("You are dumb! But i still love you! Maybe you messed up your config a bit.");
            return;
        }
        for (RocketStoryboard storyboard : storyboards) {
            this.storyboards.put(storyboard.getName(), storyboard);
        }
    }

    public void save() {
        plugin.getConfig().set("rockets", new ArrayList<>(storyboards.values()));
        plugin.saveConfig();
    }

    public RocketStoryboard getStoryboard(String name) {
        return storyboards.get(name);
    }

    private Color getColor(String value) {
        String color = value.toUpperCase();
        try {
            return (Color) Color.class.getField(color).get(Color.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public boolean exists(String name) {
        return storyboards.containsKey(name);
    }
}
