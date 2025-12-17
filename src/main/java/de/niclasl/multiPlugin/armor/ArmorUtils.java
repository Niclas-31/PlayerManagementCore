package de.niclasl.multiPlugin.armor;

import de.niclasl.multiPlugin.armor.manager.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ArmorUtils {

    public static void autoRepairArmor(Player player, double thresholdPercent, int maxRepairPerTick) {

        if (CombatManager.isInCombat(player)) return;

        if (player.isGliding()) return;

        ItemStack[] armor = player.getInventory().getArmorContents();

        for (int i = 0; i < armor.length; i++) {
            ItemStack item = armor[i];
            if (item == null) continue;

            ItemMeta meta = item.getItemMeta();
            if (!(meta instanceof Damageable damageable)) continue;

            int maxDurability = item.getType().getMaxDurability();
            int currentDamage = damageable.getDamage();

            if (maxDurability <= 0) continue;

            double currentPercent = (currentDamage * 100.0) / maxDurability;

            if (currentPercent < thresholdPercent) continue;

            int repairAmount = Math.min(maxRepairPerTick, currentDamage);

            if (repairAmount <= 0) continue;

            damageable.setDamage(currentDamage - repairAmount);
            item.setItemMeta(damageable);

            armor[i] = item;
        }

        player.getInventory().setArmorContents(armor);
    }

}
