package de.niclasl.multiPlugin.warn_system.commands;

import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import de.niclasl.multiPlugin.warn_system.model.Warning;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WarnCommand implements CommandExecutor {

    private final WarnManager warnManager;
    private final BanHistoryManager banManager;

    public WarnCommand(WarnManager warnManager, BanHistoryManager banManager) {
        this.warnManager = warnManager;
        this.banManager = banManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /warn <player> <reason>");
            return true;
        }

        if (target.isOp()) {
            sender.sendMessage(ChatColor.RED + "You cannot warn an admin!");
            return true;
        }

        if (target.getName() == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        String reason = String.join(" ", args).substring(args[0].length()).trim();
        String by = sender.getName();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        // Verwarnung hinzufügen (automatisch 3 Punkte)
        warnManager.addWarning(uuid, reason, by, date);
        sender.sendMessage("§a" + target.getName() + " was warned (§7Reason: §e" + reason + "§a).");

        // Punkte prüfen
        int totalPoints = warnManager.getTotalPoints(uuid);
        if (totalPoints >= 10) {
            // Bann auslösen (für 1 Tag)
            String banReason = "Too many warnings";
            String durationArg = "1d";
            LocalDateTime unbanTime = LocalDateTime.now().plusDays(1);
            Date banUntil = Date.from(unbanTime.atZone(ZoneId.systemDefault()).toInstant());

            // In Minecraft Banliste eintragen
            Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), banReason, banUntil, "System");

            // Kick ausführen
            if (target.isOnline() && target instanceof Player onlinePlayer) {
                String kickMessage = "§cYou have been banned!\n"
                        + "§7Reason: §e" + banReason + "\n"
                        + "§7Duration: §c" + durationArg;
                onlinePlayer.kickPlayer(kickMessage);
            }

            // Eigenes Ban-System aktualisieren
            banManager.addBan(uuid, banReason, "System", durationArg, null, null);

            // Punkte auf 0 setzen nach dem Bann
            List<de.niclasl.multiPlugin.warn_system.model.Warning> updated = warnManager.getWarnings(uuid);
            for (Warning w : updated) {
                w.setPoints(0);
            }
            warnManager.saveWarnings(uuid, updated);
        }

        return true;
    }
}