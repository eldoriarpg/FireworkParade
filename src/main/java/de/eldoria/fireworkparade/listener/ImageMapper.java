package de.eldoria.fireworkparade.listener;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Color;
import org.bukkit.plugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMapper {

    private final Plugin plugin;

    public ImageMapper(Plugin plugin) {
        this.plugin = plugin;
    }

    public ParticleMap getParticleMap(File file) {
        if (!file.exists()) {
            plugin.getLogger().warning("File " + file.getPath() + " does not exist!");
            return null;
        }

        BufferedImage read;

        try {
            read = ImageIO.read(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not load " + file.getPath() + "!");
            return null;
        }

        plugin.getLogger().info("Loading " + file.getName() + ".");

        if (read == null) {
            plugin.getLogger().warning("File " + file.getName() + " is not a image.");
            return null;
        }

        Color[][] colors = new Color[read.getWidth()][read.getHeight()];
        for (int width = 0; width < read.getWidth(); width++) {
            for (int height = 0; height < read.getHeight(); height++) {
                colors[width][height] = parseColor(read.getRGB(width, height));
            }
        }

        for (int i = 0; i < colors.length; i++) {
            ArrayUtils.reverse(colors[i]);
        }

        return new ParticleMap(file.getName().replace(" ", "_"), colors);
    }

    private Color parseColor(int colorInt) {
        java.awt.Color color = new java.awt.Color(colorInt);
        if (color.getAlpha() < 255) {
            return Color.BLACK;
        }
        if (color.getRed() + color.getBlue() + color.getGreen() == 0) {
            return Color.BLACK;
        }
        return Color.WHITE;
    }

}
