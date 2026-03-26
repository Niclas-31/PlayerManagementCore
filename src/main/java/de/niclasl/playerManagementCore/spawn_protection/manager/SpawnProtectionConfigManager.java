package de.niclasl.playerManagementCore.spawn_protection.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SpawnProtectionConfigManager {

    private final JavaPlugin plugin;

    private File file;
    private FileConfiguration config;

    public SpawnProtectionConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void createFile() {
        file = new File(plugin.getDataFolder(), "spawnProtection.yml");

        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
                plugin.getLogger().info("Created spawnProtection.yml successfully.");
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create spawnProtection.yml!");
                e.printStackTrace();
            }
        }
    }

    public void load() {
        file = new File(plugin.getDataFolder(), "spawnProtection.yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create spawnProtection.yml!");
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        if (config == null || file == null) {
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save spawnProtection.yml");
            e.printStackTrace();
        }
    }

    public int getBlockProtectionRadius() {
        return config.getInt("blockProtectionRadius", 26);
    }

    public void setBlockProtectionRadius(int blockProtectionRadius) {
        config.set("blockProtectionRadius", blockProtectionRadius);
        save();
    }

    public int getDamageProtectionRadius() {
        return config.getInt("damageProtectionRadius", 46);
    }

    public void setDamageProtectionRadius(int damageProtectionRadius) {
        config.set("damageProtectionRadius", damageProtectionRadius);
        save();
    }

    public boolean isProtectionEnabled() {
        return !config.getBoolean("protection", true);
    }

    public void setProtectionEnabled(boolean enabled) {
        config.set("protection", enabled);
        save();
    }

    public FileConfiguration getConfig() {
        return config;
    }
}