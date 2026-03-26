package de.niclasl.playerManagementCore.spawn_protection.listener;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import de.niclasl.playerManagementCore.spawn_protection.manager.SpawnManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class SpawnProtectionListener implements Listener {

    private final PlayerManagementCore plugin;

    public SpawnProtectionListener(PlayerManagementCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (plugin.getSpawnProtectionConfigManager().isProtectionEnabled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        double blockProtectionRadius = plugin.getSpawnProtectionConfigManager().getBlockProtectionRadius();
        double damageProtectionRadius = plugin.getSpawnProtectionConfigManager().getDamageProtectionRadius() + blockProtectionRadius;

        List<Location> spawns = getSpawnsForEnvironment(player.getWorld().getEnvironment());
        if (spawns.isEmpty()) return;

        for (Location spawn : spawns) {
            if (!player.getWorld().equals(spawn.getWorld())) continue;

            if (player.getLocation().distanceSquared(spawn) <= damageProtectionRadius * damageProtectionRadius) {
                event.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (plugin.getSpawnProtectionConfigManager().isProtectionEnabled()) return;

        double blockProtectionRadius = plugin.getSpawnProtectionConfigManager().getBlockProtectionRadius();

        Player player = event.getPlayer();
        List<Location> spawns = getSpawnsForEnvironment(player.getWorld().getEnvironment());
        if (spawns.isEmpty()) return;

        for (Location spawn : spawns) {
            if (!player.getWorld().equals(spawn.getWorld())) continue;

            if (player.getLocation().distanceSquared(spawn) <= blockProtectionRadius * blockProtectionRadius) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to mine anything here (" + player.getWorld().getEnvironment().name() + " spawn protection)");
                break;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (plugin.getSpawnProtectionConfigManager().isProtectionEnabled()) return;

        double blockProtectionRadius = plugin.getSpawnProtectionConfigManager().getBlockProtectionRadius();

        Player player = event.getPlayer();
        List<Location> spawns = getSpawnsForEnvironment(player.getWorld().getEnvironment());
        if (spawns.isEmpty()) return;

        for (Location spawn : spawns) {
            if (!player.getWorld().equals(spawn.getWorld())) continue;

            if (player.getLocation().distanceSquared(spawn) <= blockProtectionRadius * blockProtectionRadius) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to place blocks here (" + player.getWorld().getEnvironment().name() + " spawn protection)");
                break;
            }
        }
    }

    private List<Location> getSpawnsForEnvironment(World.Environment env) {
        return switch (env) {
            case NETHER -> SpawnManager.getAllNetherSpawns();
            case NORMAL -> SpawnManager.getAllOverworldSpawns();
            case THE_END -> SpawnManager.getAllEndSpawns();
            default -> List.of();
        };
    }
}