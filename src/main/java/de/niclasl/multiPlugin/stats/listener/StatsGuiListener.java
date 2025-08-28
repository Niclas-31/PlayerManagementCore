package de.niclasl.multiPlugin.stats.listener;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import de.niclasl.multiPlugin.stats.gui.CraftedItemsGui;
import de.niclasl.multiPlugin.stats.gui.MinedBlocksGui;
import de.niclasl.multiPlugin.stats.gui.UsedItemsGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatsGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player viewer = (Player) event.getWhoClicked();

        String title = event.getView().getTitle();
        if (!title.startsWith("§8Stats: §7")) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();

        if (slot == 45) {
            if (viewer.hasPermission("manage.player")) {
                // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
                OfflinePlayer target = getTarget(viewer); // <- das musst du ggf. anpassen
                if (target != null) {
                    WatchGuiManager.openPage2(viewer, (Player) target);
                } else {
                    viewer.sendMessage("§cError: Target player not found.");
                    viewer.closeInventory();
                }
            } else {
                viewer.closeInventory();
            }
        }

        if (slot == 25) {
            OfflinePlayer target = getTarget(viewer);

            assert target != null;
            MinedBlocksGui.open(viewer, target, 1);
        }

        if (slot == 28) {
            OfflinePlayer target = getTarget(viewer);

            assert target != null;
            UsedItemsGui.open(viewer, target, 1);
        }

        if (slot == 37) {
            OfflinePlayer target = getTarget(viewer);

            assert target != null;
            CraftedItemsGui.open(viewer, target, 1);
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();

        // Slot 10 ist das NameTag-Item (laut StatsGui)
        ItemStack nameTag = inv.getItem(49);
        if (nameTag == null || !nameTag.hasItemMeta()) return null;

        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;

        String displayName = meta.getDisplayName();
        // displayName hat Format: "§aSpielername", wir entfernen den §a-Code
        String playerName = ChatColor.stripColor(displayName);

        if (playerName.isEmpty()) return null;

        return Bukkit.getOfflinePlayer(playerName);
    }
}