package de.niclasl.multiPlugin.ban_system.commands;

import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import de.niclasl.multiPlugin.ban_system.manager.ReasonManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanCommand implements CommandExecutor {

    private final ReasonManager reasonManager;
    private final BanHistoryManager banHistoryManager;

    public BanCommand(ReasonManager reasonManager, BanHistoryManager banHistoryManager) {
        this.reasonManager = reasonManager;
        this.banHistoryManager = banHistoryManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /ban <player> <duration> <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getName() == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        if (target.isOp()) {
            sender.sendMessage("§cYou cannot ban an admin.");
            return true;
        }

        String durationArg = args[1].toLowerCase();
        Date banUntil = null;

        if (!durationArg.equalsIgnoreCase("perm")) {
            try {
                Duration duration = parseDuration(durationArg);
                LocalDateTime unbanTime = LocalDateTime.now().plus(duration);
                banUntil = Date.from(unbanTime.atZone(ZoneId.systemDefault()).toInstant());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid time format. Use e.g.: 1d, 2h30m, 1w2d.");
                return true;
            }
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (!reasonManager.isValidReason(reason)) {
            sender.sendMessage("§cInvalid reason. Valid reasons:");
            for (String valid : reasonManager.getBanReasons()) {
                sender.sendMessage("§8- §e" + valid);
            }
            return true;
        }

        // Ban setzen (Minecraft intern)
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, banUntil, sender.getName());

        // Nachricht an Sender
        sender.sendMessage("§a" + target.getName() + " was banned for: §e" + reason +
                (banUntil != null ? " §7(until: " + banUntil + ")" : " §7(permanently)"));

        // Spieler kicken, wenn online
        Player onlineTarget = Bukkit.getPlayer(target.getUniqueId());
        if (onlineTarget != null && onlineTarget.isOnline()) {
            String kickMessage = "§cYou have been banned!\n"
                    + "§7Reason: §e" + reason + "\n"
                    + "§7Duration: §c" + (durationArg.equalsIgnoreCase("perm") ? "permanent" : durationArg);
            onlineTarget.kickPlayer(kickMessage);
        }

        // In eigene Ban-History eintragen
        banHistoryManager.addBan(target.getUniqueId(), reason, sender.getName(), durationArg, null, null);
        return true;
    }

    /**
     * Parsed Strings wie 1h30m oder 2d in ein Duration-Objekt
     */
    private Duration parseDuration(String input) {
        Pattern pattern = Pattern.compile("(\\d+)([smhdwMy])");
        Matcher matcher = pattern.matcher(input);

        Duration total = Duration.ZERO;
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            switch (matcher.group(2)) {
                case "s" -> total = total.plusSeconds(value);
                case "m" -> total = total.plusMinutes(value);
                case "h" -> total = total.plusHours(value);
                case "d" -> total = total.plusDays(value);
                case "w" -> total = total.plusDays(value * 7L);
                case "M" -> total = total.plusDays(value * 30L);
                case "y" -> total = total.plusDays(value * 365L);
                default -> throw new IllegalArgumentException("Unknown duration unit.");
            }
        }

        if (total.isZero()) {
            throw new IllegalArgumentException("Invalid or empty time input.");
        }

        return total;
    }
}