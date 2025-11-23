package de.niclasl.multiPlugin.spawn_protection;

import de.niclasl.multiPlugin.spawn_protection.manager.SpawnManager;
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

    private static final double blockProtectionRadius = 18;
    private static final double damageProtectionRadius = 46 + blockProtectionRadius;

    private int lastRemainingBlocks = -1;

    public ProtectionActionbarTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            cancel();
            return;
        }

        Location playerLoc = player.getLocation();
        World.Environment env = Objects.requireNonNull(playerLoc.getWorld()).getEnvironment();

        List<Location> spawns;

        switch (env) {
            case NETHER -> spawns = SpawnManager.getAllNetherSpawns();
            case NORMAL -> spawns = SpawnManager.getAllOverworldSpawns();
            case THE_END -> spawns = SpawnManager.getAllEndSpawns();
            default -> {
                // Keine Spawns in anderen Welten
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

        int remainingBlocks = -1;
        String message = " ";

        if (distance <= blockProtectionRadius) {
            remainingBlocks = (int) Math.ceil(blockProtectionRadius - distance);
            message = "§cSpawn protection: No mining possible (" + remainingBlocks + " blocks)";
        } else if (distance <= damageProtectionRadius) {
            remainingBlocks = (int) Math.ceil(damageProtectionRadius - distance);
            message = "§eSpawn protection: No damage possible (" + remainingBlocks + " blocks)";
        }

        if (remainingBlocks != lastRemainingBlocks) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            lastRemainingBlocks = remainingBlocks;
        }
    }
}