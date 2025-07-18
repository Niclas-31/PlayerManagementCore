package de.niclasl.multiPlugin.report_system.commands;

import de.niclasl.multiPlugin.report_system.manager.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class ReportCommand implements CommandExecutor {

    private final ReportManager reportManager;

    public ReportCommand(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can create reports.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /report <player> <reason>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found or offline.");
            return true;
        }

        if (target == player) {
            player.sendMessage(ChatColor.RED + "You cannot report yourself.");
            return true;
        }

        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String time = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
        Location location = player.getLocation();

        // Report speichern
        reportManager.addReport(target.getUniqueId(), reason, sender.getName(), time, "OPEN", false);

        // Feedback an den Spieler
        player.sendMessage(ChatColor.GREEN + "Your report against §e" + target.getName() + " §ahas been sent.");

        // Mods benachrichtigen
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("report.notify"))
                .forEach(p -> p.sendMessage(ChatColor.RED + "[Report] "
                        + ChatColor.YELLOW + player.getName()
                        + " has " + target.getName()
                        + " reported: " + ChatColor.GRAY + reason));

        return true;
    }
}