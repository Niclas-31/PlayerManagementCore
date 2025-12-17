package de.niclasl.multiPlugin.warn_system.commands;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.warn_system.gui.WarnGui;
import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import de.niclasl.multiPlugin.warn_system.model.Warning;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class WarnHistoryCommand implements CommandExecutor, TabCompleter {

    private static WarnGui warnGui;

    public WarnHistoryCommand(WarnGui warnGui) {
        WarnHistoryCommand.warnGui = warnGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        if (!sender.hasPermission("multiplugin.warnhistory")) {
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

        int maxPage = warnGui.getTotalPages(target);
        if (page > maxPage) {
            player.sendMessage("§cThis player has only " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " of history.");
            return true;
        }

        warnGui.open(player, target, page);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NonNull Command command, @NonNull String alias, String @NonNull [] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("multiplugin.warnhistory")) return completions;

        if (args.length == 1) {
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                String name = player.getName();
                if (name != null && name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(name);
                }
            }
        }

        if (args.length == 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.hasPlayedBefore()) {
                List<Warning> warnings = WarnManager.getWarnings(target.getUniqueId());
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