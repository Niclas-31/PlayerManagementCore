package de.niclasl.playerManagementCore.stats.commands;

import de.niclasl.playerManagementCore.stats.gui.MinedBlocksGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MinedBlocksCommand implements CommandExecutor, TabCompleter {

    private static MinedBlocksGui minedBlocksGui;

    public MinedBlocksCommand(MinedBlocksGui minedBlocksGui) {
        MinedBlocksCommand.minedBlocksGui = minedBlocksGui;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!sender.hasPermission("minedblocks")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Target player not found!");
            return true;
        }

        minedBlocksGui.open(player, target, 1);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        }
        return completions;
    }
}
