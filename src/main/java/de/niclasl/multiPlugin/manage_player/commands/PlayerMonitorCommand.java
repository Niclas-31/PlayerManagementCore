package de.niclasl.multiPlugin.manage_player.commands;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerMonitorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)){
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        if(args.length != 1){
            player.sendMessage(ChatColor.RED + "Use: /manage <player>");
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
}
