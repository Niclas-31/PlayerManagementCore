package de.niclasl.playerManagementCore.portal.manager;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import de.niclasl.playerManagementCore.portal.PortalType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.List;

public record PortalConfigManager(PlayerManagementCore plugin) {

    private static File dataFile;
    private static FileConfiguration cfg;

    public static boolean isWhitelisted(String plugin) {
        if (plugin == null || plugin.isBlank()) return false;

        List<String> whitelist = PortalConfigManager.getWhitelist();

        return whitelist.stream().anyMatch(p -> p.equalsIgnoreCase(plugin));
    }

    public void init() {
        dataFile = new File(plugin.getDataFolder(), "portals.yml");

        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();

                cfg = YamlConfiguration.loadConfiguration(dataFile);

                cfg.set("portals.NETHER_PORTAL", true);
                cfg.set("portals.END_PORTAL", true);
                cfg.set("portals.ENDER_PEARL", true);
                cfg.set("portals.CHORUS_FRUIT", true);
                cfg.set("portals.PLUGIN", true);
                cfg.set("portals.COMMAND", true);
                cfg.set("portals.UNKNOWN", true);

                cfg.set("whitelist", List.of("TeamWar"));
                save();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            cfg = YamlConfiguration.loadConfiguration(dataFile);
        }
    }

    public static boolean isPortalEnabled(PortalType type) {
        if (cfg == null || type == null) return true;
        return cfg.getBoolean("portals." + type.name(), true);
    }

    public void setPortalEnabled(PortalType type, boolean enabled) {
        if (cfg == null) return;
        cfg.set("portals." + type.name(), enabled);
        save();
    }

    public static List<String> getWhitelist() {
        if (cfg == null) return Collections.emptyList();
        return cfg.getStringList("whitelist").stream()
                .map(String::toLowerCase)
                .toList();
    }

    public void addToWhitelist(String pluginName) {
        if (pluginName == null) return;
        List<String> wl = cfg.getStringList("whitelist");
        if (wl.stream().noneMatch(s -> s.equalsIgnoreCase(pluginName))) {
            wl.add(pluginName.toLowerCase());
            cfg.set("whitelist", wl);
            save();
        }
    }

    public void removeFromWhitelist(String pluginName) {
        if (pluginName == null) return;
        List<String> wl = cfg.getStringList("whitelist");
        if (wl.removeIf(s -> s.equalsIgnoreCase(pluginName))) {
            cfg.set("whitelist", wl);
            save();
        }
    }

    public void save() {
        try {
            cfg.save(dataFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not save portals.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void reload() {
        cfg = YamlConfiguration.loadConfiguration(dataFile);
    }
}