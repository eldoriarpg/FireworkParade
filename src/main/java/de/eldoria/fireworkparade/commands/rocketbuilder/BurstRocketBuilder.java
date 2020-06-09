package de.eldoria.fireworkparade.commands.rocketbuilder;

import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rockettypes.BurstDirection;
import de.eldoria.fireworkparade.rocket.rockettypes.BurstRocket;
import de.eldoria.fireworkparade.rocket.rockettypes.Rocket;

public class BurstRocketBuilder extends ColoredRocketBuilder {
    private double spread;
    private BurstDirection burstDirection;

    protected BurstRocketBuilder(int height, RocketType type) {
        super(height, type);
    }

    public static BurstRocketBuilder newRocketBuilder(int height) {
        return new BurstRocketBuilder(height, RocketType.BURST);
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
                if (success) currentValue = RocketValue.SPREAD;
                return success;
            case SPREAD:
                success = setSpread(value);
                if (success) currentValue = RocketValue.BURST_DIRECTION;
                return success;
            case BURST_DIRECTION:
                success = setBurstDirection(value);
                if (success) currentValue = RocketValue.SPAWN;
                return success;
            case SPAWN:
                success = setSpawn(value);
                if (success) currentValue = RocketValue.DONE;
                return success;
            case DONE:
                return true;
            default:
                throw new IllegalStateException("Unexpected value: " + currentValue);
        }
    }

    private boolean setBurstDirection(String[] value) {
        if (value.length != 1) return false;
        BurstDirection parse = BurstDirection.parse(value[0]);
        if (parse == null) return false;
        burstDirection = parse;
        return true;
    }

    private boolean setSpread(String[] value) {
        if (value.length != 1) return false;
        try {
            spread = Double.parseDouble(value[0]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public Rocket build() {
        return BurstRocket.newBurstRocket(height, spread, burstDirection, colors, fadeColors, flicker, spawn);
    }
}
