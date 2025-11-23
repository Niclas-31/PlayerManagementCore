package de.niclasl.multiPlugin.armor.task;

import de.niclasl.multiPlugin.armor.ArmorUtils;
import de.niclasl.multiPlugin.armor.manager.RepairManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RepairTask {

    public static void startAutoRepair(JavaPlugin plugin, int intervalTicks, double thresholdPercent, int repairAmount) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (RepairManager.isRepairEnabled(player)) {
                    ArmorUtils.autoRepairArmor(player, thresholdPercent, repairAmount);
                }
            }
        }, 0L, intervalTicks); // intervalTicks = wie oft in Ticks (20 Ticks = 1 Sekunde)
    }
}
