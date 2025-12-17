package de.niclasl.multiPlugin.teleport.manager;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.spawn_protection.manager.SpawnManager;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeleportManager {

    private static File folder;
    private static MultiPlugin plugin;
    private final Map<UUID, BukkitTask> pendingTeleports = new HashMap<>();
    private static final Map<String, File> dimensionFiles = new HashMap<>();
    private static final Map<String, Set<UUID>> invitedPlayers = new HashMap<>();

    public TeleportManager(File dataFolder, MultiPlugin plugin) {
        TeleportManager.plugin = plugin;
        folder = new File(dataFolder, "teleports");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                dimensionFiles.put(file.getName(), file);
            }
        }

        loadAllDimensions();

        createDimensionIfNotExists("overworld", World.Environment.NORMAL, "overworld");
        createDimensionIfNotExists("nether", World.Environment.NETHER, "nether");
        createDimensionIfNotExists("end", World.Environment.THE_END, "end");
    }

    private void createDimensionIfNotExists(String dimension, World.Environment env, String dimensionType) {
        World world = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == env)
                .findFirst()
                .orElse(null);

        String worldName = (world != null) ? world.getName() : switch (env) {
            case NETHER -> "world_nether";
            case THE_END -> "world_the_end";
            default -> "world";
        };

        if (world == null) {
            new WorldCreator(worldName).environment(env).createWorld();
        }

        createDimensionFile(dimension, worldName, dimensionType);
    }

    private void createDimensionFile(String dimension, String worldName, String dimensionType) {
        File file = new File(folder, dimension + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                World world = Bukkit.getWorld(worldName);
                Location loc = (world != null)
                        ? world.getSpawnLocation()
                        : Bukkit.getWorlds().getFirst().getSpawnLocation();

                String type = (dimensionType != null) ? dimensionType.toLowerCase() : "overworld";

                if (world != null && (dimensionType == null || dimensionType.isBlank())) {
                    switch (world.getEnvironment()) {
                        case NETHER -> type = "nether";
                        case THE_END -> type = "end";
                        default -> type = "overworld";
                    }
                }

                if (type.equals("end")) {
                    loc = new Location(world, 100.5, 50, 0.5);
                }

                config.set("x", loc.getX());
                config.set("y", loc.getY());
                config.set("z", loc.getZ());
                config.set("world", worldName);
                config.set("dimensionType", type);
                config.set("private", false);

                config.save(file);
                dimensionFiles.put(dimension + ".yml", file);

                Bukkit.getLogger().info("[TeleportManager] Dimension file '" + dimension + ".yml' was created.");
            } catch (IOException e) {
                Bukkit.getLogger().severe("[TeleportManager] Error creating dimension file for '" + dimension + "'.");
                e.printStackTrace();
            }
        }
    }

    public static boolean dimensionExists(String dimension) {
        return dimensionFiles.containsKey(dimension + ".yml") || new File(folder, dimension + ".yml").exists();
    }

    private static World.Environment getEnvironmentFromType(String type) {
        return switch (type.toLowerCase()) {
            case "nether" -> World.Environment.NETHER;
            case "end" -> World.Environment.THE_END;
            default -> World.Environment.NORMAL;
        };
    }

    public static void createDimension(String dimension, String dimensionType, Player player) {
        if ((dimensionType.equalsIgnoreCase("nether") || dimensionType.equalsIgnoreCase("end"))
                && !player.hasPermission("multiplugin.teleport.dimension.setcoords")) {
            player.sendMessage(ChatColor.RED + "These dimension types are only allowed for admins.");
            return;
        }

        File file = new File(folder, dimension + ".yml");
        if (file.exists()) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                String storedType = config.getString("dimensionType");
                if (storedType == null || !storedType.equalsIgnoreCase(dimensionType)) {
                    config.set("dimensionType", dimensionType.toLowerCase());
                    config.save(file);
                }
                dimensionFiles.put(dimension + ".yml", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        WorldCreator worldCreator = new WorldCreator(dimension);
        worldCreator.environment(getEnvironmentFromType(dimensionType));
        World world = worldCreator.createWorld();
        assert world != null;
        Location spawnLoc = world.getSpawnLocation();

        try {
            file.createNewFile();
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("x", spawnLoc.getX());
            config.set("y", spawnLoc.getY());
            config.set("z", spawnLoc.getZ());
            config.set("world", dimension);
            config.set("dimensionType", dimensionType.toLowerCase());
            config.set("owner", player.getUniqueId().toString());
            config.set("private", false);
            config.save(file);

            dimensionFiles.put(dimension + ".yml", file);

            player.sendMessage(ChatColor.GREEN + "The dimension §6" + dimension + "§a with type §6" + dimensionType + "§a was created.");
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Error creating dimension.");
        }
    }

    public static boolean hasAccess(Player player, String dimension) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return false;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        boolean isPrivate = config.getBoolean("private", false);
        if (!isPrivate) return true;

        String owner = config.getString("owner");
        if (owner != null && owner.equals(player.getUniqueId().toString())) return true;

        if (isInvited(dimension, player.getUniqueId())) {
            return true;
        }

        return player.hasPermission("dimension.access." + dimension);
    }

    public static void setPrivate(Player player, String dimension, boolean makePrivate) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String owner = config.getString("owner");

        if (owner == null || !owner.equals(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "You are not the owner of this world.");
            return;
        }

        config.set("private", makePrivate);
        try {
            config.save(file);
            player.sendMessage(ChatColor.GREEN + "World '" + dimension + "' is now " + (makePrivate ? "§cprivat" : "§apublic") + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Location getLocation(String dimension) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String worldName = config.getString("world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            WorldCreator wc = new WorldCreator(worldName);
            String dimType = config.getString("dimensionType", "overworld");
            wc.environment(switch (dimType.toLowerCase()) {
                case "nether" -> World.Environment.NETHER;
                case "end" -> World.Environment.THE_END;
                default -> World.Environment.NORMAL;
            });
            world = wc.createWorld();
        }

        if (world == null) return null;

        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");

        return new Location(world, x, y, z);
    }

    public static void setLocation(String dimension, Location loc, String dimensionType) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set("x", loc.getX());
        config.set("y", loc.getY());
        config.set("z", loc.getZ());

        String worldName = getWorldNameForDimension(dimension);
        config.set("world", worldName);

        if (!config.contains("dimensionType")) {
            config.set("dimensionType", dimensionType.toLowerCase());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        switch (dimensionType.toLowerCase()) {
            case "nether" -> SpawnManager.getAllNetherSpawns().add(loc);
            case "overworld", "normal" -> SpawnManager.getAllOverworldSpawns().add(loc);
            case "end" -> SpawnManager.getAllEndSpawns().add(loc);
        }
    }

    private static String getWorldNameForDimension(String dimension) {
        return switch (dimension.toLowerCase()) {
            case "overworld", "world" -> "world";
            case "nether", "world_nether" -> "world_nether";
            case "end", "world_the_end" -> "world_the_end";
            default -> dimension;
        };
    }

    public static List<String> getAllDimensions() {
        Set<String> dimensions = new HashSet<>();
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                dimensions.add(file.getName().replace(".yml", ""));
            }
        }
        return new ArrayList<>(dimensions);
    }

    public static void loadAllDimensions() {
        if (!folder.exists()) return;
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File file : files) {
            dimensionFiles.put(file.getName(), file);
        }
    }

    public static boolean deleteDimension(String dimension) {
        boolean deletedSomething = false;

        File file = new File(folder, dimension + ".yml");
        if (file.exists()) {
            if (!file.delete()) return false;
            dimensionFiles.remove(dimension + ".yml");
            deletedSomething = true;
        }

        World world = Bukkit.getWorld(dimension);
        if (world != null) {
            if (!Bukkit.unloadWorld(world, false)) return false;
            deletedSomething = true;
        }

        File worldFolder = new File(Bukkit.getWorldContainer(), dimension);
        if (worldFolder.exists()) {
            deleteFolder(worldFolder);
            deletedSomething = true;
        }

        return deletedSomething;
    }

    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteFolder(f);
                else f.delete();
            }
        }
        folder.delete();
    }

    public static void setTeleportDelay(Player player, String dimension, int seconds) {
        File file = new File(folder, dimension + ".yml");
        if (!file.exists()) {
            player.sendMessage(ChatColor.RED + "World '" + dimension + "' does not exist.");
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        Set<String> defaultWorlds = Set.of("world", "world_nether", "world_the_end");
        if (defaultWorlds.contains(dimension.toLowerCase())) {
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "Only server owner can set delay for default worlds!");
                return;
            }
        } else {
            String ownerUUID = config.getString("owner");
            if (ownerUUID == null || !ownerUUID.equals(player.getUniqueId().toString())) {
                player.sendMessage(ChatColor.RED + "You are not the owner of world '" + dimension + "'.");
                return;
            }
        }

        config.set("delay", seconds);

        try {
            config.save(file);
            player.sendMessage(ChatColor.GREEN + "Teleport delay for world " + ChatColor.GOLD + dimension
                    + ChatColor.GREEN + " set to " + ChatColor.GOLD + seconds + " seconds" + ChatColor.GREEN + ".");
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while saving the delay.");
        }
    }

    public static int getTeleportDelay(String dimension) {
        File file = new File(folder, dimension + ".yml");
        if (!file.exists()) return 5;

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getInt("delay", 5);
    }

    public void teleportWithDelay(Player player, Location target, int delaySeconds, String dimension) {
        UUID uuid = player.getUniqueId();

        if (pendingTeleports.containsKey(uuid)) {
            player.sendMessage(ChatColor.RED + "You are already teleporting!");
            return;
        }

        Location startLoc = player.getLocation().clone();
        player.sendMessage(ChatColor.YELLOW + "Teleporting in " + delaySeconds + " seconds. Don't move!");

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int secondsLeft = delaySeconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTeleport(uuid);
                    return;
                }

                Location currentLoc = player.getLocation();
                if (currentLoc.distanceSquared(startLoc) > 0.1) {
                    player.sendMessage(ChatColor.RED + "Teleport cancelled because you moved.");
                    player.getWorld().spawnParticle(Particle.SMOKE, currentLoc, 20, 0.3, 0.3, 0.3, 0.01);
                    player.playSound(currentLoc, Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    cancelTeleport(uuid);
                    return;
                }

                player.getWorld().spawnParticle(Particle.PORTAL, currentLoc, 10, 0.3, 0.5, 0.3, 0.01);
                player.playSound(currentLoc, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.4f, 1.6f);

                if (--secondsLeft <= 0) {
                    player.getWorld().spawnParticle(Particle.END_ROD, currentLoc, 50, 0.5, 1.0, 0.5, 0.05);
                    player.playSound(currentLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    player.teleport(target);
                    player.getWorld().spawnParticle(Particle.END_ROD, target, 50, 0.5, 1.0, 0.5, 0.05);
                    player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    player.sendMessage(ChatColor.GREEN + "Teleported to §6'" + dimension + "'§a.");
                    cancelTeleport(uuid);
                } else {
                    player.sendRawMessage(ChatColor.YELLOW + "Teleporting in " + secondsLeft + "s...");
                }
            }
        }, 0L, 20L);

        pendingTeleports.put(uuid, task);
    }

    private void cancelTeleport(UUID uuid) {
        BukkitTask task = pendingTeleports.remove(uuid);
        if (task != null) task.cancel();
    }

    public void setOwner(String dimension, UUID ownerUUID) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("owner", ownerUUID.toString());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UUID getOwner(String dimension) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String ownerStr = config.getString("owner");
        if (ownerStr == null) return null;

        try {
            return UUID.fromString(ownerStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getDimensionType(String dimension) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return null;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.getString("dimensionType");
    }

    public static boolean isInvited(String dimension, UUID playerUUID) {
        Set<UUID> invited = invitedPlayers.get(dimension);
        return invited != null && invited.contains(playerUUID);
    }

    public static boolean isOwner(Player player, String dimension) {
        File file = dimensionFiles.get(dimension + ".yml");
        if (file == null || !file.exists()) return true;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String ownerUUID = config.getString("owner");
        if (ownerUUID == null) return true;

        return !player.getUniqueId().toString().equals(ownerUUID);
    }

    public static void invite(Player owner, String dimension, Player target) {
        if (isOwner(owner, dimension)) {
            owner.sendMessage("§cYou are not the owner of this world!");
            return;
        }
        invitedPlayers.computeIfAbsent(dimension, k -> new HashSet<>()).add(target.getUniqueId());
        owner.sendMessage("§aYou invited " + target.getName() + " to " + dimension);
        target.sendMessage("§aYou have been invited to world " + dimension + " by " + owner.getName());
    }
}