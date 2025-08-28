package de.niclasl.multiPlugin.stats.commands;

import de.niclasl.multiPlugin.stats.gui.CraftedItemsGui;
import de.niclasl.multiPlugin.stats.gui.MinedBlocksGui;
import de.niclasl.multiPlugin.stats.gui.StatsGui;
import de.niclasl.multiPlugin.stats.gui.UsedItemsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    private static StatsGui statsGui;
    private static MinedBlocksGui minedBlocksGui;
    private static UsedItemsGui usedItemsGui;
    private static CraftedItemsGui craftedItemsGui;

    public StatsCommand(StatsGui statsGui, MinedBlocksGui minedBlocksGui, UsedItemsGui usedItemsGui, CraftedItemsGui craftedItemsGui    ) {
        StatsCommand.statsGui = statsGui;
        StatsCommand.minedBlocksGui = minedBlocksGui;
        StatsCommand.usedItemsGui = usedItemsGui;
        StatsCommand.craftedItemsGui = craftedItemsGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        switch (label.toLowerCase()) {
            case "stats" -> {
                if (!sender.hasPermission("multiplugin.stats")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                openStatsGui(player);
            }
            case "minedblocks" -> {
                if (!sender.hasPermission("multiplugin.minedblocks")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                openMinedBlocksGui(player);
            }
            case "useditems" -> {
                if (!sender.hasPermission("multiplugin.useditems")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                openUsedItemsGui(player);
            }
            case "crafteditems" -> {
                if (!sender.hasPermission("multiplugin.crafteditems")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                openCraftedItemsGui(player);
            }
            default -> player.sendMessage("§cUnknown command.");
        }
        return true;
    }

    public void openStatsGui(Player player) {
        statsGui.open(player, player);
    }

    private void openMinedBlocksGui(Player player) {
        minedBlocksGui.open(player, player, 1);
    }

    private void openUsedItemsGui(Player player) {
        usedItemsGui.open(player, player, 1);
    }

    private void openCraftedItemsGui(Player player) {
        craftedItemsGui.open(player, player, 1);
    }
}