package de.niclasl.multiPlugin.portal.listener;

import de.niclasl.multiPlugin.portal.PortalType;
import de.niclasl.multiPlugin.portal.api.PortalApi;
import de.niclasl.multiPlugin.portal.manager.PortalConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.logging.Level;

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

        // Wenn Ursache COMMAND angegeben ist, override
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
        // Extra: blocke /tp /teleport direkt falls COMMAND deaktiviert ist
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

        // 1) Wenn PortalType.PLUGIN und Plugins global erlaubt -> erlaubt
        if (type == PortalType.PLUGIN && PortalConfigManager.isPortalEnabled(PortalType.PLUGIN)) {
            return true;
        }

        // 2) Poll API: Plugins können vor Teleport explizit Registrieren
        String registered = PortalApi.pollNextBypass(player);
        if (registered != null) {
            // Prüfe, ob registrierter Name in Whitelist ist (case-insensitive)
            List<String> wl = PortalConfigManager.getWhitelist();
            if (wl.stream().anyMatch(w -> w.equalsIgnoreCase(registered))) return true;
            // ansonsten trotzdem erlauben, weil Plugin selbst registrierte
            return true;
        }

        // 3) Best-Effort: versuche das verantwortliche Plugin aus StackTrace zu ermitteln
        String caller = detectCallingPluginName();
        if (caller != null) {
            List<String> wl = PortalConfigManager.getWhitelist();
            return wl.stream().anyMatch(w -> w.equalsIgnoreCase(caller));
        }

        // 4) Standard: false -> nicht bypassed
        return false;
    }

    /**
     * Versucht per StackTrace das Plugin zu ermitteln, das die Methode aufgerufen hat.
     * Nicht 100 % zuverlässig, aber in vielen Fällen brauchbar.
     */
    private String detectCallingPluginName() {
        try {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            // Gib einige Klassen am Ende auf Graph
            for (StackTraceElement el : trace) {
                String className = el.getClassName();
                try {
                    Class<?> c = Class.forName(className);
                    ClassLoader cl = c.getClassLoader();
                    for (org.bukkit.plugin.Plugin p : Bukkit.getPluginManager().getPlugins()) {
                        if (p.getClass().getClassLoader() == cl) {
                            return p.getName();
                        }
                    }
                } catch (ClassNotFoundException ignored) {
                } catch (Throwable t) {
                    Bukkit.getLogger().log(Level.FINE, "detectCallingPluginName error", t);
                }
            }
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.FINE, "detectCallingPluginName outer error", t);
        }
        return null;
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
