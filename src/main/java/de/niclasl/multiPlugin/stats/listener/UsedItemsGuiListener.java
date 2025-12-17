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

public record UsedItemsGuiListener(MultiPlugin plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.startsWith("§9Used Items: ")) return;

        event.setCancelled(true);

        if (!player.hasMetadata("item_target") || !player.hasMetadata("item_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("item_target").getFirst().asString());
        int page = player.getMetadata("item_page").getFirst().asInt();

        int slot = event.getSlot();

        int totalUsedItems = 0;
        for (Material mat : Material.values()) {
            try {
                int count = Bukkit.getOfflinePlayer(targetUUID).getStatistic(Statistic.USE_ITEM, mat);
                if (count > 0) totalUsedItems++;
            } catch (IllegalArgumentException ignored) {
            }
        }

        int itemsPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(totalUsedItems / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        if (slot == 26) {
            OfflinePlayer target = getTarget(player);
            if (target instanceof Player onlineTarget)
                plugin.getStatsGui().open(player, onlineTarget);
            else {
                player.sendMessage("§cTarget player not found.");
                player.closeInventory();
            }
            return;
        }

        if (slot == 35 && page > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            plugin.getUsedItemsGui().open(player, target, page - 1);
            return;
        }

        if (slot == 44 && page < totalPages) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            plugin.getUsedItemsGui().open(player, target, page + 1);
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