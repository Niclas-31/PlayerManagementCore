package de.niclasl.multiPlugin.spawn_protection.manager;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public record SpawnManager(MultiPlugin plugin) {

    private static final List<Location> netherSpawns = new ArrayList<>();
    private static final List<Location> overworldSpawns = new ArrayList<>();
    private static final List<Location> endSpawns = new ArrayList<>();

    public void loadAllSpawns() {
        netherSpawns.clear();
        overworldSpawns.clear();
        endSpawns.clear();

        File folder = new File(plugin.getDataFolder(), "teleports");
        if (!folder.exists()) {
            plugin.getLogger().warning("Teleports folder does not exist!");
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String dimensionType = config.getString("dimensionType", "overworld").toLowerCase();

            double x = config.getDouble("x");
            double y = config.getDouble("y");
            double z = config.getDouble("z");

            World.Environment env;

            switch (dimensionType) {
                case "nether" -> env = World.Environment.NETHER;
                case "end" -> env = World.Environment.THE_END;
                case "overworld", "normal" -> env = World.Environment.NORMAL;
                default -> {
                    plugin.getLogger().warning("Unknown dimensionType '" + dimensionType + "' in file " + file.getName());
                    continue;
                }
            }

            // Finde die passende Welt mit passendem Environment
            World world = Bukkit.getWorlds().stream()
                    .filter(w -> w.getEnvironment() == env)
                    .findFirst()
                    .orElse(null);

            if (world == null) {
                plugin.getLogger().warning("No world found for environment " + env + " in file: " + file.getName());
                continue;
            }

            Location loc = new Location(world, x, y, z);

            // Direkt in die passende Liste einfügen
            switch (env) {
                case NETHER -> netherSpawns.add(loc);
                case NORMAL -> overworldSpawns.add(loc);
                case THE_END -> endSpawns.add(loc);
            }
        }

        plugin.getLogger().info("Nether spawns loaded: " + netherSpawns.size());
        plugin.getLogger().info("Overworld spawns loaded: " + overworldSpawns.size());
        plugin.getLogger().info("End spawns loaded: " + endSpawns.size());
    }

    public static List<Location> getAllNetherSpawns() {
        return netherSpawns;
    }

    public static List<Location> getAllOverworldSpawns() {
        return overworldSpawns;
    }

    public static List<Location> getAllEndSpawns() {
        return endSpawns;
    }
}