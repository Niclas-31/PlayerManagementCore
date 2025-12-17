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

            if (!record.isPermanent() && record.isExpired() && record.getUnbanDate() == null) {

                String expectedUnban = record.calculateExpectedUnbanDate();

                if (expectedUnban == null) {
                    expectedUnban = LocalDateTime.now().toString();
                }

                record.setUnbanDate(expectedUnban);
                record.setUnbanBy("System");

                banHistoryManager.saveBanHistory(uuid, history);
                break;
            }
        }
    }
}