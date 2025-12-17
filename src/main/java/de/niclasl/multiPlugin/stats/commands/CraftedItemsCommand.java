package de.niclasl.multiPlugin.stats.commands;

import de.niclasl.multiPlugin.stats.gui.CraftedItemsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class CraftedItemsCommand implements CommandExecutor, TabCompleter {

    private static CraftedItemsGui craftedItemsGui;

    public CraftedItemsCommand(CraftedItemsGui craftedItemsGui) {
        CraftedItemsCommand.craftedItemsGui = craftedItemsGui;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.crafteditems")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        craftedItemsGui.open(player, player, 1);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        return List.of();
    }
}