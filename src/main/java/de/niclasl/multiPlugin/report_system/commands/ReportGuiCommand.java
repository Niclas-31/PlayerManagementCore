package de.niclasl.multiPlugin.report_system.commands;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.report_system.gui.ReportGui;
import de.niclasl.multiPlugin.report_system.manager.ReportManager;
import de.niclasl.multiPlugin.report_system.model.Report;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReportGuiCommand implements CommandExecutor, TabCompleter {

    private static ReportGui reportGui;

    public ReportGuiCommand(ReportGui reportGui) {
        ReportGuiCommand.reportGui = reportGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.report.history")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        // Argument-Prüfung
        if (args.length < 1) {
            player.sendMessage("§cUsage: /report-history <player> [page]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }

        int page = 1;

        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) {
                    player.sendMessage("§cPage number must be at least 1.");
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid page number.");
                return true;
            }
        }

        // Seitenanzahl prüfen
        int maxPage = reportGui.getTotalPages(target);
        if (page > maxPage) {
            player.sendMessage("§cThis player has only " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " of history.");
            return true;
        }

        reportGui.open(player, target, page);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // 1. Argument: Spielernamen
        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                String name = p.getName();
                if (name != null && name.toLowerCase().startsWith(partial)) {
                    completions.add(name);
                }
            }
        }

        // 2. Argument: Seitenzahlen
        if (args.length == 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.getName() != null && target.hasPlayedBefore()) {
                List<Report> warnings = ReportManager.getReports(target.getUniqueId());
                int warningsPerPage = GuiConstants.ALLOWED_SLOTS.length;
                int totalPages = (int) Math.ceil(warnings.size() / (double) warningsPerPage);
                if (totalPages < 1) totalPages = 1;

                String partialPage = args[1].toLowerCase();
                for (int i = 1; i <= totalPages; i++) {
                    String s = String.valueOf(i);
                    if (partialPage.isEmpty() || s.startsWith(partialPage)) {
                        completions.add(s);
                    }
                }
            }
        }

        return completions;
    }
}