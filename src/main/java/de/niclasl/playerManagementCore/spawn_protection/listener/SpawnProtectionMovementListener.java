package de.niclasl.playerManagementCore.spawn_protection.listener;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import de.niclasl.playerManagementCore.spawn_protection.manager.ProtectionTaskManager;
import de.niclasl.playerManagementCore.spawn_protection.manager.SpawnManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Objects;

public record SpawnProtectionMovementListener(PlayerManagementCore plugin) implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (plugin.getSpawnProtectionConfigManager().isProtectionEnabled()) {
            ProtectionTaskManager.stopTask(player);
            return;
        }

        double triggerRadius = plugin.getSpawnProtectionConfigManager().getBlockProtectionRadius() +
                plugin.getSpawnProtectionConfigManager().getDamageProtectionRadius();

        World.Environment env = player.getWorld().getEnvironment();
        List<Location> spawnsToCheck;

        switch (env) {
            case NETHER -> spawnsToCheck = SpawnManager.getAllNetherSpawns();
            case NORMAL -> spawnsToCheck = SpawnManager.getAllOverworldSpawns();
            case THE_END -> spawnsToCheck = SpawnManager.getAllEndSpawns();
            default -> {
                ProtectionTaskManager.stopTask(player);
                return;
            }
        }

        Location playerLoc = player.getLocation();
        boolean isInProtection = false;

        for (Location spawn : spawnsToCheck) {
            if (!Objects.equals(spawn.getWorld(), playerLoc.getWorld())) continue;

            if (playerLoc.distanceSquared(spawn) <= triggerRadius * triggerRadius) {
                isInProtection = true;
                break;
            }
        }

        if (isInProtection) {
            ProtectionTaskManager.startTask(player, plugin);
        } else {
            ProtectionTaskManager.stopTask(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ProtectionTaskManager.stopTask(event.getPlayer());
    }
}