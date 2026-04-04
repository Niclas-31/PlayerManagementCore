package de.niclasl.playerManagementCore.teleport.commands;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import de.niclasl.playerManagementCore.teleport.manager.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeleportCommand implements CommandExecutor, TabCompleter {

    private static TeleportManager teleportManager;
    private final PlayerManagementCore plugin;

    public TeleportCommand(TeleportManager teleportManager, PlayerManagementCore plugin) {
        TeleportCommand.teleportManager = teleportManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }

        if (!player.hasPermission("teleport.dimension")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: ");
            player.sendMessage(ChatColor.RED + "/teleport-dimension gui <player>");
            player.sendMessage(ChatColor.RED + "/teleport-dimension set <dimension-name>");
            player.sendMessage(ChatColor.RED + "/teleport-dimension create <dimension-name> <dimension-type>");
            player.sendMessage(ChatColor.RED + "/teleport-dimension delete <dimension-name>");
            player.sendMessage(ChatColor.RED + "/teleport-dimension invite <world> <player>");
            player.sendMessage(ChatColor.RED + "/teleport-dimension setdelay <world> <seconds>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {

            case "gui" -> {
                if (!player.hasPermission("teleport.dimension.gui")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                String targetName = args[1];
                Player target = Bukkit.getPlayer(targetName);

                if (target == null) {
                    player.sendMessage("§cTarget player not found.");
                    return true;
                }

                plugin.getDimensionGui().open(player, target, 1);
                return true;
            }

            case "set" -> {
                if (!player.hasPermission("teleport.dimension.setcoords")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to set teleport locations.");
                    return true;
                }

                String dimension = args[1].toLowerCase();
                if (!TeleportManager.dimensionExists(dimension)) {
                    player.sendMessage(ChatColor.RED + "Dimension does not exist. Use /teleport-dimension create to create it first.");
                    return true;
                }

                String dimensionType = teleportManager.getDimensionType(dimension);
                if (dimensionType == null) dimensionType = "overworld";

                Location loc = player.getLocation();
                TeleportManager.setLocation(dimension, loc, dimensionType);
                player.sendMessage(ChatColor.GREEN + "Location for §6'" + dimension + "'§a set to §cX=§6" +
                        loc.getBlockX() + "§c Y=§6" + loc.getBlockY() + "§c Z=§6" + loc.getBlockZ());
                return true;
            }

            case "create" -> {
                if (!player.hasPermission("teleport.dimension.create")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to create a dimension.");
                    return true;
                }

                String dimension = args[1].toLowerCase();
                String dimensionType = args[2].toLowerCase();

                if (TeleportManager.dimensionExists(dimension)) {
                    player.sendMessage(ChatColor.RED + "This dimension already exists.");
                    return true;
                }

                TeleportManager.createDimension(dimension, dimensionType, player);
                teleportManager.setOwner(dimension, player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Dimension '" + dimension + "' created with type '" + dimensionType + "'.");
                return true;
            }

            case "delete" -> {
                if (!player.hasPermission("teleport.dimension.delete")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to delete dimensions.");
                    return true;
                }

                String dimensionToDelete = args[1].toLowerCase();
                if (TeleportManager.deleteDimension(dimensionToDelete)) {
                    player.sendMessage(ChatColor.GREEN + "Dimension §6'" + dimensionToDelete + "'§a deleted.");
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to delete dimension §6'" + dimensionToDelete + "'§c or it doesn't exist.");
                }
                return true;
            }

            case "invite" -> {
                if (!player.hasPermission("teleport.dimension.invite")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                String dimension = args[1];
                Player target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    player.sendMessage("§cTarget player not found.");
                    return true;
                } else {
                    target.getName();
                }

                if (!TeleportManager.dimensionExists(dimension)) {
                    player.sendMessage(ChatColor.RED + "World '" + dimension + "' does not exist.");
                    return true;
                }

                if (!TeleportManager.isOwner(player, dimension)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of world '" + dimension + "'.");
                    return true;
                }

                TeleportManager.invite(player, dimension, target);
                player.sendMessage("§aPlayer §e" + target.getName() + " §ahas been invited to §6" + dimension);
                return true;
            }

            case "setdelay" -> {
                if (!player.hasPermission("teleport.dimension.setdelay")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                String dimension = args[1];
                int delay;
                try {
                    delay = Integer.parseInt(args[2]);
                    if (delay < 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Delay must be a non-negative number.");
                    return true;
                }

                if (!TeleportManager.dimensionExists(dimension)) {
                    player.sendMessage(ChatColor.RED + "World '" + dimension + "' does not exist.");
                    return true;
                }

                if (!TeleportManager.isOwner(player, dimension)) {
                    player.sendMessage(ChatColor.RED + "You are not the owner of world '" + dimension + "'.");
                    return true;
                }

                TeleportManager.setTeleportDelay(player, dimension, delay);
                player.sendMessage("§aTeleport delay for §6" + dimension + "§a set to §e" + delay + " seconds.");
                return true;
            }

            default -> {
                player.sendMessage(ChatColor.RED + "Unknown subcommand. Available: gui, set, create, delete, invite, setdelay");
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("gui", "set", "create", "delete", "invite", "setdelay"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("setdelay") || args[0].equalsIgnoreCase("set")) {
                completions.addAll(TeleportManager.getAllDimensions());
            } else if (args[0].equalsIgnoreCase("gui")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("invite")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                completions.addAll(Arrays.asList("overworld", "nether", "end"));
            }
        }
        return completions;
    }
}