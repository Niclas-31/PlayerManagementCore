package de.niclasl.multiPlugin.stats.listener;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
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

        if (event.getCurrentItem() == null) return;
        if (!event.getView().getTitle().equalsIgnoreCase("§8Player Stats")) return;

        event.setCancelled(true); // Verhindert das Herausnehmen der Items

        int slot = event.getRawSlot();

        if (slot == 36) {
            // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
            OfflinePlayer target = getTarget(viewer); // <- das musst du ggf. anpassen
            if (target != null) {
                WatchGuiManager.openPage2(viewer, (Player) target);
            } else {
                viewer.sendMessage("§cError: Target player not found.");
                viewer.closeInventory();
            }
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();

        // Slot 10 ist das NameTag-Item (laut StatsGui)
        ItemStack nameTag = inv.getItem(10);
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