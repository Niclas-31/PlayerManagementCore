package de.niclasl.playerManagementCore.portal.listener;

import de.niclasl.playerManagementCore.portal.PortalType;
import de.niclasl.playerManagementCore.portal.api.PortalApi;
import de.niclasl.playerManagementCore.portal.manager.PortalConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportBlockerListener implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        Player p = event.getPlayer();
        PortalType type = mapPortalEvent(event);

        if (PortalConfigManager.isPortalEnabled(type)) return;

        event.setCancelled(true);
        p.sendMessage("§cThis portal (" + type.name() + ") is disabled!");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        PortalType type = mapTeleportEvent(event);

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {

            String plugin = PortalApi.getActivePlugin(p);

            if (PortalConfigManager.isWhitelisted(plugin)) {
                PortalApi.clearActivePlugin(p);
                return;
            }

            if (!PortalConfigManager.isPortalEnabled(PortalType.PLUGIN)) {
                event.setCancelled(true);
                p.sendMessage("§cPlugin teleports are disabled for your plugin!");
            }

            return;
        }

        if (PortalConfigManager.isPortalEnabled(type)) return;

        event.setCancelled(true);
        p.sendMessage("§cThis teleport (" + type.name() + ") is disabled!");
    }

    private PortalType mapPortalEvent(PlayerPortalEvent event) {
        return switch (event.getCause()) {
            case NETHER_PORTAL -> PortalType.NETHER_PORTAL;
            case END_PORTAL -> PortalType.END_PORTAL;
            default -> PortalType.UNKNOWN;
        };
    }

    private PortalType mapTeleportEvent(PlayerTeleportEvent event) {
        return switch (event.getCause()) {
            case CHORUS_FRUIT -> PortalType.CHORUS_FRUIT;
            case ENDER_PEARL -> PortalType.ENDER_PEARL;
            case PLUGIN -> PortalType.PLUGIN;
            case COMMAND -> PortalType.COMMAND;
            default -> PortalType.UNKNOWN;
        };
    }
}