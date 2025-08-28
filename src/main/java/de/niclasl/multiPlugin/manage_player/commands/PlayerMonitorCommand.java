package de.niclasl.multiPlugin.manage_player.commands;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerMonitorCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)){
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        if (!sender.hasPermission("multiplugin.manage")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /manage <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null){
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        WatchGuiManager.openPage1(player, target);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());
        }

        return completions;
    }
}
