package de.eldoria.fireworkparade.commands.storyboardbuilder.rocketbuilder;

import de.eldoria.fireworkparade.FireworkParade;
import de.eldoria.fireworkparade.listener.ParticleMap;
import de.eldoria.fireworkparade.rocket.RocketType;
import de.eldoria.fireworkparade.rocket.rockettypes.ImageRocket;
import de.eldoria.fireworkparade.rocket.rockettypes.Rocket;

public class ImageRocketBuilder extends RocketBuilder {
    private ParticleMap particleMap;

    public ImageRocketBuilder(int height) {
        super(height, RocketType.IMAGE, RocketValue.IMAGE);
    }

    @Override
    public boolean setValue(String[] value) {
        switch (currentValue) {
            case IMAGE:
                ParticleMap particleMap = FireworkParade.getImageLib().getParticleMap(String.join(" ", value));
                if (particleMap == null) return false;
                this.particleMap = particleMap;
                currentValue = RocketValue.DONE;
                return true;
            case DONE:
                return true;
            default:
                throw new IllegalStateException("Unexpected value: " + currentValue);
        }
    }

    @Override
    public Rocket build() {
        return new ImageRocket(height, particleMap);
    }
}
