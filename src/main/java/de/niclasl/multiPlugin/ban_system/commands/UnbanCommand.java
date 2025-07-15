package de.niclasl.multiPlugin.ban_system.commands;

import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnbanCommand implements CommandExecutor {

    private final BanHistoryManager banHistoryManager;

    public UnbanCommand(BanHistoryManager banHistoryManager) {
        this.banHistoryManager = banHistoryManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != 1) {
            sender.sendMessage("§cUse: /unban <player>");
            return true;
        }

        String name = args[0];
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);

        BanEntry entry = banList.getBanEntry(name);
        if (entry == null) {
            sender.sendMessage("§e" + name + " is not banned.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        UUID targetUUID = target.getUniqueId();

        // Spieler entbannen
        banList.pardon(name);
        sender.sendMessage("§a" + name + " was successfully unbanned.");

        // Ban-Historie aktualisieren (wer entbannt hat und wann)
        banHistoryManager.updateLastBanWithUnban(targetUUID, sender.getName());

        return true;
    }
}