package de.niclasl.multiPlugin.portal.manager;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.portal.PortalType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public final class PortalConfigManager {

    private static File dataFile;
    private static FileConfiguration cfg;
    private static MultiPlugin plugin;

    public static void init(MultiPlugin pl) {
        plugin = pl;
        dataFile = new File(plugin.getDataFolder(), "portals.yml");

        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();

                cfg = YamlConfiguration.loadConfiguration(dataFile);

                // Default-Werte
                cfg.set("portals.NETHER_PORTAL", false);
                cfg.set("portals.END_PORTAL", false);
                cfg.set("portals.END_GATEWAY", false);
                cfg.set("portals.ENDER_PEARL", true);
                cfg.set("portals.CHORUS_FRUIT", true);
                cfg.set("portals.EXIT_BED", false);
                cfg.set("portals.DISMOUNT", true);
                cfg.set("portals.SPECTATE", true);
                cfg.set("portals.PLUGIN", true);
                cfg.set("portals.COMMAND", false);
                cfg.set("portals.UNKNOWN", true);

                cfg.set("whitelist", java.util.List.of("TeamWar"));
                save();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            cfg = YamlConfiguration.loadConfiguration(dataFile);
        }
    }

    public static boolean isPortalEnabled(PortalType type) {
        if (cfg == null) return true;
        return cfg.getBoolean("portals." + type.name(), true);
    }

    public static void setPortalEnabled(PortalType type, boolean enabled) {
        if (cfg == null) return;
        cfg.set("portals." + type.name(), enabled);
        save();
    }

    public static List<String> getWhitelist() {
        if (cfg == null) return java.util.Collections.emptyList();
        return cfg.getStringList("whitelist");
    }

    public static void addToWhitelist(String pluginName) {
        List<String> wl = cfg.getStringList("whitelist");
        if (!wl.contains(pluginName)) {
            wl.add(pluginName);
            cfg.set("whitelist", wl);
            save();
        }
    }

    public static void removeFromWhitelist(String pluginName) {
        List<String> wl = cfg.getStringList("whitelist");
        if (wl.removeIf(s -> s.equalsIgnoreCase(pluginName))) {
            cfg.set("whitelist", wl);
            save();
        }
    }

    public static void save() {
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