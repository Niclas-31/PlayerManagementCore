package de.niclasl.multiPlugin.portal.listener;

import de.niclasl.multiPlugin.portal.PortalType;
import de.niclasl.multiPlugin.portal.api.PortalApi;
import de.niclasl.multiPlugin.portal.manager.PortalConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class TeleportBlockerListener implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        PortalType type = mapPortalEvent(event);
        Player p = event.getPlayer();

        if (isBypassed(p, type)) return;
        if (PortalConfigManager.isPortalEnabled(type)) return;

        event.setCancelled(true);
        p.sendMessage("§cThis portal (" + type.name() + ") is disabled!");
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        PortalType type = mapTeleportEvent(event);
        Player p = event.getPlayer();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            type = PortalType.COMMAND;
        }

        if (isBypassed(p, type)) return;
        if (PortalConfigManager.isPortalEnabled(type)) return;

        event.setCancelled(true);
        p.sendMessage("§cThis teleport (" + type.name() + ") is disabled!");
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        Player p = event.getPlayer();

        if (isBypassed(p, PortalType.COMMAND)) return;
        if (PortalConfigManager.isPortalEnabled(PortalType.COMMAND)) return;

        if (msg.startsWith("/tp") || msg.startsWith("/teleport")) {
            event.setCancelled(true);
            p.sendMessage("§cTeleport commands are currently disabled!");
        }
    }

    private boolean isBypassed(Player player, PortalType type) {
        List<String> whitelist = PortalConfigManager.getWhitelist();

        String bypassedPlugin = PortalApi.getNextBypass(player);
        if (bypassedPlugin != null && whitelist.stream().anyMatch(w -> w.equalsIgnoreCase(bypassedPlugin))) {
            return true;
        }

        return PortalConfigManager.isPortalEnabled(type);
    }

    private PortalType mapPortalEvent(PlayerPortalEvent event) {
        return switch (event.getCause()) {
            case NETHER_PORTAL -> PortalType.NETHER_PORTAL;
            case END_PORTAL -> PortalType.END_PORTAL;
            case END_GATEWAY -> PortalType.END_GATEWAY;
            default -> PortalType.UNKNOWN;
        };
    }

    private PortalType mapTeleportEvent(PlayerTeleportEvent event) {
        return switch (event.getCause()) {
            case CHORUS_FRUIT -> PortalType.CHORUS_FRUIT;
            case DISMOUNT -> PortalType.DISMOUNT;
            case ENDER_PEARL -> PortalType.ENDER_PEARL;
            case EXIT_BED -> PortalType.EXIT_BED;
            case SPECTATE -> PortalType.SPECTATE;
            case PLUGIN -> PortalType.PLUGIN;
            case COMMAND -> PortalType.COMMAND;
            default -> PortalType.UNKNOWN;
        };
    }
}