package de.niclasl.multiPlugin.teleport.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TeleportManager {

    private final File teleportFolder;
    private final Map<String, File> files = new HashMap<>();

    public TeleportManager(File dataFolder) {
        this.teleportFolder = new File(dataFolder, "teleports");
        if (!teleportFolder.exists()) teleportFolder.mkdirs();

        createFile("overworld.yml", 0.5, 64);
        createFile("nether.yml", 0.5, 50);
        createFile("end.yml", 100.5, 50.5);
    }

    private void createFile(String fileName, double x, double y) {
        File file = new File(teleportFolder, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("x", x);
                config.set("y", y);
                config.set("z", 0.5);
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        files.put(fileName, file);
    }

    public File getLocationFile(String fileName) {
        return files.get(fileName);
    }

    /**
     * Lädt eine Location aus der angegebenen Dimension.
     * Fügt automatisch die korrekte Welt hinzu.
     * Liefert Default-Location, falls noch keine gesetzt.
     */
    public Location getLocation(String dimension) {
        String fileName = getFileName(dimension);
        if (fileName == null) return null;

        File file = getLocationFile(fileName);
        World world = getWorld(dimension);
        if (world == null) return null;

        if (file == null || !file.exists()) {
            // Gib Default zurück, falls Datei fehlt
            return getDefaultLocation(dimension, world);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // Falls Koordinaten nicht gesetzt, Default zurückgeben
        if (!config.contains("x") || !config.contains("y") || !config.contains("z")) {
            return getDefaultLocation(dimension, world);
        }

        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");

        return new Location(world, x, y, z);
    }

    /**
     * Speichert eine Location in der angegebenen Dimension.
     */
    public void setLocation(String dimension, Location location) {
        String fileName = getFileName(dimension);
        if (fileName == null) return;

        File file = getLocationFile(fileName);
        if (file == null) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("x", location.getX());
        config.set("y", location.getY());
        config.set("z", location.getZ());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(String dimension) {
        return switch (dimension.toLowerCase()) {
            case "overworld" -> "overworld.yml";
            case "nether" -> "nether.yml";
            case "end" -> "end.yml";
            default -> null;
        };
    }

    private World getWorld(String dimension) {
        return switch (dimension.toLowerCase()) {
            case "overworld" -> Bukkit.getWorld("world");
            case "nether" -> Bukkit.getWorld("world_nether");
            case "end" -> Bukkit.getWorld("world_the_end");
            default -> null;
        };
    }

    private Location getDefaultLocation(String dimension, World world) {
        return switch (dimension.toLowerCase()) {
            case "overworld" -> world.getSpawnLocation();
            case "nether" -> new Location(world, 0.5, 50, 0.5);
            case "end" -> new Location(world, 100.5, 50.5,0.5);
            default -> null;
        };
    }
}