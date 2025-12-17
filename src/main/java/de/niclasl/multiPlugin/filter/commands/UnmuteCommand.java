package de.niclasl.multiPlugin.filter.commands;

import de.niclasl.multiPlugin.filter.manager.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnmuteCommand implements CommandExecutor, TabCompleter {

    private static MuteManager muteManager;

    public UnmuteCommand(MuteManager muteManager) {
        UnmuteCommand.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {

        if (!sender.hasPermission("multiplugin.unmute")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("multiplugin.unmute")) return completions;

        if (args.length == 1) {
            for (UUID mutedUUID : muteManager.getMutedPlayers()) {
                OfflinePlayer mutedPlayer = Bukkit.getOfflinePlayer(mutedUUID);
                String name = mutedPlayer.getName();
                if (name != null && name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(name);
                }
            }
        }

        return completions;
    }
}