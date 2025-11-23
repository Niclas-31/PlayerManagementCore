package de.niclasl.multiPlugin.spawn_protection.listener;

import de.niclasl.multiPlugin.spawn_protection.manager.SpawnManager;
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

    private static final double blockProtectionRadius = 18;
    private static final double damageProtectionRadius = 46 + blockProtectionRadius;

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        World.Environment env = player.getWorld().getEnvironment();
        List<Location> spawns = getSpawnsForEnvironment(env);
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
        Player player = event.getPlayer();

        World.Environment env = player.getWorld().getEnvironment();
        List<Location> spawns = getSpawnsForEnvironment(env);
        if (spawns.isEmpty()) return;

        for (Location spawn : spawns) {
            if (!player.getWorld().equals(spawn.getWorld())) continue;

            if (player.getLocation().distanceSquared(spawn) <= blockProtectionRadius * blockProtectionRadius) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to mine anything here (" + env.name() + " spawn protection)");
                break;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        World.Environment env = player.getWorld().getEnvironment();
        List<Location> spawns = getSpawnsForEnvironment(env);
        if (spawns.isEmpty()) return;

        for (Location spawn : spawns) {
            if (!player.getWorld().equals(spawn.getWorld())) continue;

            if (player.getLocation().distanceSquared(spawn) <= blockProtectionRadius * blockProtectionRadius) {
                event.setCancelled(true);
                player.sendMessage("§cYou are not allowed to place blocks here (" + env.name() + " spawn protection)");
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