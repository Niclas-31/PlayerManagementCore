package de.niclasl.multiPlugin.filter.commands;

import de.niclasl.multiPlugin.filter.manager.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MuteCommand implements CommandExecutor {

    private final MuteManager muteManager;

    public MuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("admin.mute")){
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /mute <player> <duration> <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getName() == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        String durationArg = args[1].toLowerCase();

        long durationMillis;
        if (durationArg.equalsIgnoreCase("perm")) {
            durationMillis = 0L;
        } else {
            try {
                durationMillis = parseDurationToMillis(durationArg);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid duration! Example: 10m, 2h30m, 1d");
                return true;
            }
        }

        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));

        muteManager.mutePlayer(target.getUniqueId(), durationMillis);

        sender.sendMessage("§a" + target.getName() + " was muted for " + durationArg + ". Reason: " + reason);

        // Falls der Spieler online ist, benachrichtige ihn
        if (target.isOnline()) {
            Player onlinePlayer = target.getPlayer();
            if (onlinePlayer != null) {
                onlinePlayer.sendMessage("§cYou were muted for " + durationArg + ". Reason: " + reason);
            }
        }
        return true;
    }

    private long parseDurationToMillis(String input) {
        Pattern pattern = Pattern.compile("(\\d+)([smhdwMy])");
        Matcher matcher = pattern.matcher(input);

        long totalMillis = 0;
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            switch (matcher.group(2)) {
                case "s" -> totalMillis += value * 1000L;
                case "m" -> totalMillis += value * 60_000L;
                case "h" -> totalMillis += value * 60 * 60_000L;
                case "d" -> totalMillis += value * 24 * 60 * 60_000L;
                case "w" -> totalMillis += value * 7 * 24 * 60 * 60_000L;
                case "M" -> totalMillis += value * 30 * 24 * 60 * 60_000L;
                case "y" -> totalMillis += value * 365 * 24 * 60 * 60_000L;
                default -> throw new IllegalArgumentException("Unknown entity");
            }
        }
        if (totalMillis == 0) throw new IllegalArgumentException("No valid time detected");
        return totalMillis;
    }
}
