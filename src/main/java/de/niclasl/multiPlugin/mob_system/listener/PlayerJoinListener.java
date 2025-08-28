package de.niclasl.multiPlugin.mob_system.listener;

import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerJoinListener implements Listener {

    private static MobManager mobManager;

    public PlayerJoinListener(MobManager mobManager) {
        PlayerJoinListener.mobManager = mobManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        mobManager.checkAndCreateDefaultIfAbsent(player.getUniqueId());
    }
}
