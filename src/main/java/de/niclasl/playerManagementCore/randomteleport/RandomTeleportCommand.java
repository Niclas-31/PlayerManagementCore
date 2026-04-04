package de.niclasl.playerManagementCore.randomteleport;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import de.niclasl.playerManagementCore.audit.AuditManager;
import de.niclasl.playerManagementCore.audit.model.AuditAction;
import de.niclasl.playerManagementCore.audit.model.AuditType;
import de.niclasl.playerManagementCore.portal.PortalType;
import de.niclasl.playerManagementCore.portal.manager.PortalConfigManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World.Environment;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTeleportCommand implements CommandExecutor, TabCompleter {

    private final PlayerManagementCore plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public RandomTeleportCommand(PlayerManagementCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("rtp")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        long cooldownSeconds = 300;
        long now = System.currentTimeMillis();

        if (cooldowns.containsKey(player)) {
            long lastUsed = cooldowns.get(player);
            long remaining = (lastUsed + (cooldownSeconds * 1000) - now) / 1000;

            if (remaining > 0) {
                player.sendMessage("§cPlease wait §e" + remaining + " seconds§c before teleporting again.");
                return true;
            }
        }

        World world = player.getWorld();
        Environment env = world.getEnvironment();

        final int[] minRange = {(env == Environment.NETHER) ? 62 : 500};

        player.sendMessage("§7Search for a safe teleportation destination…");

        new BukkitRunnable() {
            @Override
            public void run() {

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        Location foundLocation = null;

                        WorldBorder border = world.getWorldBorder();
                        Location center = border.getCenter();
                        double size = border.getSize() / 2.0;

                        double margin = 10;

                        double minX = center.getX() - size + margin;
                        double maxX = center.getX() + size - margin;
                        double minZ = center.getZ() - size + margin;
                        double maxZ = center.getZ() + size - margin;

                        minRange[0] = (int) Math.min(minRange[0], size - 50);

                        for (int i = 0; i < 20; i++) {
                            int x = (int) ThreadLocalRandom.current().nextDouble(minX, maxX);
                            int z = (int) ThreadLocalRandom.current().nextDouble(minZ, maxZ);

                            if (Math.abs(x) < minRange[0] && Math.abs(z) < minRange[0]) continue;

                            if (!border.isInside(new Location(world, x, 100, z))) continue;

                            int y = env == Environment.NETHER
                                    ? findSafeNetherY(world, x, z)
                                    : world.getHighestBlockYAt(x, z);

                            if (y == -1) continue;

                            foundLocation = new Location(world, x + 0.5, y + 1, z + 0.5);
                            break;
                        }

                        if (foundLocation == null) {
                            player.sendMessage("§cNo safe place to teleport could be found.");
                            return;
                        }

                        Location finalLocation = foundLocation;

                        player.sendMessage("§aTeleport in 5 seconds...");

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.teleport(finalLocation);
                                player.sendMessage("§aYou have been successfully teleported.");
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                cooldowns.put(player, System.currentTimeMillis());
                            }
                        }.runTaskLater(plugin, 100L);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);

        if (PortalConfigManager.isPortalEnabled(PortalType.PLUGIN)) {
            AuditManager.log(
                    player,
                    AuditType.TELEPORT,
                    AuditAction.EXECUTE,
                    player,
                    "Random Teleport"
            );
        }

        return true;
    }

    private int findSafeNetherY(World world, int x, int z) {
        for (int y = 30; y < 100; y++) {
            Material block = world.getBlockAt(x, y, z).getType();
            Material above = world.getBlockAt(x, y + 1, z).getType();
            Material below = world.getBlockAt(x, y - 1, z).getType();

            if (block == Material.AIR && above == Material.AIR && below.isSolid() && below != Material.LAVA) {
                return y;
            }
        }
        return -1;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        return List.of();
    }
}