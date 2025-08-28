package de.niclasl.multiPlugin.playtime.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeManager {
    private static File playtimeFolder;
    private final File timeResetFile;
    private final YamlConfiguration resetConfig;

    public PlaytimeManager(File pluginDataFolder) {
        playtimeFolder = new File(pluginDataFolder, "playtime");
        if (!playtimeFolder.exists()) playtimeFolder.mkdirs();

        this.timeResetFile = new File(pluginDataFolder, "time_reset.yml");
        if (!timeResetFile.exists()) {
            saveDefaultTimeResetConfig();
        }

        this.resetConfig = YamlConfiguration.loadConfiguration(timeResetFile);
    }

    private void saveDefaultTimeResetConfig() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("limits.seconds", 60);
        config.set("limits.minutes", 60);
        config.set("limits.hours", 24);
        config.set("limits.days", 7);
        config.set("limits.weeks", 4);
        config.set("limits.months", 12);
        try {
            config.save(timeResetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getPlayerFile(UUID uuid) {
        return new File(playtimeFolder, uuid.toString() + ".yml");
    }

    public static YamlConfiguration getPlayerConfig(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (!file.exists()) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("time.seconds", 0);
            config.set("time.minutes", 0);
            config.set("time.hours", 0);
            config.set("time.days", 0);
            config.set("time.months", 0);
            config.set("time.years", 0);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return config;
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void savePlayerConfig(UUID uuid, YamlConfiguration config) {
        try {
            config.save(getPlayerFile(uuid));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSeconds(UUID uuid, int secondsToAdd) {
        YamlConfiguration playerConfig = getPlayerConfig(uuid);
        Map<String, Integer> limits = getLimits();

        int seconds = playerConfig.getInt("time.seconds") + secondsToAdd;
        int minutes = playerConfig.getInt("time.minutes");
        int hours = playerConfig.getInt("time.hours");
        int days = playerConfig.getInt("time.days");
        int months = playerConfig.getInt("time.months");
        int years = playerConfig.getInt("time.years");

        if (seconds >= limits.get("seconds")) {
            minutes += seconds / limits.get("seconds");
            seconds %= limits.get("seconds");
        }
        if (minutes >= limits.get("minutes")) {
            hours += minutes / limits.get("minutes");
            minutes %= limits.get("minutes");
        }
        if (hours >= limits.get("hours")) {
            days += hours / limits.get("hours");
            hours %= limits.get("hours");
        }
        if (days >= limits.get("days")) {
            months += days / limits.get("days");
            days %= limits.get("days");
        }
        if (months >= limits.get("months")) {
            years += months / limits.get("months");
            months %= limits.get("months");
        }

        playerConfig.set("time.seconds", seconds);
        playerConfig.set("time.minutes", minutes);
        playerConfig.set("time.hours", hours);
        playerConfig.set("time.days", days);
        playerConfig.set("time.months", months);
        playerConfig.set("time.years", years);

        savePlayerConfig(uuid, playerConfig);
    }

    private Map<String, Integer> getLimits() {
        Map<String, Integer> limits = new HashMap<>();

        limits.put("seconds", resetConfig.getInt("limits.seconds", 60));
        limits.put("minutes", resetConfig.getInt("limits.minutes", 60));
        limits.put("hours", resetConfig.getInt("limits.hours", 24));
        limits.put("days", resetConfig.getInt("limits.days", 7));
        limits.put("weeks", resetConfig.getInt("limits.weeks", 4));
        limits.put("months", resetConfig.getInt("limits.months", 12));

        return limits;
    }
}