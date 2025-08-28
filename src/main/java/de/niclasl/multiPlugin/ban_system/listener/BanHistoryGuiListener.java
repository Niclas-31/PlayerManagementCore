package de.niclasl.multiPlugin.ban_system.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.ban_system.gui.BanHistoryGui;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import de.niclasl.multiPlugin.ban_system.model.BanRecord;
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

import java.util.List;
import java.util.UUID;

public class BanHistoryGuiListener implements Listener {

    private static BanHistoryManager banHistoryManager;

    public BanHistoryGuiListener(BanHistoryManager banHistoryManager) {
        BanHistoryGuiListener.banHistoryManager = banHistoryManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§8Bans from ")) return;

        e.setCancelled(true);

        if (!player.hasMetadata("ban_target") || !player.hasMetadata("ban_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("ban_target").get(0).asString());
        int page = player.getMetadata("ban_page").get(0).asInt();

        int slot = e.getSlot();

        if (slot == 26) {
            // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
            OfflinePlayer target = getTarget(player); // <- das musst du ggf. anpassen
            if (target != null) {
                WatchGuiManager.openPage1(player, (Player) target);
            } else {
                player.sendMessage("§cError: Target player not found.");
                player.closeInventory();
            }
        }

        // Klick auf vorherige Seite Button (Slot 35)
        if (slot == 35) {
            if (page > 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                BanHistoryGui.open(player, target, page - 1);
            }
            return;
        }

        // Klick auf Nächste Seite Button (Slot 44)
        if (slot == 44) {
            List<BanRecord> bans = BanHistoryManager.getBanHistory(targetUUID);
            int warningsPerPage = GuiConstants.ALLOWED_SLOTS.length;
            int totalPages = (int) Math.ceil(bans.size() / (double) warningsPerPage);
            if (page < totalPages) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                BanHistoryGui.open(player, target, page + 1);
            }
            return;
        }

        // Klick auf Warnungen
        // Prüfen, ob Slot in allowedSlots ist
        int indexInPage = -1;
        for (int i = 0; i < GuiConstants.ALLOWED_SLOTS.length; i++) {
            if (GuiConstants.ALLOWED_SLOTS[i] == slot) {
                indexInPage = i;
                break;
            }
        }
        if (indexInPage == -1) return; // Kein gültiger Slot

        List<BanRecord> bans = BanHistoryManager.getBanHistory(targetUUID);

        int banIndex = (page - 1) * GuiConstants.ALLOWED_SLOTS.length + indexInPage;
        if (banIndex < 0 || banIndex >= bans.size()) {
            player.sendMessage("§cInvalid ban slot.");
            return;
        }

        BanRecord ban = bans.get(banIndex);

        // Rechtsklick → permanent machen
        if (e.isRightClick()) {
            if (ban.isPermanent()) {
                player.sendMessage("§7This warning is already §cpermanent§7.");
                return;
            }

            ban.setPermanent(true);
            banHistoryManager.saveBanHistory(targetUUID, bans);

            player.sendMessage("§aBan #" + (banIndex + 1) + " has been made permanent.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            BanHistoryGui.open(player, target, page);
            return;
        }

        // Linksklick → löschen (nur wenn nicht permanent)
        if (e.isLeftClick()) {
            if (ban.isPermanent()) {
                player.sendMessage("§cThis ban is permanent and cannot be deleted.");
                return;
            }

            banHistoryManager.removeBanHistory(targetUUID, banIndex);

            player.sendMessage("§aBan #" + (banIndex + 1) + " has been deleted.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            BanHistoryGui.open(player, target, page);
        }
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