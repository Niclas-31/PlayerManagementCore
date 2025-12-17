package de.niclasl.multiPlugin.report_system.gui;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import de.niclasl.multiPlugin.ban_system.model.BanRecord;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReportGui {

    private static MultiPlugin plugin;
    private static final HashMap<UUID, Boolean> playerSortMode = new HashMap<>();

    public ReportGui(MultiPlugin plugin) {
        ReportGui.plugin = plugin;
    }

    public static void open(Player viewer, OfflinePlayer target, int page) {
        List<Report> reports = ReportManager.getReports(target.getUniqueId());

        boolean newestFirst = playerSortMode.getOrDefault(viewer.getUniqueId(), true);
        reports.sort((w1, w2) -> newestFirst
                ? w2.getTime().compareTo(w1.getTime())
                : w1.getTime().compareTo(w2.getTime())
        );

        int[] allowedSlots = {
                0,1,2,3,4,5,6,7,
                9,10,11,12,13,14,15,16,
                18,19,20,21,22,23,24,25,
                27,28,29,30,31,32,33,34,
                36,37,38,39,40,41,42,43,
                45,46,47,48,49,50,51,52
        };

        int reportsPerPage = allowedSlots.length; // 48
        int totalPages = (int) Math.ceil(reports.size() / (double) reportsPerPage);
        if(totalPages == 0) totalPages = 1;

        page = Math.min(Math.max(page, 1), totalPages);

        Inventory inv = Bukkit.createInventory(null, 54, "§8Reports from " + target.getName() + " §7(" + page + "/" + totalPages + ")");

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{8}) {
            inv.setItem(i, glass);
        }

        if (totalPages == 1 || page == 1) {
            ItemStack glass1 = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
            for (int i : new int[]{35}) {
                inv.setItem(i, glass1);
            }
        }

        if (totalPages == 1 || totalPages == page) {
            ItemStack glass2 = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
            for (int i : new int[]{44}) {
                inv.setItem(i, glass2);
            }
        }

        int startIndex = (page - 1) * reportsPerPage;
        int endIndex = Math.min(startIndex + reportsPerPage, reports.size());

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

        ItemStack sortBook = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = sortBook.getItemMeta();
        assert bookMeta != null;
        bookMeta.setDisplayName("§eSort: " + (newestFirst ? "§aNewest → Oldest" : "§cOldest → Newest"));
        bookMeta.setLore(List.of("§7Click to toggle sort order"));
        sortBook.setItemMeta(bookMeta);
        inv.setItem(17, sortBook);

        viewer.openInventory(inv);
        viewer.setMetadata("report_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("report_page", new FixedMetadataValue(plugin, page));
    }

    public int getTotalPages(OfflinePlayer target) {
        List<BanRecord> bans = BanHistoryManager.getBanHistory(target.getUniqueId());

        int bansPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(bans.size() / (double) bansPerPage);
        if (totalPages == 0) totalPages = 1;
        return totalPages;
    }

    public static void toggleSort(Player player) {
        boolean mode = playerSortMode.getOrDefault(player.getUniqueId(), true);
        playerSortMode.put(player.getUniqueId(), !mode);
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
