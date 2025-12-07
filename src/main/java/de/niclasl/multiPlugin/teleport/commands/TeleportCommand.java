package de.niclasl.multiPlugin.teleport.commands;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.teleport.manager.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class TeleportCommand implements CommandExecutor, TabCompleter {

    private static TeleportManager teleportManager;
    private final MultiPlugin plugin;

    public TeleportCommand(TeleportManager teleportManager, MultiPlugin plugin) {
        TeleportCommand.teleportManager = teleportManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            plugin.getDimensionGui().open(player, player, 1);
            return true;
        }

        // SET
        if (args[0].equalsIgnoreCase("set")) {
            if (!player.hasPermission("multiplugin.teleport.dimension.setcoords")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to set teleport locations.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /teleport-dimension set <dimension_name>");
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

        // CREATE
        if (args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("multiplugin.teleport.dimension.create")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to create a dimension.");
                return true;
            }

            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /teleport-dimension create <dimension_name> <dimension_type>");
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
            return true;
        }

        // DELETE
        if (args[0].equalsIgnoreCase("delete")) {
            if (!player.hasPermission("multiplugin.teleport.dimension.delete")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to delete dimensions.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /teleport-dimension delete <world_name>");
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

        // INVITE
        if (args[0].equalsIgnoreCase("invite")) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /teleport-dimension invite <world> <player>");
                return true;
            }

            String targetName = args[2];
            String dimension = args[1];

            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player '" + targetName + "' not found.");
                return true;
            }

            if (!TeleportManager.dimensionExists(dimension)) {
                player.sendMessage(ChatColor.RED + "World '" + dimension + "' does not exist.");
                return true;
            }

            // Owner-Check
            if (!TeleportManager.isOwner(player, dimension)) {
                player.sendMessage(ChatColor.RED + "You are not the owner of world '" + dimension + "'.");
                return true;
            }

            TeleportManager.invite(player, dimension, target);
            return true;
        }

        // SET DELAY
        if (args[0].equalsIgnoreCase("setdelay")) {
            if (args.length < 3) {
                player.sendMessage(ChatColor.RED + "Usage: /teleport-dimension setdelay <world> <seconds>");
                return true;
            }

            String dimension = args[1];
            String delayArg = args[2];

            if (!TeleportManager.dimensionExists(dimension)) {
                player.sendMessage(ChatColor.RED + "World '" + dimension + "' does not exist.");
                return true;
            }

            // Owner-Check
            if (!TeleportManager.isOwner(player, dimension)) {
                player.sendMessage(ChatColor.RED + "You are not the owner of world '" + dimension + "'.");
                return true;
            }

            int delay;
            try {
                delay = Integer.parseInt(delayArg);
                if (delay < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Delay must be a non-negative number.");
                return true;
            }

            TeleportManager.setTeleportDelay(player, dimension, delay);
            return true;
        }

        // TELEPORT
        String dimension = args[0].toLowerCase();

        if (!TeleportManager.dimensionExists(dimension)) {
            player.sendMessage(ChatColor.RED + "Unknown dimension. Use /teleport-dimension to see all.");
            return true;
        }

        Location targetLoc = TeleportManager.getLocation(dimension);
        if (targetLoc == null) {
            player.sendMessage(ChatColor.RED + "No location set for dimension §6'" + dimension + "'§c.");
            return true;
        }

        UUID owner = TeleportManager.getOwner(dimension);

        if (owner != null && !owner.equals(player.toString())) {
            // Prüfe auf Einladung oder explizite Permission
            if (!TeleportManager.hasAccess(player, dimension) &&
                    !player.hasPermission("multiplugin.teleport.dimension.private." + dimension)) {

                player.sendMessage(ChatColor.RED + "You don't have permission to teleport to this private dimension.");
                return true;
            }
        } else {
            if (!player.hasPermission("multiplugin.teleport.dimension")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to teleport.");
                return true;
            }
        }

        // Verzögertes Teleportieren mit Effekten
        teleportManager.teleportWithDelay(player, targetLoc, TeleportManager.getTeleportDelay(dimension), dimension);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("set", "create", "delete", "invite", "setdelay"));
            completions.addAll(TeleportManager.getAllDimensions()); // Direkter Teleport
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("setdelay") || args[0].equalsIgnoreCase("set")) {
                completions.addAll(TeleportManager.getAllDimensions());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("invite")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create")) {
                completions.addAll(Arrays.asList("overworld", "nether", "end"));
            }
        }
        return completions;
    }
}