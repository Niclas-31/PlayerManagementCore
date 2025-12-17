package de.niclasl.multiPlugin.armor.listener;

import de.niclasl.multiPlugin.armor.manager.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        CombatManager.tagCombat(player);
    }
}
