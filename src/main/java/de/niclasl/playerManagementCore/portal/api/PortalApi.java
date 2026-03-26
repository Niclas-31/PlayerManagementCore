package de.niclasl.playerManagementCore.portal.api;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalApi {

    private static final Map<UUID, String> activeTeleportPlugins = new HashMap<>();

    public static void markTeleport(Player player, String pluginName) {
        activeTeleportPlugins.put(player.getUniqueId(), pluginName);
    }

    public static String getActivePlugin(Player player) {
        return activeTeleportPlugins.get(player.getUniqueId());
    }

    public static void clearActivePlugin(Player player) {
        activeTeleportPlugins.remove(player.getUniqueId());
    }
}