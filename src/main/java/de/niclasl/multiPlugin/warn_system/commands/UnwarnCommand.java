package de.niclasl.multiPlugin.warn_system.commands;

import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import de.niclasl.multiPlugin.warn_system.model.Warning;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnwarnCommand implements CommandExecutor, TabCompleter {

    private static WarnManager warnManager;

    public UnwarnCommand(WarnManager warnManager) {
        UnwarnCommand.warnManager = warnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("multiplugin.unwarn")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /unwarn <player> <warnId>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getName() == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        String warnId = args[1];

        List<Warning> warnings = warnManager.getWarnings(uuid);
        if (warnings == null || warnings.isEmpty()) {
            sender.sendMessage("§cThis player has no warnings.");
            return true;
        }

        boolean removed = warnings.removeIf(warn -> warn.getId().equalsIgnoreCase(warnId));

        if (removed) {
            warnManager.saveWarnings(uuid, warnings);
            sender.sendMessage("§aWarning §e" + warnId + "§a for §e" + target.getName() + "§a was removed.");
        } else {
            sender.sendMessage("§cNo warning found with ID §e" + warnId + "§c.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("multiplugin.unwarn")) return completions;

        if (args.length == 1) {
            // Vorschläge für Spielernamen
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (p.getName() != null && p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 2) {
            // Vorschläge für Warn-IDs
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.getName() != null) {
                UUID uuid = target.getUniqueId();
                List<Warning> warnings = warnManager.getWarnings(uuid);
                if (warnings != null) {
                    for (Warning w : warnings) {
                        if (w.getId().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(w.getId());
                        }
                    }
                }
            }
        }

        return completions;
    }
}