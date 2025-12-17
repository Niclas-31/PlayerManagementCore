package de.niclasl.multiPlugin.portal.api;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PortalApi {

    private static final Map<UUID, String> nextBypass = new ConcurrentHashMap<>();

    public static void registerPluginTeleport(Player player, String pluginName) {
        if (player == null || pluginName == null) return;
        nextBypass.put(player.getUniqueId(), pluginName);
    }

    public static String getNextBypass(Player player) {
        if (player == null) return null;
        return nextBypass.get(player.getUniqueId());
    }

    public static void removePluginFromBypass(String pluginName) {
        if (pluginName == null) return;

        nextBypass.entrySet().removeIf(entry -> entry.getValue().equalsIgnoreCase(pluginName));
    }
}
