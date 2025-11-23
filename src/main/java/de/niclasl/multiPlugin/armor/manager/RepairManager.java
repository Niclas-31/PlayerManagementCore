package de.niclasl.multiPlugin.armor.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RepairManager {

    private static final Set<UUID> repairingPlayers = new HashSet<>();

    private static File file;
    private static FileConfiguration config;

    // Muss im onEnable() aufgerufen werden!
    public static void init(File dataFolder) {
        file = new File(dataFolder, "repair-users.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        // gespeicherte UUIDs laden
        if (config.contains("repairing")) {
            for (String uuidStr : config.getStringList("repairing")) {
                repairingPlayers.add(UUID.fromString(uuidStr));
            }
        }
    }

    public static void save() {
        config.set("repairing", repairingPlayers.stream().map(UUID::toString).toList());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRepairEnabled(Player player) {
        return repairingPlayers.contains(player.getUniqueId());
    }

    public static void setRepairEnabled(Player player, boolean enabled) {
        if (enabled) {
            repairingPlayers.add(player.getUniqueId());
        } else {
            repairingPlayers.remove(player.getUniqueId());
        }
        save();
    }
}
