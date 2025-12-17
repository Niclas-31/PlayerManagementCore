package de.niclasl.multiPlugin.warn_system.commands;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.audit.AuditManager;
import de.niclasl.multiPlugin.audit.model.AuditAction;
import de.niclasl.multiPlugin.audit.model.AuditType;
import de.niclasl.multiPlugin.warn_system.manage.WarnActionConfigManager;
import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import de.niclasl.multiPlugin.warn_system.model.Warning;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WarnCommand implements CommandExecutor, TabCompleter {

    private static WarnManager warnManager;
    private static BanHistoryManager banManager;
    private static MultiPlugin plugin;

    public WarnCommand(WarnManager warnManager, BanHistoryManager banManager, MultiPlugin plugin) {
        WarnCommand.warnManager = warnManager;
        WarnCommand.banManager = banManager;
        WarnCommand.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.warn")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /warn <player> <reason>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        if (target.isOp()) {
            sender.sendMessage("§cYou cannot warn an admin!");
            return true;
        }

        UUID uuid = target.getUniqueId();
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String by = sender.getName();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        warnManager.addWarning(uuid, reason, by, date);
        sender.sendMessage("§a" + target.getName() + " was warned (§7Reason: §e" + reason + "§a).");

        WarnActionConfigManager warnConfig = new WarnActionConfigManager(plugin);
        int totalPoints = warnManager.getTotalPoints(uuid);

        AuditManager.log(
                target,
                AuditType.WARN,
                AuditAction.ADD,
                player,
                reason
        );

        List<String> thresholds = new ArrayList<>(warnConfig.getThresholds());
        thresholds.sort((a, b) -> Integer.compare(Integer.parseInt(b), Integer.parseInt(a)));

        for (String thresholdStr : thresholds) {
            int threshold = Integer.parseInt(thresholdStr);
            if (totalPoints >= threshold) {
                ConfigurationSection section = warnConfig.getThresholdSection(thresholdStr);
                String action = section.getString("action");
                String durationArg = section.getString("duration", null);
                boolean resetPoints = section.getBoolean("resetPoints", false);

                switch (Objects.requireNonNull(action).toLowerCase()) {
                    case "kick" -> {
                        if (target.isOnline() && target instanceof Player onlinePlayer) {
                            onlinePlayer.kickPlayer("§cYou have been kicked!\n§7Reason: §e" + reason);
                        }
                    }
                    case "ban" -> {
                        LocalDateTime unbanTime = parseDuration(durationArg);
                        Date banUntil = Date.from(unbanTime.atZone(ZoneId.systemDefault()).toInstant());

                        Bukkit.getBanList(BanList.Type.NAME).addBan(Objects.requireNonNull(target.getName()), reason, banUntil, "System");

                        if (target.isOnline() && target instanceof Player onlinePlayer) {
                            onlinePlayer.kickPlayer("§cYou have been banned!\n§7Reason: §e" + reason + "\n§7Duration: §c" + durationArg);
                        }

                        banManager.addBan(uuid, reason, "System", durationArg, null, null);
                    }
                }

                if (resetPoints) {
                    List<Warning> updated = WarnManager.getWarnings(uuid);
                    for (Warning w : updated) {
                        w.setPoints(0);
                    }
                    warnManager.saveWarnings(uuid, updated);
                }

                break;
            }
        }

        return true;
    }

    public LocalDateTime parseDuration(String durationArg) {
        if (durationArg == null) return LocalDateTime.now().plusYears(100);
        int amount = Integer.parseInt(durationArg.substring(0, durationArg.length() - 1));
        char unit = durationArg.charAt(durationArg.length() - 1);
        return switch (unit) {
            case 'd' -> LocalDateTime.now().plusDays(amount);
            case 'h' -> LocalDateTime.now().plusHours(amount);
            case 'm' -> LocalDateTime.now().plusMinutes(amount);
            default -> LocalDateTime.now();
        };
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (p.getName() != null && p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }
        return completions;
    }
}