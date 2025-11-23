package de.niclasl.multiPlugin.warn_system.gui;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import de.niclasl.multiPlugin.warn_system.model.Warning;
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

import java.util.*;

public class WarnGui {

    private static MultiPlugin plugin;
    public static WarnManager warnManager;

    // Sortiermodus pro Spieler (true = neu → alt, false = alt → neu)
    private static final HashMap<UUID, Boolean> playerSortMode = new HashMap<>();

    public WarnGui(MultiPlugin plugin, WarnManager warnManager) {
        WarnGui.plugin = plugin;
        WarnGui.warnManager = warnManager;
    }

    public void open(Player viewer, OfflinePlayer target, int page) {
        List<Warning> warnings = new ArrayList<>(WarnManager.getWarnings(target.getUniqueId()));

        // Sortierung anwenden
        boolean newestFirst = playerSortMode.getOrDefault(viewer.getUniqueId(), true);
        warnings.sort((w1, w2) -> newestFirst
                ? w2.getDate().compareTo(w1.getDate())
                : w1.getDate().compareTo(w2.getDate())
        );

        int[] allowedSlots = {
                0,1,2,3,4,5,6,7,
                9,10,11,12,13,14,15,16,
                18,19,20,21,22,23,24,25,
                27,28,29,30,31,32,33,34,
                36,37,38,39,40,41,42,43,
                45,46,47,48,49,50,51,52
        };

        int warningsPerPage = allowedSlots.length; // 48
        int totalPages = (int) Math.ceil(warnings.size() / (double) warningsPerPage);
        if (totalPages == 0) totalPages = 1;
        page = Math.min(Math.max(page, 1), totalPages);

        Inventory inv = Bukkit.createInventory(null, 54, "§8Warnings from " + target.getName() + " §7(" + page + "/" + totalPages + ")");

        // Rand
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{8,17}) inv.setItem(i, glass);
        if (totalPages == 1 || page == 1) inv.setItem(35, glass);
        if (totalPages == 1 || totalPages == page) inv.setItem(44, glass);

        int startIndex = (page - 1) * warningsPerPage;
        int endIndex = Math.min(startIndex + warningsPerPage, warnings.size());

        for (int i = startIndex; i < endIndex; i++) {
            Warning warning = warnings.get(i);
            int slot = allowedSlots[i - startIndex];

            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta paperMeta = paper.getItemMeta();
            assert paperMeta != null;

            paperMeta.setDisplayName("§eWarning " + (i + 1));
            paperMeta.setLore(warning.isPermanent()
                            ? List.of(
                            "§7Reason: §f" + warning.getReason(),
                            "§7By: §f" + warning.getFrom(),
                            "§7Date: §f" + warning.getDate(),
                            "§7Warn points: §f" + warning.getPoints(),
                            "§7Warn Id: §f" + warning.getId(),
                            "§c§lPERMANENT – Right click possible"
                    )
                            : List.of(
                            "§7Reason: §f" + warning.getReason(),
                            "§7By: §f" + warning.getFrom(),
                            "§7Date: §f" + warning.getDate(),
                            "§7Warn points: §f" + warning.getPoints(),
                            "§7Warn Id: §f" + warning.getId(),
                            "§7§oRight click: Make permanent",
                            "§e§oLeft click: Delete"
                    )
            );
            paper.setItemMeta(paperMeta);
            inv.setItem(slot, paper);
        }

        // Kopf-Icon (rechts unten)
        int totalPoints = warnManager.getTotalPoints(target.getUniqueId());
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        meta.setLore(List.of(
                ChatColor.RED + "Total Warn points: " + totalPoints,
                ChatColor.AQUA + "Total Warnings: " + warnings.size()
        ));
        skull.setItemMeta(meta);
        inv.setItem(53, skull);

        // Zurück-Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName(ChatColor.WHITE + "Back");
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        // Navigationspfeile
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

        // Sortier-Buch (neu → alt / alt → neu)
        ItemStack sortBook = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = sortBook.getItemMeta();
        assert bookMeta != null;
        bookMeta.setDisplayName("§eSort: " + (newestFirst ? "§aNewest → Oldest" : "§cOldest → Newest"));
        bookMeta.setLore(List.of("§7Click to toggle sort order"));
        sortBook.setItemMeta(bookMeta);
        inv.setItem(17, sortBook);

        viewer.openInventory(inv);
        viewer.setMetadata("warn_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("warn_page", new FixedMetadataValue(plugin, page));
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

    // Toggle sort mode für einen Spieler
    public static void toggleSort(Player player) {
        boolean mode = playerSortMode.getOrDefault(player.getUniqueId(), true);
        playerSortMode.put(player.getUniqueId(), !mode);
    }

    public int getTotalPages(OfflinePlayer target) {
        List<Warning> warnings = WarnManager.getWarnings(target.getUniqueId());

        int bansPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(warnings.size() / (double) bansPerPage);
        if (totalPages == 0) totalPages = 1;
        return totalPages;
    }
}