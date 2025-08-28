package de.niclasl.multiPlugin.playtime.listener;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.playtime.manager.PlaytimeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeListener implements Listener {

    private final PlaytimeManager playtimeManager;
    private final Map<UUID, BukkitRunnable> timers = new HashMap<>();
    private final MultiPlugin plugin; // Ersetze das mit deinem Plugin-Haupttyp

    public PlaytimeListener(MultiPlugin plugin, PlaytimeManager playtimeManager) {
        this.plugin = plugin;
        this.playtimeManager = playtimeManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                playtimeManager.addSeconds(uuid, 1);
            }
        };

        runnable.runTaskTimer(plugin, 20L, 20L); // 1 Sekunde
        timers.put(uuid, runnable);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // Timer stoppen
        BukkitRunnable runnable = timers.remove(uuid);
        if (runnable != null) {
            runnable.cancel();
        }

        // Zeit speichern (optional, falls nötig)
        playtimeManager.savePlayerConfig(uuid, playtimeManager.getPlayerConfig(uuid));
    }
}
