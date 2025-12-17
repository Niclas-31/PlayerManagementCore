package de.niclasl.multiPlugin.ban_system.commands;

import de.niclasl.multiPlugin.audit.AuditManager;
import de.niclasl.multiPlugin.audit.model.AuditAction;
import de.niclasl.multiPlugin.audit.model.AuditType;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
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
import java.util.Set;
import java.util.UUID;

public class UnbanCommand implements CommandExecutor, TabCompleter {

    private static BanHistoryManager banHistoryManager;

    public UnbanCommand(BanHistoryManager banHistoryManager) {
        UnbanCommand.banHistoryManager = banHistoryManager;
    }
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.unban")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage("§cUsage: /unban <player>");
            return true;
        }
        String name = args[0];

        BanList<?> banList = Bukkit.getBanList(BanList.Type.NAME);
        BanEntry<?> entry = banList.getBanEntry(name);

        if (entry == null) {
            sender.sendMessage("§e" + name + " is not banned.");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
        UUID targetUUID = target.getUniqueId();

        banList.pardon(name);

        sender.sendMessage("§a" + name + " was successfully unbanned.");
        banHistoryManager.updateLastBanWithUnban(targetUUID, sender.getName());
        AuditManager.log(
                target,
                AuditType.BAN,
                AuditAction.REMOVE,
                player,
                "Unbanned by staff"
        );
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("multiplugin.unban")) {
            return completions;
        }

        if (args.length == 1) {
            BanList<?> banList = Bukkit.getBanList(BanList.Type.NAME);
            Set<? extends BanEntry<?>> entries = banList.getEntries();

            for (BanEntry<?> entry : entries) {
                String bannedName = entry.getTarget();
                if (bannedName.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(bannedName);
                }
            }
        }

        return completions;
    }
}