package de.niclasl.multiPlugin.stats.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public record MinedBlocksGuiListener(MultiPlugin plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.startsWith("§9Mined Blocks: ")) return;

        event.setCancelled(true);

        if (!player.hasMetadata("block_target") || !player.hasMetadata("block_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("block_target").getFirst().asString());
        int page = player.getMetadata("block_page").getFirst().asInt();

        int slot = event.getSlot();

        int totalMinedBlocks = 0;
        for (Material mat : Material.values()) {
            if (!mat.isBlock()) continue;
            try {
                int count = Bukkit.getOfflinePlayer(targetUUID).getStatistic(Statistic.MINE_BLOCK, mat);
                if (count > 0) totalMinedBlocks++;
            } catch (IllegalArgumentException ignored) {
            }
        }

        int blocksPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(totalMinedBlocks / (double) blocksPerPage);
        if (totalPages == 0) totalPages = 1;

        if (slot == 26) {
            OfflinePlayer target = getTarget(player);
            if (target instanceof Player onlineTarget)
                plugin.getEnchantGUI().open(player, onlineTarget);
            else {
                player.sendMessage("§cTarget player not found.");
                player.closeInventory();
            }
            return;
        }

        if (slot == 35 && page > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            plugin.getMinedBlocksGui().open(player, target, page - 1);
            return;
        }

        if (slot == 44 && page < totalPages) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            plugin.getMinedBlocksGui().open(player, target, page + 1);
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();
        ItemStack nameTag = inv.getItem(53);
        if (nameTag == null || !nameTag.hasItemMeta()) return null;
        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;
        return Bukkit.getOfflinePlayer(ChatColor.stripColor(meta.getDisplayName()));
    }
}