package de.niclasl.multiPlugin.warn_system.listener;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import de.niclasl.multiPlugin.warn_system.gui.WarnGui;
import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import de.niclasl.multiPlugin.warn_system.model.Warning;
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

public class WarnGuiListener implements Listener {

    private final WarnManager warnManager;
    private final WarnGui warnGui;

    // Slots, die Warnungen anzeigen (müssen mit WarnGui übereinstimmen)
    private final int[] allowedSlots = {
            0,1,2,3,4,5,6,7,
            9,10,11,12,13,14,15,16,
            18,19,20,21,22,23,24,25,
            27,28,29,30,31,32,33,34,
            36,37,38,39,40,41,42,43,
            45,46,47,48,49,50,51,52
    };

    public WarnGuiListener(WarnManager warnManager, WarnGui warnGui) {
        this.warnManager = warnManager;
        this.warnGui = warnGui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§8Warnings from ")) return;

        e.setCancelled(true);

        if (!player.hasMetadata("warn_target") || !player.hasMetadata("warn_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("warn_target").get(0).asString());
        int page = player.getMetadata("warn_page").get(0).asInt();

        int slot = e.getSlot();

        if (slot == 26) {
            // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
            OfflinePlayer target = getTarget(player); // <- das musst du ggf. anpassen
            if (target != null) {
                WatchGuiManager.openPage2(player, (Player) target);
            } else {
                player.sendMessage("§cError: Target player not found.");
                player.closeInventory();
            }
        }

        // Klick auf vorherige Seite Button (Slot 35)
        if (slot == 35) {
            if (page > 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                warnGui.open(player, target, page - 1);
            }
            return;
        }

        // Klick auf Nächste Seite Button (Slot 44)
        if (slot == 44) {
            List<Warning> warnings = warnManager.getWarnings(targetUUID);
            int warningsPerPage = allowedSlots.length;
            int totalPages = (int) Math.ceil(warnings.size() / (double) warningsPerPage);
            if (page < totalPages) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                warnGui.open(player, target, page + 1);
            }
            return;
        }

        // Klick auf Warnungen
        // Prüfen, ob Slot in allowedSlots ist
        int indexInPage = -1;
        for (int i = 0; i < allowedSlots.length; i++) {
            if (allowedSlots[i] == slot) {
                indexInPage = i;
                break;
            }
        }
        if (indexInPage == -1) return; // Kein gültiger Slot

        List<Warning> warnings = warnManager.getWarnings(targetUUID);

        int warningIndex = (page - 1) * allowedSlots.length + indexInPage;
        if (warningIndex < 0 || warningIndex >= warnings.size()) {
            player.sendMessage("§cInvalid warning slot.");
            return;
        }

        Warning warning = warnings.get(warningIndex);

        // Rechtsklick → permanent machen
        if (e.isRightClick()) {
            if (warning.isPermanent()) {
                player.sendMessage("§7This warning is already §cpermanent§7.");
                return;
            }

            warning.setPermanent(true);
            warnManager.saveWarnings(targetUUID, warnings);

            player.sendMessage("§aWarning #" + (warningIndex + 1) + " has been made permanent.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            warnGui.open(player, target, page);
            return;
        }

        // Linksklick → löschen (nur wenn nicht permanent)
        if (e.isLeftClick()) {
            if (warning.isPermanent()) {
                player.sendMessage("§cThis warning is permanent and cannot be deleted.");
                return;
            }

            warnManager.removeWarning(targetUUID, warningIndex);

            player.sendMessage("§aWarning #" + (warningIndex + 1) + " has been deleted.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            warnGui.open(player, target, page);
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