package de.niclasl.multiPlugin.stats.commands;

import de.niclasl.multiPlugin.stats.gui.UsedItemsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class UsedItemsCommand implements CommandExecutor, TabCompleter {

    private static UsedItemsGui usedItemsGui;

    public UsedItemsCommand(UsedItemsGui usedItemsGui) {
        UsedItemsCommand.usedItemsGui = usedItemsGui;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.useditems")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        usedItemsGui.open(player, player, 1);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] strings) {
        return List.of();
    }
}
