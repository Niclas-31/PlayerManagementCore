package de.niclasl.multiPlugin.report_system.gui;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.report_system.manager.ReportManager;
import de.niclasl.multiPlugin.report_system.model.Report;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class ReportGui {

    private static MultiPlugin plugin;
    private static ReportManager reportManager;

    public ReportGui(MultiPlugin plugin, ReportManager reportManager) {
        ReportGui.plugin = plugin;
        ReportGui.reportManager = reportManager;
    }

    public static void open(Player viewer, OfflinePlayer target, int page) {
        List<Report> reports = reportManager.getReports(target.getUniqueId());

        // Slots, die für Warnungen benutzt werden (48 Slots verteilt auf 6 Reihen, je 8 Slots, ohne die jeweils 9., 17., 26., 35., 44., 53. Slots)
        int[] allowedSlots = {
                0,1,2,3,4,5,6,7,
                9,10,11,12,13,14,15,16,
                18,19,20,21,22,23,24,25,
                27,28,29,30,31,32,33,34,
                36,37,38,39,40,41,42,43,
                45,46,47,48,49,50,51,52
        };

        int warningsPerPage = allowedSlots.length; // 48
        int totalPages = (int) Math.ceil(reports.size() / (double) warningsPerPage);
        if(totalPages == 0) totalPages = 1;

        page = Math.min(Math.max(page, 1), totalPages);

        Inventory inv = Bukkit.createInventory(null, 54, "§8Reports from " + target.getName() + " §7(" + page + "/" + totalPages + ")");

        int startIndex = (page - 1) * warningsPerPage;
        int endIndex = Math.min(startIndex + warningsPerPage, reports.size());

        for (int i = startIndex; i < endIndex; i++) {
            Report report = reports.get(i);

            int slot = allowedSlots[i - startIndex];

            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta paperMeta = paper.getItemMeta();
            assert paperMeta != null;

            paperMeta.setDisplayName("§eReport " + (i + 1));
            paperMeta.setLore(report.isPermanent()
                            ? List.of(
                            "§7Reason: §f" + report.getReason(),
                            "§7By: §f" + report.getFrom(),
                            "§7Date: §f" + report.getTime(),
                            "§7Status: §f" + report.getStatus(),
                            "§7Report Id: §f" + report.getId(),
                            "§c§lPERMANENT – Right click possible"
                    )
                            : List.of(
                            "§7Reason: §f" + report.getReason(),
                            "§7By: §f" + report.getFrom(),
                            "§7Date: §f" + report.getTime(),
                            "§7Status: §f" + report.getStatus(),
                            "§7Warn Id: §f" + report.getId(),
                            "§7§oRight click: Make permanent",
                            "§e§oLeft click: Delete"
                    )
            );
            paper.setItemMeta(paperMeta);

            inv.setItem(slot, paper);
        }

        // Kopf ganz rechts unten (Slot 53)
        int headSlot = 53;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        meta.setLore(List.of(ChatColor.AQUA + "Total Reports: " + reports.size()));
        skull.setItemMeta(meta);
        inv.setItem(headSlot, skull);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName(ChatColor.WHITE + "Back");
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        // Navigationspfeile:
        if (page > 1) {
            ItemStack backArrow = new ItemStack(Material.ARROW);
            ItemMeta backArrowMeta = backArrow.getItemMeta();
            assert backArrowMeta != null;
            backArrowMeta.setDisplayName("§aPrevious page");
            backArrow.setItemMeta(backArrowMeta);
            inv.setItem(35, backArrow);
        }
        if (page < totalPages) {
            ItemStack nextArrow = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextArrow.getItemMeta();
            assert nextMeta != null;
            nextMeta.setDisplayName("§aNext page");
            nextArrow.setItemMeta(nextMeta);
            inv.setItem(44, nextArrow);
        }

        viewer.openInventory(inv);
        viewer.setMetadata("report_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("report_page", new FixedMetadataValue(plugin, page));
    }
}
