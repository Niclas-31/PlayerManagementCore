package de.niclasl.multiPlugin.portal.commands;

import de.niclasl.multiPlugin.portal.PortalType;
import de.niclasl.multiPlugin.portal.gui.PortalGui;
import de.niclasl.multiPlugin.portal.manager.PortalConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PortalCommand implements CommandExecutor, TabCompleter {

    public PortalCommand() {}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("multiplugin.portal.manage")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/portal reload");
            sender.sendMessage(ChatColor.YELLOW + "/portal toggle <type>");
            sender.sendMessage(ChatColor.YELLOW + "/portal whitelist add <plugin>");
            sender.sendMessage(ChatColor.YELLOW + "/portal whitelist remove <plugin>");
            sender.sendMessage(ChatColor.YELLOW + "/portal whitelist list");
            sender.sendMessage(ChatColor.YELLOW + "/portal gui");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload" -> {
                PortalConfigManager.reload();
                sender.sendMessage(ChatColor.GREEN + "Portal config reloaded.");
            }
            case "toggle" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /portal toggle <type>");
                    return true;
                }
                try {
                    PortalType type = PortalType.valueOf(args[1].toUpperCase());
                    boolean enabled = PortalConfigManager.isPortalEnabled(type);
                    PortalConfigManager.setPortalEnabled(type, !enabled);
                    sender.sendMessage(ChatColor.GREEN + type.name() + " set to " + !enabled);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Unknown PortalType. Use one of: " +
                            Arrays.stream(PortalType.values()).map(Enum::name).collect(Collectors.joining(", ")));
                }
            }
            case "whitelist" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /portal whitelist <add|remove|list> [plugin]");
                    return true;
                }
                String op = args[1].toLowerCase();
                switch (op) {
                    case "add" -> {
                        if (args.length < 3) {
                            sender.sendMessage(ChatColor.RED + "Usage: /portal whitelist add <pluginName>");
                            return true;
                        }
                        String pluginName = args[2];
                        PortalConfigManager.addToWhitelist(pluginName);
                        sender.sendMessage(ChatColor.GREEN + "Added " + pluginName + " to portal whitelist.");
                    }
                    case "remove" -> {
                        if (args.length < 3) {
                            sender.sendMessage(ChatColor.RED + "Usage: /portal whitelist remove <pluginName>");
                            return true;
                        }
                        String pluginName = args[2];
                        PortalConfigManager.removeFromWhitelist(pluginName);
                        sender.sendMessage(ChatColor.GREEN + "Removed " + pluginName + " from portal whitelist.");
                    }
                    case "list" -> {
                        List<String> wl = PortalConfigManager.getWhitelist();
                        sender.sendMessage(ChatColor.YELLOW + "Portal Whitelist: " + String.join(", ", wl));
                    }
                    default -> sender.sendMessage(ChatColor.RED + "Unknown whitelist command. Use add/remove/list.");
                }
            }
            case "gui" -> {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage("Only players can use the GUI.");
                    return true;
                }
                PortalGui.openFor(p);
            }
            default -> sender.sendMessage(ChatColor.RED + "Unknown subcommand.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("multiplugin.portal.manage")) return List.of();

        if (args.length == 1) {
            return Stream.of("reload", "toggle", "whitelist", "gui")
                    .filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            return Arrays.stream(PortalType.values()).map(Enum::name)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            return Stream.of("add", "remove", "list")
                    .filter(s -> s.startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("whitelist")) {
            return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                    .map(Plugin::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}