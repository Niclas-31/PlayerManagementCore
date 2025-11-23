package de.niclasl.multiPlugin.ban_system.listener;


import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import de.niclasl.multiPlugin.ban_system.model.BanRecord;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LoginListener(BanHistoryManager banHistoryManager) implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        List<BanRecord> history = BanHistoryManager.getBanHistory(uuid);
        if (history.isEmpty()) return;

        for (BanRecord record : history) {

            // Wenn Bann nicht permanent und schon abgelaufen → Unban setzen
            if (!record.isPermanent() && record.isExpired() && record.getUnbanDate() == null) {

                // Berechnetes Expected-Unban-Datum holen
                String expectedUnban = record.calculateExpectedUnbanDate();

                // Falls das null ist → aktuelles Datum als Fallback
                if (expectedUnban == null) {
                    expectedUnban = LocalDateTime.now().toString();
                }

                record.setUnbanDate(expectedUnban);
                record.setUnbanBy("System");

                // Speichern
                banHistoryManager.saveBanHistory(uuid, history);
                break;
            }
        }
    }
}