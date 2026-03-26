package de.niclasl.playerManagementCore.armor.listener;

import de.niclasl.playerManagementCore.armor.manager.CombatManager;
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
