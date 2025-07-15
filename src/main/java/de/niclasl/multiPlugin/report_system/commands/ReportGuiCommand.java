package de.niclasl.multiPlugin.report_system.commands;

import de.niclasl.multiPlugin.report_system.gui.ReportGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportGuiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können das Report-GUI öffnen.");
            return true;
        }

        if (!player.hasPermission("report.gui")) {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung dafür.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Benutzung: /report-gui <Spieler> [Seite]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            player.sendMessage(ChatColor.RED + "Spieler wurde nie gesehen oder existiert nicht.");
            return true;
        }

        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Ungültige Seitenzahl.");
                return true;
            }
        }

        ReportGui.open(player, target, page);
        return true;
    }
}
