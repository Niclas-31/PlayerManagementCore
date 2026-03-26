package de.niclasl.playerManagementCore.spawn_protection.command;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpawnProtectionCommand implements TabExecutor {

    private final PlayerManagementCore plugin;

    public SpawnProtectionCommand(PlayerManagementCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if (!sender.hasPermission("protection")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§aTeamWar - /protection <enable/disable/set>");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "set" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /protection set <block|damage> <value>");
                    return true;
                }

                String type = args[1].toLowerCase();
                String valueStr = args[2];

                int value;
                try {
                    value = Integer.parseInt(valueStr);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cValue must be a number!");
                    return true;
                }

                switch (type) {
                    case "block" -> {
                        plugin.getSpawnProtectionConfigManager().setBlockProtectionRadius(value);
                        plugin.getSpawnProtectionConfigManager().save();
                        sender.sendMessage("§aBlock protection radius set to §e" + value);
                    }
                    case "damage" -> {
                        plugin.getSpawnProtectionConfigManager().setDamageProtectionRadius(value);
                        plugin.getSpawnProtectionConfigManager().save();
                        sender.sendMessage("§aDamage protection radius set to §e" + value);
                    }
                    default -> sender.sendMessage("§cUnknown protection type. Use block or damage.");
                }
            }
            case "enable" -> {
                plugin.getSpawnProtectionConfigManager().setProtectionEnabled(true);
                plugin.getSpawnProtectionConfigManager().save();
                sender.sendMessage("§aSpawn Protection enabled.");
            }
            case "disable" -> {
                plugin.getSpawnProtectionConfigManager().setProtectionEnabled(false);
                plugin.getSpawnProtectionConfigManager().save();
                sender.sendMessage("§cSpawn Protection disabled.");
            }
            default -> sender.sendMessage("§cUnknown subcommand.");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("set".startsWith(args[0].toLowerCase())) completions.add("set");
            if ("enable".startsWith(args[0].toLowerCase())) completions.add("enable");
            if ("disable".startsWith(args[0].toLowerCase())) completions.add("disable");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if ("block".startsWith(args[1].toLowerCase())) completions.add("block");
            if ("damage".startsWith(args[1].toLowerCase())) completions.add("damage");
        }
        return completions;
    }
}