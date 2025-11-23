package de.niclasl.multiPlugin.portal.api;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API: Plugins können vor einem geplanten Plugin-Teleport diese Methode aufrufen,
 * damit der Teleport nicht geblockt wird:
 * <p>
 * PortalApi.registerPluginTeleport(player, "TeamWar");
 * <p>
 * Diese Registerung gilt nur für das nächste Teleport-Event des Spielers.
 */
public final class PortalApi {

    private static final Map<UUID, String> nextBypass = new ConcurrentHashMap<>();

    public static void registerPluginTeleport(Player player, String pluginName) {
        if (player == null || pluginName == null) return;
        nextBypass.put(player.getUniqueId(), pluginName);
    }

    public static String pollNextBypass(Player player) {
        if (player == null) return null;
        return nextBypass.remove(player.getUniqueId());
    }
}
