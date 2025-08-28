package de.niclasl.multiPlugin.portal.manager;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;

public class PortalManager implements Listener {

    private static final String FILE_NAME = "portals.yml";
    private static File file;
    private static FileConfiguration config;
    public enum PortalType {
        NETHER_PORTAL,
        END_PORTAL,
        COMMAND,
        CHORUS_FRUIT,
        DISMOUNT,
        END_GATEWAY,
        ENDER_PEARL,
        EXIT_BED,
        PLUGIN,
        SPECTATE,
        UNKNOWN
    }

    public static void init(MultiPlugin plugin) {
        file = new File(plugin.getDataFolder(), FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    // --- Getter / Setter ---

    public static boolean isPortalEnabled(PortalType type) {
        if (config == null) return false; // Default: aktiviert
        return config.getBoolean(type.name(), true);
    }

    public static void setPortalEnabled(PortalType type, boolean enabled) {
        if (config == null) return;
        config.set(type.name(), enabled);
        save();
    }

    private static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Event-Handling ---
    @EventHandler
    public void onPortalUse(PlayerPortalEvent event) {
        PortalType type = mapPortalEvent(event);
        if (!isPortalEnabled(type)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cThis portal (" + type.name() + ") is disabled!");
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        PortalType type = mapTeleportEvent(event);
        if (!isPortalEnabled(type)) {
            event.setCancelled(true);
            if (event.getPlayer() != null) {
                event.getPlayer().sendMessage("§cThis teleport (" + type.name() + ") is disabled!");
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();

        // Nur Befehle blockieren, die teleportieren
        if (!isPortalEnabled(PortalType.COMMAND)) {
            if (message.startsWith("/tp ") || message.startsWith("/teleport ")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cTeleport commands are currently disabled!");
            }
        }
    }

    // --- Mapping Methoden ---
    private PortalType mapPortalEvent(PlayerPortalEvent event) {
        switch (event.getCause()) {
            case NETHER_PORTAL -> { return PortalType.NETHER_PORTAL; }
            case END_PORTAL -> { return PortalType.END_PORTAL; }
            case END_GATEWAY -> { return PortalType.END_GATEWAY; }
            case DISMOUNT -> { return PortalType.DISMOUNT; }
            case CHORUS_FRUIT -> { return PortalType.CHORUS_FRUIT; }
            default -> { return PortalType.UNKNOWN; }
        }
    }

    private PortalType mapTeleportEvent(PlayerTeleportEvent event) {
        switch (event.getCause()) {
            case CHORUS_FRUIT -> { return PortalType.CHORUS_FRUIT; }
            case DISMOUNT -> { return PortalType.DISMOUNT; }
            case ENDER_PEARL -> { return PortalType.ENDER_PEARL; }
            case EXIT_BED -> { return PortalType.EXIT_BED; }
            case PLUGIN -> { return PortalType.PLUGIN; }
            case SPECTATE -> { return PortalType.SPECTATE; }
            default -> { return PortalType.UNKNOWN; }
        }
    }
}