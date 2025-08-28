package de.niclasl.multiPlugin.permission.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public class PermissionCommand implements CommandExecutor, TabCompleter {

    // Speichert temporäre Attachments pro Spieler
    private final Map<Player, PermissionAttachment> attachments = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("multiplugin.permission.manage")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§eUsage:");
            sender.sendMessage("§e/permission list <player>");
            sender.sendMessage("§e/permission remove <player> <permission>");
            sender.sendMessage("§e/permission set <player> <permission>");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "list" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /permission list <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found!");
                    return true;
                }
                sender.sendMessage("§ePermissions for " + target.getName() + ":");
                target.getEffectivePermissions().forEach(p ->
                        sender.sendMessage("§7- " + p.getPermission() + " = " + p.getValue())
                );
            }
            case "remove" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /permission remove <player> <permission>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage("§cPlayer not found or not online!");
                    return true;
                }

                String perm = args[2];
                PermissionAttachment attachment = attachments.get(target);
                if (attachment == null) {
                    attachment = target.addAttachment(Bukkit.getPluginManager().getPlugin("Multi-Plugin"));
                    attachments.put(target, attachment);
                }

                attachment.setPermission(perm, false);
                sender.sendMessage("§eRemoved permission §c" + perm + "§e from " + target.getName());
            }

            case "set" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /permission set <player> <permission>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage("§cPlayer not found or not online!");
                    return true;
                }

                String perm = args[2];
                PermissionAttachment attachment = attachments.get(target);
                if (attachment == null) {
                    attachment = target.addAttachment(Bukkit.getPluginManager().getPlugin("Multi-Plugin"));
                    attachments.put(target, attachment);
                }

                attachment.setPermission(perm, true);
                sender.sendMessage("§eAdded permission §a" + perm + " §eto " + target.getName());
            }
            default -> sender.sendMessage("§cUnknown subcommand! Use list, remove, or set.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (!sender.hasPermission("multiplugin.permission.manage")) return Collections.emptyList();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("list");
            completions.add("remove");
            completions.add("set");
        } else if (args.length == 2) {
            Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set"))) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                target.getEffectivePermissions().forEach(p -> completions.add(p.getPermission()));
            }
        }

        String current = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(current));

        return completions;
    }
}
