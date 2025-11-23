package de.niclasl.multiPlugin.armor;

import de.niclasl.multiPlugin.armor.manager.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ArmorUtils {

    // repariert Rüstung wenn Schaden über threshold%
    public static void autoRepairArmor(Player player, double thresholdPercent, int maxRepairPerTick) {

        // Nicht im Kampf reparieren (5 Sekunden ohne Schaden)
        if (CombatManager.isInCombat(player)) return;

        // Nicht reparieren, wenn Elytra genutzt wird
        if (player.isGliding()) return;

        ItemStack[] armor = player.getInventory().getArmorContents();

        for (int i = 0; i < armor.length; i++) {
            ItemStack item = armor[i];
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (!(meta instanceof Damageable damageable)) continue;

            int maxDurability = item.getType().getMaxDurability();
            int currentDamage = damageable.getDamage();

            if (maxDurability <= 0) continue; // Sicherheitscheck für Items ohne Durability

            // Prozent Schaden
            double currentPercent = (currentDamage * 100.0) / maxDurability;

            // Repariert erst, wenn Schaden über thresholdPercent%
            if (currentPercent < thresholdPercent) continue;

            // Reparaturmenge berechnen
            int repairAmount = Math.min(maxRepairPerTick, currentDamage);

            if (repairAmount <= 0) continue;

            // Rüstung reparieren
            damageable.setDamage(currentDamage - repairAmount);
            item.setItemMeta(damageable);

            // aktualisieren
            armor[i] = item;
        }

        player.getInventory().setArmorContents(armor);
    }

}
