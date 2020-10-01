package de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder;

import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rocketspawns.RadiusSpawn;
import de.eldoria.fireworkparade.rocket.rocketspawns.RocketSpawn;
import de.eldoria.fireworkparade.rocket.rocketspawns.SingleSpawn;
import de.eldoria.fireworkparade.rocket.rocketspawns.SpawnForm;
import de.eldoria.fireworkparade.rocket.rockettypes.ColoredRocket;
import de.eldoria.fireworkparade.rocket.rockettypes.Rocket;
import de.eldoria.fireworkparade.util.ColorUtil;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ColoredRocketBuilder extends RocketBuilder {

    protected Color[] colors;
    protected Color[] fadeColors;
    protected Boolean flicker;
    protected RocketSpawn spawn;

    protected ColoredRocketBuilder(int height, RocketType type) {
        super(height, type, RocketValue.COLOR);
    }

    public static ColoredRocketBuilder newRocketBuilder(int height, RocketType type) {
        return new ColoredRocketBuilder(height, type);
    }

    @Override
    public boolean setValue(String[] value) {
        boolean success;
        switch (currentValue) {
            case COLOR:
                success = setColors(value);
                if (success) currentValue = RocketValue.FADE_COLOR;
                return success;
            case FADE_COLOR:
                success = setFadeColors(value);
                if (success) currentValue = RocketValue.FLICKER;
                return success;
            case FLICKER:
                success = setFlicker(value);
                if (success) currentValue = RocketValue.SPAWN;
                return success;
            case SPAWN:
                success = setSpawn(value);
                if (success) currentValue = RocketValue.DONE;
                return success;
            case DONE:
                return true;
        }
        return false;
    }

    @Override
    public Rocket build() {
        switch (type) {
            case BALL:
                return ColoredRocket.newBallRocket(height, colors, fadeColors, flicker, spawn);
            case BALL_LARGE:
                return ColoredRocket.newLargeBallRocket(height, colors, fadeColors, flicker, spawn);
            case CREEPER:
                return ColoredRocket.newCreeperRocket(height, colors, fadeColors, flicker, spawn);
            case STAR:
                return ColoredRocket.newStarRocket(height, colors, fadeColors, flicker, spawn);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }


    protected boolean setColors(String[] args) {
        if (args.length == 0) return false;
        List<Color> colors = new ArrayList<>();
        for (String arg : args) {
            Color color;
            if (arg.length() == 1) {
                color = ColorUtil.parseColor(arg.charAt(0));
            } else {
                color = ColorUtil.parseColor(arg);
            }
            if (color == null) return false;
            colors.add(color);
        }
        this.colors = colors.toArray(new Color[0]);
        return true;
    }

    protected boolean setFadeColors(String[] args) {
        List<Color> fadeColors = new ArrayList<>();
        for (String arg : args) {
            Color color;
            if (arg.length() == 1) {
                color = ColorUtil.parseColor(arg.charAt(0));
            } else {
                color = ColorUtil.parseColor(arg);
            }
            if (color == null) return false;
            fadeColors.add(color);
        }
        this.fadeColors = fadeColors.toArray(new Color[0]);
        return true;
    }

    protected boolean setFlicker(String[] args) {
        if (args.length != 1) return false;
        if ("true".equalsIgnoreCase(args[0])) {
            this.flicker = true;
            return true;
        }
        if ("false".equalsIgnoreCase(args[0])) {
            this.flicker = false;
            return true;
        }
        return false;
    }

    protected boolean setSpawn(String[] args) {
        if (args.length == 0) return false;
        String spawnType = args[0];
        if ("center".equalsIgnoreCase(spawnType)) {
            String[] split = args[1].split(",");
            if (split.length != 3) return false;

            int[] vector = new int[3];

            for (int i = 0; i < split.length; i++) {
                try {
                    vector[i] = Integer.parseInt(split[i]);
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            this.spawn = new SingleSpawn(new Vector(vector[0], vector[1], vector[2]));
            return true;
        }

        SpawnForm spawnForm = SpawnForm.parse(spawnType);
        if (spawnForm == null) return false;

        if (args.length != 3) return false;

        int radius;
        try {
            radius = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        int count;
        try {
            count = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        this.spawn = RadiusSpawn.newSpawn(spawnForm, count, radius);
        return true;
    }
}
