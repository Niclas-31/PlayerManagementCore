package de.niclasl.multiPlugin.warn_system.manage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.Set;

public class WarnActionConfigManager {

    private final FileConfiguration config;

    public WarnActionConfigManager(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "warn_actions.yml");
        if (!file.exists()) {
            plugin.saveResource("warn_actions.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public Set<String> getThresholds() {
        return Objects.requireNonNull(config.getConfigurationSection("warn-actions")).getKeys(false);
    }

    public ConfigurationSection getThresholdSection(String threshold) {
        return config.getConfigurationSection("warn-actions." + threshold);
    }
}
