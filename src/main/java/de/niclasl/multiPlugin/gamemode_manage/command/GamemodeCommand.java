package de.niclasl.multiPlugin.gamemode_manage.command;

import de.niclasl.multiPlugin.gamemode_manage.gui.GamemodeGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Nur Spieler dürfen das GUI öffnen
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        // Nur Admins
        if (!player.hasPermission("multiplugin.gamemode.gui")) {
            player.sendMessage("§cYou do not have permission to access this GUI.");
            return true;
        }

        // Zielspieler optional (z. B. /gamemode-gui [Name])
        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage("§cPlayer '" + args[0] + "' was not found.");
                return true;
            }
        } else {
            target = player;
        }

        // GUI öffnen
        GamemodeGui.open(player, target);
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
