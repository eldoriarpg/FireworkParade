package de.eldoria.fireworkparade.listener;

import de.eldoria.fireworkparade.rocket.storyboard.RocketStoryboard;
import org.bukkit.Color;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        storyboards.put(stripColorCodes(storyboard.getName()), storyboard);
    }

    public void removeStoryboard(RocketStoryboard storyboard) {
        storyboards.remove(stripColorCodes(storyboard.getName()));
        storyboards.remove(storyboard.getName());
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
            this.storyboards.put(stripColorCodes(storyboard.getName()), storyboard);
        }
    }

    public void save() {
        plugin.getConfig().set("rockets", new ArrayList<>(storyboards.values()));
        plugin.saveConfig();
    }

    public RocketStoryboard getStoryboard(String name) {
        return storyboards.getOrDefault(name, storyboards.get(stripColorCodes(name)));
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
        return getStoryboard(name) != null;
    }

    public List<String> getMatchingStoryboard(String value) {
        if (value.isEmpty()) {
            return storyboards.keySet().stream()
                    .map(this::stripColorCodes).collect(Collectors.toList());
        }
        return storyboards.keySet().stream()
                .filter(string -> string.toLowerCase().startsWith(value.toLowerCase())
                        || stripColorCodes(string).startsWith(value.toLowerCase()))
                .map(this::stripColorCodes).collect(Collectors.toList());
    }

    private String stripColorCodes(String s) {
        return s.replaceAll("[$§].", "");
    }
}
