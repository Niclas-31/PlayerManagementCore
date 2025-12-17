package de.niclasl.multiPlugin.portal.listener;

import de.niclasl.multiPlugin.portal.api.PortalApi;
import de.niclasl.multiPlugin.portal.manager.PortalConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class PortalListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<String> whitelist = PortalConfigManager.getWhitelist();

        for (String pluginName : whitelist) {
            PortalApi.registerPluginTeleport(player, pluginName);
        }
    }
}
