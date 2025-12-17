package de.niclasl.multiPlugin.teleport.commands;

import de.niclasl.multiPlugin.teleport.manager.TeleportManager;
import org.bukkit.ChatColor;
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

public class DimensionCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length != 3 || !args[0].equalsIgnoreCase("setprivate")) {
            player.sendMessage(ChatColor.RED + "Usage: /dimension setprivate <world_name> <true/false>");
            return true;
        }

        String dimension = args[1];
        String flag = args[2].toLowerCase();

        if (!TeleportManager.dimensionExists(dimension)) {
            player.sendMessage(ChatColor.RED + "This world does not exist.");
            return true;
        }

        // 2. Owner prüfen
        UUID owner = TeleportManager.getOwner(dimension);
        if (owner == null) {
            player.sendMessage(ChatColor.RED + "This world has no owner defined.");
            return true;
        }

        if (!owner.equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not the owner of this world!");
            return true;
        }

        boolean makePrivate;
        if (flag.equals("true") || flag.equals("yes")) {
            makePrivate = true;
        } else if (flag.equals("false") || flag.equals("no")) {
            makePrivate = false;
        } else {
            player.sendMessage(ChatColor.RED + "Please specify 'true' or 'false'.");
            return true;
        }

        TeleportManager.setPrivate(player, dimension, makePrivate);
        player.sendMessage(ChatColor.GREEN + "World " + dimension + " is now set to " + (makePrivate ? "private." : "public."));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("setprivate");
        } else if (args.length == 2) {
            completions.addAll(TeleportManager.getAllDimensions());
        } else if (args.length == 3) {
            List<String> options = Arrays.asList("true", "false");
            for (String option : options) {
                if (option.startsWith(args[2].toLowerCase())) completions.add(option);
            }
        }
        return completions;
    }
}