package de.niclasl.multiPlugin.spawn_protection.manager;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.spawn_protection.ProtectionActionbarTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProtectionTaskManager {

    private static final Map<UUID, BukkitTask> tasks = new HashMap<>();

    public static void startTask(Player player, MultiPlugin plugin) {
        if (tasks.containsKey(player.getUniqueId())) return;

        BukkitTask task = new ProtectionActionbarTask(player).runTaskTimer(plugin, 0L, 2L);
        tasks.put(player.getUniqueId(), task);
    }

    public static void stopTask(Player player) {
        BukkitTask task = tasks.remove(player.getUniqueId());
        if (task != null) task.cancel();
    }
}
