package de.niclasl.multiPlugin.portal.commands;

import de.niclasl.multiPlugin.portal.manager.PortalManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PortalCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("multiplugin.portal.toggle")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§eUsage: /portal <type> <on|off>");
            return true;
        }

        String typeArg = args[0].toUpperCase();
        String action = args[1].toLowerCase();

        boolean enable = action.equals("on");

        PortalManager.PortalType type;
        try {
            type = PortalManager.PortalType.valueOf(typeArg);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cUnknown portal type! Available types:");
            for (PortalManager.PortalType t : PortalManager.PortalType.values()) {
                sender.sendMessage("§e- " + t.name());
            }
            return true;
        }

        // PortalManager aktualisieren
        PortalManager.setPortalEnabled(type, enable);
        sender.sendMessage("§e" + type.name() + " portals are now: " + (enable ? "§aON" : "§cOFF"));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (PortalManager.PortalType type : PortalManager.PortalType.values()) {
                if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(type.name());
                }
            }
        } else if (args.length == 2) {
            List<String> options = Arrays.asList("on", "off");
            for (String option : options) {
                if (option.startsWith(args[1].toLowerCase())) {
                    completions.add(option);
                }
            }
        }

        return completions;
    }
}