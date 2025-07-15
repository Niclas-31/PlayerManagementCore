package de.niclasl.multiPlugin.teleport.commands;

import de.niclasl.multiPlugin.teleport.manager.TeleportManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.ChatColor;

public class TeleportCommand implements CommandExecutor {

    private final TeleportManager teleportManager;

    public TeleportCommand(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by one player.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Please specify a dimension: overworld, nether, end");
            return false;
        }

        String dimension = args[0].toLowerCase();

        if(sender.hasPermission("admin.set.dimension.coords") == sender.hasPermission("teleport.player")){
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                // Format: /teleport-dimension set <dimension>
                dimension = args[1].toLowerCase();

                if (isValidDimension(dimension)) {
                    player.sendMessage(ChatColor.RED + "Invalid dimension. Use: overworld, nether, end");
                    return true;
                }

                Location loc = player.getLocation();
                teleportManager.setLocation(dimension, loc);
                player.sendMessage(ChatColor.GREEN + "Position for dimension " + dimension + " set:" +
                        "§c X=§6" + loc.getBlockX() + "§c Y=§" + loc.getBlockY() + "§c Z=§6" + loc.getBlockZ());
                return true;
            }
        }else {
            sender.sendMessage(ChatColor.RED + "You can't use this!");
        }

        if(!sender.hasPermission("admin.set.dimension.coords") == sender.hasPermission("teleport.player")){
            // Teleport-Befehl: /teleport-dimension <dimension>
            if (isValidDimension(dimension)) {
                player.sendMessage(ChatColor.RED + "Invalid dimension. Use: overworld, nether, end");
                return true;
            }

            Location targetLoc = teleportManager.getLocation(dimension);
            if (targetLoc == null) {
                player.sendMessage(ChatColor.RED + "No position has been set for this dimension yet.");
                return true;
            }

            player.teleport(targetLoc);
            player.sendMessage(ChatColor.GREEN + "You have been teleported to dimension §6" + dimension + ".");
            return true;
        }else if(sender.hasPermission("admin.set.dimension.coords") == sender.hasPermission("teleport.player")){
            // Teleport-Befehl: /teleport-dimension <dimension>
            if (isValidDimension(dimension)) {
                player.sendMessage(ChatColor.RED + "Invalid dimension. Use: overworld, nether, end");
                return true;
            }

            Location targetLoc = teleportManager.getLocation(dimension);
            if (targetLoc == null) {
                player.sendMessage(ChatColor.RED + "No position has been set for this dimension yet.");
                return true;
            }

            player.teleport(targetLoc);
            player.sendMessage(ChatColor.GREEN + "You have been teleported to dimension §6" + dimension + ".");
            return true;
        }
        return false;
    }

    private boolean isValidDimension(String dimension) {
        return !dimension.equals("overworld") && !dimension.equals("nether") && !dimension.equals("end");
    }
}