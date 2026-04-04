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
import java.util.UUID;

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

        if (!player.hasPermission("dimension")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Helper-Methode für Argumentprüfung
        java.util.function.BiPredicate<Integer, String> checkArgs = (required, usage) -> {
            if (args.length < required) {
                player.sendMessage(ChatColor.RED + "Usage: " + usage);
                return false;
            }
            return true;
        };

        switch (subCommand) {

            case "gui" -> {
                if (!player.hasPermission("dimension.gui")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                if (!checkArgs.test(2, "/dimension gui <player>")) return true;

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cTarget player not found.");
                    return true;
                }

                plugin.getDimensionGui().open(player, target, 1);
                return true;
            }

            case "set" -> {
                if (!player.hasPermission("dimension.setcoords")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to set teleport locations.");
                    return true;
                }

                if (!checkArgs.test(2, "/dimension set <dimension-name>")) return true;

                String dimension = args[1].toLowerCase();
                if (!TeleportManager.dimensionExists(dimension)) {
                    player.sendMessage(ChatColor.RED + "Dimension does not exist. Use /dimension create to create it first.");
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
                if (!player.hasPermission("dimension.create")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to create a dimension.");
                    return true;
                }

                if (!checkArgs.test(3, "/dimension create <dimension-name> <dimension-type>")) return true;

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
                if (!player.hasPermission("dimension.delete")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to delete dimensions.");
                    return true;
                }

                if (!checkArgs.test(2, "/dimension delete <dimension-name>")) return true;

                String dimensionToDelete = args[1].toLowerCase();
                if (TeleportManager.deleteDimension(dimensionToDelete)) {
                    player.sendMessage(ChatColor.GREEN + "Dimension §6'" + dimensionToDelete + "'§a deleted.");
                } else {
                    player.sendMessage(ChatColor.RED + "Failed to delete dimension §6'" + dimensionToDelete + "'§c or it doesn't exist.");
                }
                return true;
            }

            case "invite", "uninvite", "deny" -> {
                if (!player.hasPermission("dimension." + subCommand)) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                if (!checkArgs.test(3, "/dimension " + subCommand + " <dimension-name> <player>")) return true;

                String dimension = args[1];
                Player target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    player.sendMessage("§cTarget player not found.");
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

                switch (subCommand) {
                    case "invite" -> {
                        TeleportManager.invite(player, dimension, target);
                        player.sendMessage("§aPlayer §e" + target.getName() + " §ahas been invited to §6" + dimension);
                    }
                    case "uninvite" -> {
                        TeleportManager.uninvite(player, dimension, target);
                        player.sendMessage("§aPlayer §e" + target.getName() + " §ahas been uninvited from §6" + dimension);
                    }
                    case "deny" -> {
                        TeleportManager.deny(player, dimension, target);
                        player.sendMessage("§aPlayer §e" + target.getName() + " §ahas been denied from §6" + dimension);
                    }
                }
                return true;
            }

            case "info" -> {
                if (!player.hasPermission("dimension.info")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }

                if (!checkArgs.test(2, "/dimension info <dimension-name>")) return true;

                String dimension = args[1];

                player.sendMessage("Info: ");
                player.sendMessage("Dimension: " + dimension);
                player.sendMessage("Owner: " + TeleportManager.getOwner(dimension));
                player.sendMessage("Dimension Type: " + teleportManager.getDimensionType(dimension));
                player.sendMessage("Private: " + teleportManager.isPrivate(dimension));
                return true;
            }

            case "setdelay" -> {
                if (!player.hasPermission("dimension.setdelay")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                if (!checkArgs.test(3, "/dimension setdelay <dimension-name> <seconds>")) return true;

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

            case "setprivate" -> {
                if (!player.hasPermission("dimension.setprivate")) {
                    player.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                if (!checkArgs.test(3, "/dimension setprivate <dimension-name> <true/false>")) return true;

                String dimension = args[1];
                String flag = args[2].toLowerCase();

                if (!TeleportManager.dimensionExists(dimension)) {
                    player.sendMessage(ChatColor.RED + "This world does not exist.");
                    return true;
                }

                UUID owner = TeleportManager.getOwner(dimension);
                if (owner == null) {
                    player.sendMessage(ChatColor.RED + "This world has no owner defined.");
                    return true;
                }

                boolean makePrivate;
                if (flag.equals("true")) makePrivate = true;
                else if (flag.equals("false")) makePrivate = false;
                else {
                    player.sendMessage(ChatColor.RED + "Please specify 'true' or 'false'.");
                    return true;
                }

                TeleportManager.setPrivate(player, dimension, makePrivate);
                player.sendMessage(ChatColor.GREEN + "World " + dimension + " is now set to " + (makePrivate ? "private." : "public."));
                return true;
            }

            default -> {
                sendUsage(player);
                return true;
            }
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage:");
        player.sendMessage(ChatColor.RED + "/dimension gui <player>");
        player.sendMessage(ChatColor.RED + "/dimension set <world>");
        player.sendMessage(ChatColor.RED + "/dimension create <world> <dimension-type>");
        player.sendMessage(ChatColor.RED + "/dimension delete <world>");
        player.sendMessage(ChatColor.RED + "/dimension invite <world> <player>");
        player.sendMessage(ChatColor.RED + "/dimension uninvite <world> <player>");
        player.sendMessage(ChatColor.RED + "/dimension deny <world> <player>");
        player.sendMessage(ChatColor.RED + "/dimension info <world>");
        player.sendMessage(ChatColor.RED + "/dimension setdelay <world> <seconds>");
        player.sendMessage(ChatColor.RED + "/dimension setprivate <world> <true/false>");
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("gui", "set", "create", "delete", "invite", "uninvite", "deny", "info", "setdelay", "setprivate"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "info", "setprivate", "uninvite", "deny", "invite", "delete", "setdelay", "set" ->
                        completions.addAll(TeleportManager.getAllDimensions());
                case "gui" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                }
            }
        } else if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "invite", "uninvite", "deny" -> {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        completions.add(p.getName());
                    }
                }
                case "create" -> completions.addAll(Arrays.asList("overworld", "nether", "end"));
                case "setprivate" -> completions.addAll(Arrays.asList("true", "false"));
            }
        }
        return completions;
    }
}