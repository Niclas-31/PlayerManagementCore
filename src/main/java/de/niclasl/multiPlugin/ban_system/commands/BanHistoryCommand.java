package de.niclasl.multiPlugin.ban_system.commands;

import de.niclasl.multiPlugin.ban_system.gui.BanHistoryGui;
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

public class BanHistoryCommand implements CommandExecutor, TabCompleter {

    private static BanHistoryGui banHistoryGui;

    public BanHistoryCommand(BanHistoryGui banHistoryGui) {
        BanHistoryCommand.banHistoryGui = banHistoryGui;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.banhistory")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUsage: /banhistory <player> [page]");
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

        int maxPage = banHistoryGui.getTotalPages(target);
        if (page > maxPage) {
            player.sendMessage("§cThis player has only " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " of history.");
            return true;
        }

        BanHistoryGui.open(player, target, page);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("multiplugin.banhistory")) return completions;

        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.hasPlayedBefore() || target.isOnline()) {
                int maxPage = banHistoryGui.getTotalPages(target);
                for (int i = 1; i <= maxPage; i++) {
                    String pageStr = String.valueOf(i);
                    if (pageStr.startsWith(args[1])) {
                        completions.add(pageStr);
                    }
                }
            }
        }

        return completions;
    }
}