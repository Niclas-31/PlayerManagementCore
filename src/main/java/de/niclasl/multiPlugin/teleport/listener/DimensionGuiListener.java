package de.niclasl.multiPlugin.teleport.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import de.niclasl.multiPlugin.teleport.gui.DimensionGui;
import de.niclasl.multiPlugin.teleport.manager.TeleportManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class DimensionGuiListener implements Listener {

    private static TeleportManager teleportManager;

    public DimensionGuiListener(TeleportManager teleportManager) {
        DimensionGuiListener.teleportManager = teleportManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.startsWith("§8Teleport to Dimension ")) return;

        event.setCancelled(true);

        // Metadata auslesen (angepasst auf DimensionGui)
        if (!player.hasMetadata("dimension_target") || !player.hasMetadata("dimension_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("dimension_target").get(0).asString());
        int page = player.getMetadata("dimension_page").get(0).asInt();

        int slot = event.getSlot();

        // BACK Button (Slot 26 im GUI)
        if (slot == 26) {
            if (player.hasPermission("manage.player")) {
                // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
                OfflinePlayer target = getTarget(player); // <- das musst du ggf. anpassen
                if (target != null) {
                    WatchGuiManager.open1(player, (Player) target);
                } else {
                    player.sendMessage("§cError: Target player not found.");
                    player.closeInventory();
                }
            } else {
                player.closeInventory();
            }
        }

        // Klick auf vorherige Seite Button (Slot 35)
        if (slot == 35) {
            if (page > 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                DimensionGui.open(player, target, page - 1);
            }
            return;
        }

        // Klick auf Nächste Seite Button (Slot 44)
        if (slot == 44) {
            List<String> dimensions = teleportManager.getAllDimensions();
            int dimensionsPerPage = GuiConstants.ALLOWED_SLOTS.length;
            int totalPages = (int) Math.ceil(dimensions.size() / (double) dimensionsPerPage);
            if (page < totalPages) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                DimensionGui.open(player, target, page + 1);
            }
            return;
        }

        // Dimension Items (Slots z.B. 0-43, siehe DimensionGui.allowedSlots)
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        List<String> lore = meta.getLore();
        if (lore == null) return;

        String dimension = ChatColor.stripColor(meta.getDisplayName()).toLowerCase();
        for (String line : lore) {
            if (line.toLowerCase().startsWith("§7dimension:")) {
                dimension = ChatColor.stripColor(line.substring("§7dimension:".length()).trim());
                break;
            }
        }

        Location targetLoc = teleportManager.getLocation(dimension);

        if (targetLoc == null) {
            player.sendMessage(ChatColor.RED + "No location set for dimension §6'" + dimension + "'§c.");
            return;
        }

        UUID owner = teleportManager.getOwner(dimension);

        if (owner != null && !owner.equals(player.getUniqueId())) {
            if (!player.hasPermission("teleport.dimension." + dimension)) {
                player.sendMessage(ChatColor.RED + "You don't have permission to teleport to this private dimension.");
                return;
            }
        } else {
            if (!player.hasPermission("teleport.player")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to teleport.");
                return;
            }
        }


        // Verzögertes Teleportieren mit Effekten
        teleportManager.teleportWithDelay(player, targetLoc, TeleportManager.getTeleportDelay(dimension), dimension);
        player.closeInventory();
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();

        // Slot 10 ist das NameTag-Item (laut StatsGui)
        ItemStack nameTag = inv.getItem(53);
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