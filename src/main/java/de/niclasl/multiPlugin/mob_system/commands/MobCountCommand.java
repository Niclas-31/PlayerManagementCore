package de.niclasl.multiPlugin.mob_system.commands;

import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import de.niclasl.multiPlugin.mob_system.model.MobSpawnRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobCountCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        if (!sender.hasPermission("multiplugin.mobcount")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /mobcount <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        UUID targetUUID = target.getUniqueId();
        List<MobSpawnRequest> requests = MobManager.getRequests(targetUUID);

        int valid = 0;
        int invalid = 0;

        for (MobSpawnRequest req : requests) {
            EntityType type = req.getEntityType();
            if (type != null && type.isAlive()) {
                valid++;
            } else {
                invalid++;
            }
        }

        player.sendMessage(ChatColor.AQUA + "Mob status for " + target.getName() + ":");
        player.sendMessage(ChatColor.GREEN + "✔ Living mobs: " + valid);
        player.sendMessage(ChatColor.RED + "✘ Invalid or not alive: " + invalid);
        player.sendMessage(ChatColor.GRAY + "➜ Total: " + requests.size());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());
        }

        return completions;
    }
}