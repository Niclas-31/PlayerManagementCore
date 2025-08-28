package de.niclasl.multiPlugin.spawn_protection.listener;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.spawn_protection.manager.ProtectionTaskManager;
import de.niclasl.multiPlugin.spawn_protection.manager.SpawnManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Objects;

public class SpawnProtectionMovementListener implements Listener {

    private final MultiPlugin plugin;

    private static final double triggerRadius = 63;

    public SpawnProtectionMovementListener(MultiPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World.Environment env = player.getWorld().getEnvironment();

        List<Location> spawnsToCheck;

        switch (env) {
            case NETHER:
                spawnsToCheck = SpawnManager.getAllNetherSpawns();
                break;
            case NORMAL:
                spawnsToCheck = SpawnManager.getAllOverworldSpawns();
                break;
            case THE_END:
                spawnsToCheck = SpawnManager.getAllEndSpawns();
                break;
            default:
                ProtectionTaskManager.stopTask(player);
                return;
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
