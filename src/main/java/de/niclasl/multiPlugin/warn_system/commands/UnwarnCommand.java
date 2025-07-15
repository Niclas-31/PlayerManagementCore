package de.niclasl.multiPlugin.warn_system.commands;

import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import de.niclasl.multiPlugin.warn_system.model.Warning;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class UnwarnCommand implements CommandExecutor {

    private final WarnManager warnManager;

    public UnwarnCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /unwarn <player> <warn-id>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = target.getUniqueId();
        String warnId = args[1];

        List<Warning> warnings = warnManager.getWarnings(uuid);
        boolean removed = warnings.removeIf(warn -> warn.getId().equalsIgnoreCase(warnId));

        if (removed) {
            warnManager.saveWarnings(uuid, warnings);
            sender.sendMessage("§aWarning §e" + warnId + " §a was removed for §e" + target.getName() + "§a.");
        } else {
            sender.sendMessage("§cNo warning found with this ID.");
        }

        return true;
    }
}