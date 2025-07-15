package de.niclasl.multiPlugin.filter.commands;

import de.niclasl.multiPlugin.filter.manager.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UnmuteCommand implements CommandExecutor {

    private final MuteManager muteManager;

    public UnmuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /unmute <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target.getName() == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        if (!muteManager.isMuted(target.getUniqueId())) {
            sender.sendMessage("§e" + target.getName() + " is not muted.");
            return true;
        }

        muteManager.unmutePlayer(target.getUniqueId());
        sender.sendMessage("§a" + target.getName() + " was discouraged.");
        return true;
    }
}
