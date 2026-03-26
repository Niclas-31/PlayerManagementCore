package de.niclasl.playerManagementCore.vanish_system.manager;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public record VanishManager(PlayerManagementCore plugin) {

    private static File vanishFile;
    private static FileConfiguration vanishConfig;

    public boolean isVanished(UUID uuid) {
        return getVanishConfig().getBoolean(uuid.toString(), false);
    }

    public void setVanish(UUID uuid, boolean vanish) {
        getVanishConfig().set(uuid.toString(), vanish);
        saveVanishConfig();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (vanish) {
                    if (!other.hasPermission("playerManagementCore.vanish.see")) {
                        other.hidePlayer(plugin, player);
                    }
                } else {
                    other.showPlayer(plugin, player);
                }
            }

            player.setSilent(vanish);
            player.setCollidable(!vanish);
            player.setInvulnerable(vanish);
        }
    }
    public void createVanishFile() {
        vanishFile = new File(plugin.getDataFolder(), "vanish.yml");

        if (!vanishFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                vanishFile.createNewFile();
                plugin.getLogger().info("Created vanish.yml successfully.");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create vanish.yml!");
                e.printStackTrace();
            }
        }

        vanishConfig = YamlConfiguration.loadConfiguration(vanishFile);
    }

    public FileConfiguration getVanishConfig() {
        return vanishConfig;
    }

    public void saveVanishConfig() {
        if (vanishConfig == null || vanishFile == null) {
            plugin.getLogger().warning("Vanish config not initialized.");
            return;
        }

        try {
            vanishConfig.save(vanishFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save vanish.yml!");
            e.printStackTrace();
        }
    }

    public void loadVanishConfig() {
        vanishFile = new File(plugin.getDataFolder(), "vanish.yml");

        if (!vanishFile.exists()) {
            try {
                vanishFile.getParentFile().mkdirs();
                vanishFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create vanish.yml!");
                e.printStackTrace();
            }
        }

        vanishConfig = YamlConfiguration.loadConfiguration(vanishFile);
    }
}
