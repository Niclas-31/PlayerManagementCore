package de.niclasl.multiPlugin.report_system.listener;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import de.niclasl.multiPlugin.report_system.gui.ReportGui;
import de.niclasl.multiPlugin.report_system.manager.ReportManager;
import de.niclasl.multiPlugin.report_system.model.Report;
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

public class ReportListener implements Listener {

    private final ReportManager reportManager;
    private final ReportGui reportGui;

    // Slots, die Warnungen anzeigen (müssen mit WarnGui übereinstimmen)
    private final int[] allowedSlots = {
            0,1,2,3,4,5,6,7,
            9,10,11,12,13,14,15,16,
            18,19,20,21,22,23,24,25,
            27,28,29,30,31,32,33,34,
            36,37,38,39,40,41,42,43,
            45,46,47,48,49,50,51,52
    };

    public ReportListener(ReportManager reportManager, ReportGui reportGui) {
        this.reportManager = reportManager;
        this.reportGui = reportGui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§8Reports from ")) return;

        e.setCancelled(true);

        if (!player.hasMetadata("report_target") || !player.hasMetadata("report_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("report_target").get(0).asString());
        int page = player.getMetadata("report_page").get(0).asInt();

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
                reportGui.open(player, target, page - 1);
            }
            return;
        }

        // Klick auf Nächste Seite Button (Slot 44)
        if (slot == 44) {
            List<Report> warnings = reportManager.getReports(targetUUID);
            int warningsPerPage = allowedSlots.length;
            int totalPages = (int) Math.ceil(warnings.size() / (double) warningsPerPage);
            if (page < totalPages) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                reportGui.open(player, target, page + 1);
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

        List<Report> reports = reportManager.getReports(targetUUID);

        int reportIndex = (page - 1) * allowedSlots.length + indexInPage;
        if (reportIndex < 0 || reportIndex >= reports.size()) {
            player.sendMessage("§cInvalid report slot.");
            return;
        }

        Report report = reports.get(reportIndex);

        // Rechtsklick → permanent machen
        if (e.isRightClick()) {
            if (report.isPermanent()) {
                player.sendMessage("§7This report is already §cpermanent§7.");
                return;
            }

            report.setPermanent(true);
            reportManager.saveReports(targetUUID, reports);

            player.sendMessage("§aReport #" + (reportIndex + 1) + " has been made permanent.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            reportGui.open(player, target, page);
            return;
        }

        // Linksklick → löschen (nur wenn nicht permanent)
        if (e.isLeftClick()) {
            if (report.isPermanent()) {
                player.sendMessage("§cThis report is permanent and cannot be deleted.");
                return;
            }

            reportManager.removeReport(targetUUID, reportIndex);

            player.sendMessage("§aReport #" + (reportIndex + 1) + " has been deleted.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            reportGui.open(player, target, page);
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