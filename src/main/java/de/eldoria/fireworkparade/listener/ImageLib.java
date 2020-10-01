package de.eldoria.fireworkparade.listener;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageLib {
    private final Map<String, ParticleMap> particleMaps = new HashMap<>();
    private final ImageMapper imageMapper;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ImageLib(Plugin plugin) {
        imageMapper = new ImageMapper(plugin);
        Path imagesPath = Paths.get(plugin.getDataFolder().toString(), "images");
        File imageDir = imagesPath.toFile();

        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        try (Stream<Path> paths = Files.list(imagesPath)) {
            for (Path path : paths.collect(Collectors.toList())) {
                File file = path.toFile();
                if (!file.isFile()) {
                    continue;
                }
                String name = file.getName();
                ParticleMap particleMap = imageMapper.getParticleMap(file);
                if (particleMap == null) continue;
                particleMaps.put(name, particleMap);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not load file", e);
        }
    }

    public ParticleMap getParticleMap(String file) {
        return particleMaps.get(file);
    }

    public void spawnImage(Location location, String image) {
        if (!particleMaps.containsKey(image)) {
            return;
        }
        executorService.execute(() -> particleMaps.get(image).spawnMap(location));
    }

    public List<String> getMatchingImages(String string) {
        if (string.isEmpty()) return new ArrayList<>(particleMaps.keySet());
        return particleMaps.keySet().stream()
                .filter(s -> s.toLowerCase().startsWith(string.toLowerCase()))
                .collect(Collectors.toList());
    }
}
