package de.niclasl.multiPlugin.ban_system.listener;

import de.niclasl.multiPlugin.ban_system.model.BanRecord;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class LoginListener implements Listener {

    private static BanHistoryManager banHistoryManager;

    public LoginListener(BanHistoryManager banHistoryManager) {
        LoginListener.banHistoryManager = banHistoryManager;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        List<BanRecord> history = banHistoryManager.getBanHistory(uuid);

        for (BanRecord record : history) {
            if (record.getUnbanDate() == null && record.isExpired() && !record.isPermanent()) {
                String expectedUnban = record.calculateExpectedUnbanDate();
                record.setUnbanDate(expectedUnban != null ? expectedUnban :
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                record.setUnbanBy("System");

                banHistoryManager.saveBanHistory(uuid, history);
                return;
            }
        }
    }
}
