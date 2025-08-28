package de.niclasl.multiPlugin.warn_system.commands;

import de.niclasl.multiPlugin.warn_system.gui.WarnGui;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarnHistoryCommand implements CommandExecutor, TabCompleter {

    private static WarnGui warnGui;

    public WarnHistoryCommand(WarnGui warnGui) {
        WarnHistoryCommand.warnGui = warnGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("multiplugin.warn.history")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /warn-history <player> [page]");
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
        int maxPage = warnGui.getTotalPages(target);
        if (page > maxPage) {
            player.sendMessage("§cThis player has only " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " of history.");
            return true;
        }

        warnGui.open(player, target, page); // GUI öffnen, statische Methode

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // Nur Spieler mit Permission
        if (!sender.hasPermission("multiplugin.warn.history")) return completions;

        // Vorschläge für den Spieler-Namen (erstes Argument)
        if (args.length == 1) {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                String name = player.getName();
                if (name != null && name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(name);
                }
            }
        }

        // Optional: Vorschläge für die Seite (zweites Argument)
        else if (args.length == 2) {
            completions.add("1");
            completions.add("2");
            completions.add("3");
            // Du könntest hier auch dynamisch die maximale Seitenzahl aus der WarnGui ziehen
        }

        return completions;
    }
}