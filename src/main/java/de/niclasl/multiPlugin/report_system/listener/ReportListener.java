package de.niclasl.multiPlugin.report_system.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
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

    private static ReportManager reportManager;
    private final MultiPlugin plugin;

    public ReportListener(ReportManager reportManager, MultiPlugin plugin) {
        ReportListener.reportManager = reportManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§8Reports from ")) return;

        e.setCancelled(true);

        if (!player.hasMetadata("report_target") || !player.hasMetadata("report_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("report_target").getFirst().asString());
        int page = player.getMetadata("report_page").getFirst().asInt();

        int slot = e.getSlot();

        if (slot == 26) {
            OfflinePlayer target = getTarget(player);
            if (target != null) {
                plugin.getWatchGuiManager().open1(player, (Player) target);
            } else {
                player.sendMessage("§cError: Target player not found.");
                player.closeInventory();
            }
        }

        if (slot == 35) {
            if (page > 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                ReportGui.open(player, target, page - 1);
            }
            return;
        }

        if (slot == 44) {
            List<Report> warnings = ReportManager.getReports(targetUUID);
            int warningsPerPage = GuiConstants.ALLOWED_SLOTS.length;
            int totalPages = (int) Math.ceil(warnings.size() / (double) warningsPerPage);
            if (page < totalPages) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                ReportGui.open(player, target, page + 1);
            }
            return;
        }

        if (slot == 17) {
            ReportGui.toggleSort(player);
            OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(player.getMetadata("report_target").getFirst().asString()));
            ReportGui.open(player, target, 1);
        }

        int indexInPage = -1;
        for (int i = 0; i < GuiConstants.ALLOWED_SLOTS.length; i++) {
            if (GuiConstants.ALLOWED_SLOTS[i] == slot) {
                indexInPage = i;
                break;
            }
        }
        if (indexInPage == -1) return;

        List<Report> reports = ReportManager.getReports(targetUUID);

        int reportIndex = (page - 1) * GuiConstants.ALLOWED_SLOTS.length + indexInPage;
        if (reportIndex < 0 || reportIndex >= reports.size()) {
            player.sendMessage("§cInvalid report slot.");
            return;
        }

        Report report = reports.get(reportIndex);

        if (e.isRightClick()) {
            if (report.isPermanent()) {
                player.sendMessage("§7This report is already §cpermanent§7.");
                return;
            }

            report.setPermanent(true);
            reportManager.saveReports(targetUUID, reports);

            player.sendMessage("§aReport #" + (reportIndex + 1) + " has been made permanent.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            ReportGui.open(player, target, page);
            return;
        }

        if (e.isLeftClick()) {
            if (report.isPermanent()) {
                player.sendMessage("§cThis report is permanent and cannot be deleted.");
                return;
            }

            reportManager.removeReport(targetUUID, reportIndex);

            player.sendMessage("§aReport #" + (reportIndex + 1) + " has been deleted.");
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
            ReportGui.open(player, target, page);
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();

        ItemStack nameTag = inv.getItem(53);
        if (nameTag == null || !nameTag.hasItemMeta()) return null;

        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;

        String displayName = meta.getDisplayName();

        String playerName = ChatColor.stripColor(displayName);

        if (playerName.isEmpty()) return null;

        return Bukkit.getOfflinePlayer(playerName);
    }
}