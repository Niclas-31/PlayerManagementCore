package de.niclasl.multiPlugin.vanish_system.manager;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class VanishManager {

    private static MultiPlugin plugin;

    public VanishManager(MultiPlugin plugin) {
        VanishManager.plugin = plugin;
    }

    public static boolean isVanished(UUID uuid) {
        return plugin.getVanishConfig().getBoolean(uuid.toString(), false);
    }

    public static void setVanish(UUID uuid, boolean vanish) {
        plugin.getVanishConfig().set(uuid.toString(), vanish);
        plugin.saveVanishConfig();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            // Sichtbarkeit für andere Spieler
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (vanish) {
                    if (!other.hasPermission("multiPlugin.vanish.see")) {
                        other.hidePlayer(plugin, player);
                    }
                } else {
                    other.showPlayer(plugin, player);
                }
            }

            // Eigenschaften für den Spieler setzen (nur einmal!)
            player.setSilent(vanish);
            player.setCollidable(!vanish);
            player.setInvulnerable(vanish);
        }
    }
}
