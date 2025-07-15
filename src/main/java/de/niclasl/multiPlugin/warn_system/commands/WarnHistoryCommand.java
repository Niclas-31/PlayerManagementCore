package de.niclasl.multiPlugin.warn_system.commands;

import de.niclasl.multiPlugin.warn_system.gui.WarnGui;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarnHistoryCommand implements CommandExecutor {

    private final WarnGui warnGui;

    public WarnHistoryCommand(WarnGui warnGui) {
        this.warnGui = warnGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cUsage: /warn-history <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getName() == null) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }

        warnGui.open(player, target, 1); // GUI öffnen, statische Methode

        return true;
    }
}