package de.niclasl.multiPlugin.audit.command;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.audit.model.AuditEntry;
import de.niclasl.multiPlugin.audit.storage.YamlAuditStorage;
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

public record AuditCommand(MultiPlugin plugin) implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {

        if (!sender.hasPermission("multiplugin.audit")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage("§c/audit <player> [page]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            p.sendMessage("§cPlayer not found.");
            return true;
        }

        int page = 1;

        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) {
                    p.sendMessage("§cPage number must be at least 1.");
                    return true;
                }
            } catch (NumberFormatException e) {
                p.sendMessage("§cInvalid page number.");
                return true;
            }
        }

        int maxPage = plugin.getAuditGui().getTotalPages(target);
        if (page > maxPage) {
            p.sendMessage("§cThis player has only " + maxPage + " page" + (maxPage == 1 ? "" : "s") + " of history.");
            return true;
        }

        plugin.getAuditGui().open(p, target, page);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("multiplugin.audit")) return completions;

        if (args.length == 1) {
            for (OfflinePlayer target : Bukkit.getOfflinePlayers()) {
                String name = target.getName();
                if (name != null && name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(name);
                }
            }
        }

        if (args.length == 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.hasPlayedBefore()) {
                List<AuditEntry> entries = YamlAuditStorage.getEntries(target.getUniqueId());
                int entriesPerPage = GuiConstants.ALLOWED_SLOTS.length;
                int totalPages = (int) Math.ceil(entries.size() / (double) entriesPerPage);
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