package de.niclasl.playerManagementCore.spawn_protection;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import de.niclasl.playerManagementCore.spawn_protection.manager.SpawnManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class ProtectionActionbarTask extends BukkitRunnable {

    private final Player player;
    private final PlayerManagementCore plugin;

    private double lastRemainingBlocks = -1;

    public ProtectionActionbarTask(Player player, PlayerManagementCore plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        double blockProtectionRadius = plugin.getSpawnProtectionConfigManager().getBlockProtectionRadius();
        double damageProtectionRadius = plugin.getSpawnProtectionConfigManager().getDamageProtectionRadius() + blockProtectionRadius;

        Location playerLoc = player.getLocation();
        World.Environment env = Objects.requireNonNull(playerLoc.getWorld()).getEnvironment();

        List<Location> spawns;

        switch (env) {
            case NETHER -> spawns = SpawnManager.getAllNetherSpawns();
            case NORMAL -> spawns = SpawnManager.getAllOverworldSpawns();
            case THE_END -> spawns = SpawnManager.getAllEndSpawns();
            default -> {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                return;
            }
        }

        Location nearestSpawn = null;
        double nearestDistanceSq = Double.MAX_VALUE;

        for (Location spawn : spawns) {
            if (!Objects.equals(spawn.getWorld(), playerLoc.getWorld())) continue;

            double distSq = playerLoc.distanceSquared(spawn);
            if (distSq < nearestDistanceSq) {
                nearestDistanceSq = distSq;
                nearestSpawn = spawn;
            }
        }

        if (nearestSpawn == null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            return;
        }

        double distance = Math.sqrt(nearestDistanceSq);

        double remainingBlocks = -1;
        String message = " ";

        if (distance <= blockProtectionRadius) {
            remainingBlocks = blockProtectionRadius - distance;
            message = "§cSpawn protection: No mining possible (" + String.format("%.2f", remainingBlocks) + " blocks)";
        } else if (distance <= damageProtectionRadius) {
            remainingBlocks = damageProtectionRadius - distance;
            message = "§eSpawn protection: No damage possible (" + String.format("%.2f", remainingBlocks) + " blocks)";
        }

        if (remainingBlocks != lastRemainingBlocks) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            lastRemainingBlocks = remainingBlocks;
        }
    }
}