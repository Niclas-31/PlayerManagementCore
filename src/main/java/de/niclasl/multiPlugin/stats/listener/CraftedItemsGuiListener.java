package de.niclasl.multiPlugin.stats.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.stats.gui.CraftedItemsGui;
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

public class CraftedItemsGuiListener implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.startsWith("§9Crafted Items: ")) return;

        event.setCancelled(true);

        // Metadaten prüfen
        if (!player.hasMetadata("craft_target") || !player.hasMetadata("craft_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("craft_target").get(0).asString());
        int page = player.getMetadata("craft_page").get(0).asInt();

        int slot = event.getSlot();

        // Anzahl der Items zählen
        int totalCraftedItems = 0;
        for (Material mat : Material.values()) {
            try {
                int count = Bukkit.getOfflinePlayer(targetUUID).getStatistic(Statistic.CRAFT_ITEM, mat);
                if (count > 0) totalCraftedItems++;
            } catch (IllegalArgumentException ignored) {}
        }

        int itemsPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(totalCraftedItems / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        // Button: Zurück zur Übersicht
        if (slot == 26) {
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
            CraftedItemsGui.open(player, target, page - 1);
            return;
        }

        // Button: Nächste Seite
        if (slot == 44 && page < totalPages) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            CraftedItemsGui.open(player, target, page + 1);
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