package de.niclasl.multiPlugin.stats.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.stats.gui.MinedBlocksGui;
import de.niclasl.multiPlugin.stats.gui.StatsGui;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class MinedBlocksGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.startsWith("§9Mined Blocks: ")) return;

        event.setCancelled(true);

        // Metadaten prüfen
        if (!player.hasMetadata("block_target") || !player.hasMetadata("block_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("block_target").get(0).asString());
        int page = player.getMetadata("block_page").get(0).asInt();

        int slot = event.getSlot();

        int totalMinedBlocks = 0;
        for (Material mat : Material.values()) {
            if (!mat.isBlock()) continue;
            try {
                int count = Bukkit.getOfflinePlayer(targetUUID).getStatistic(Statistic.MINE_BLOCK, mat);
                if (count > 0) totalMinedBlocks++;
            } catch (IllegalArgumentException ignored) {}
        }

        int blocksPerPage = GuiConstants.ALLOWED_SLOTS.length; // oder hardcoded: 45
        int totalPages = (int) Math.ceil(totalMinedBlocks / (double) blocksPerPage);
        if (totalPages == 0) totalPages = 1;

        // Button: Zurück zur Übersicht
        if (slot == 26) { // zurück zur Übersicht
            OfflinePlayer target = getTarget(player);
            if (target instanceof Player onlineTarget)
                StatsGui.open(player, onlineTarget);
            else {
                player.sendMessage("§cTarget player not found.");
                player.closeInventory();
            }
            return;
        }

        // Button: Vorherige Seite
        if (slot == 35 && page > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            MinedBlocksGui.open(player, target, page - 1);
            return;
        }

        // Button: Nächste Seite
        if (slot == 44 && page < totalPages) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            MinedBlocksGui.open(player, target, page + 1);
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();
        ItemStack nameTag = inv.getItem(53); // oder anderer Slot
        if (nameTag == null || !nameTag.hasItemMeta()) return null;
        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;
        return Bukkit.getOfflinePlayer(ChatColor.stripColor(meta.getDisplayName()));
    }
}