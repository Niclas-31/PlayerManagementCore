package de.niclasl.multiPlugin.randomteleport;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World.Environment;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTeleportCommand implements CommandExecutor, TabCompleter {

    private final MultiPlugin plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public RandomTeleportCommand(MultiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        // --- Cooldown prüfen ---
        long cooldownSeconds = 300; // 5 Minuten
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

        int maxRange;
        int minRange;

        if (env == Environment.NETHER) {
            maxRange = 625;
            minRange = 62;
        } else {
            maxRange = 5000;
            minRange = 500;
        }

        player.sendMessage("§7Search for a safe teleportation destination…");
        new BukkitRunnable() {
            @Override
            public void run() {
                Location foundLocation = null;

                for (int i = 0; i < 20; i++) {
                    int x = ThreadLocalRandom.current().nextInt(-maxRange, maxRange);
                    int z = ThreadLocalRandom.current().nextInt(-maxRange, maxRange);

                    if (Math.abs(x) < minRange && Math.abs(z) < minRange) continue;

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
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage("§aTeleport in 5 seconds...");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.teleport(finalLocation);
                                player.sendMessage("§aYou have been successfully teleported.");
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                                cooldowns.put(player, System.currentTimeMillis()); // Cooldown setzen
                            }
                        }.runTaskLater(plugin, 100L); // 5 Sekunden Delay
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);

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
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of();
    }
}