package de.niclasl.multiPlugin.mob_system.listener;

import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.entity.Player;

public class MobIgnoreListener implements Listener {

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player) {
            if (MobManager.isOwner(event.getEntity(), player)) {
                event.setCancelled(true); // greift seinen Spawner nicht an
            }
        }
    }
}
