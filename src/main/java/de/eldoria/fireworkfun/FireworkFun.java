package de.eldoria.fireworkfun;

import de.eldoria.fireworkfun.commands.FireworkCommand;
import de.eldoria.fireworkfun.listener.ImageLib;
import de.eldoria.fireworkfun.listener.StartListener;
import de.eldoria.fireworkfun.listener.StoryboardLib;
import de.eldoria.fireworkfun.rocket.rocketspawns.RadiusSpawn;
import de.eldoria.fireworkfun.rocket.rocketspawns.SingleSpawn;
import de.eldoria.fireworkfun.rocket.rocketspawns.SpawnForm;
import de.eldoria.fireworkfun.rocket.rockettypes.BurstDirection;
import de.eldoria.fireworkfun.rocket.rockettypes.BurstRocket;
import de.eldoria.fireworkfun.rocket.rockettypes.ColoredRocket;
import de.eldoria.fireworkfun.rocket.rockettypes.ImageRocket;
import de.eldoria.fireworkfun.rocket.storyboard.RocketStage;
import de.eldoria.fireworkfun.rocket.storyboard.RocketStoryboard;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class FireworkFun extends JavaPlugin {
    private static FireworkFun instance;
    private StoryboardLib storyboardLib;
    private static ImageLib imageLib;

    public static ImageLib getImageLib() {
        return imageLib;
    }

    public static FireworkFun getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        imageLib = new ImageLib(this);

        ConfigurationSerialization.registerClass(RocketStoryboard.class, "storyboard");
        ConfigurationSerialization.registerClass(RocketStage.class, "stage");
        ConfigurationSerialization.registerClass(ImageRocket.class, "imageRocket");
        ConfigurationSerialization.registerClass(ColoredRocket.class, "coloredRocket");
        ConfigurationSerialization.registerClass(BurstRocket.class, "burstRocket");
        ConfigurationSerialization.registerClass(SingleSpawn.class, "singleSpawn");
        ConfigurationSerialization.registerClass(RadiusSpawn.class, "radiusSpawn");

        saveDefaultConfig();
        storyboardLib = new StoryboardLib(this, imageLib);
        getCommand("fireworkfun").setExecutor(new FireworkCommand(storyboardLib));
        Bukkit.getPluginManager().registerEvents(new StartListener(storyboardLib, this), this);

        //generateExampleRocket();
    }

    public void generateExampleRocket() {
        RocketStoryboard rocketStoryboard = new RocketStoryboard("testrocket", 3);
        RocketStage stage1 = new RocketStage(20);
        rocketStoryboard.addRocket(stage1);
        stage1.addRocket(new ImageRocket(60, imageLib.getParticleMap("lewd.png")));
        stage1.addRocket(BurstRocket.newBurstRocket(60, 4, BurstDirection.FUZZY_OUTSIDE,
                new Color[] {Color.RED, Color.BLUE}, new Color[] {Color.WHITE, Color.GREEN},
                true, RadiusSpawn.newSpawn(SpawnForm.CYCLE, 30, 20)));

        RocketStage stage2 = new RocketStage(60);
        rocketStoryboard.addRocket(stage2);
        stage2.addRocket(BurstRocket.newBallRocket(80,
                new Color[] {Color.RED, Color.BLUE}, new Color[] {Color.WHITE, Color.GREEN},
                true, RadiusSpawn.newSpawn(SpawnForm.RANDOM_CYCLE, 30, 20)));

        RocketStage stage3 = new RocketStage(80);
        rocketStoryboard.addRocket(stage3);
        stage3.addRocket(BurstRocket.newLargeBallRocket(80,
                new Color[] {Color.RED, Color.BLUE}, new Color[] {Color.WHITE, Color.GREEN},
                true, RadiusSpawn.newSpawn(SpawnForm.RANDOM_SPHERE, 30, 20)));

        storyboardLib.addStoryboard(rocketStoryboard);
        storyboardLib.save();
    }
}
