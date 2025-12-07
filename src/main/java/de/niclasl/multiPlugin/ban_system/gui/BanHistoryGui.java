package de.niclasl.multiPlugin.ban_system.gui;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.ban_system.model.BanRecord;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
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

public class BanHistoryGui {

    private static MultiPlugin plugin;
    private static final HashMap<UUID, Boolean> playerSortMode = new HashMap<>();

    public BanHistoryGui(MultiPlugin plugin) {
        BanHistoryGui.plugin = plugin;
    }

    public static void open(Player viewer, OfflinePlayer target, int page) {
        List<BanRecord> bans = BanHistoryManager.getBanHistory(target.getUniqueId());

        boolean newestFirst = playerSortMode.getOrDefault(viewer.getUniqueId(), true);
        bans.sort((w1, w2) -> newestFirst
                ? w2.getDate().compareTo(w1.getDate())
                : w1.getDate().compareTo(w2.getDate())
        );

        int bansPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(bans.size() / (double) bansPerPage);
        if (totalPages == 0) totalPages = 1;

        page = Math.max(1, Math.min(page, totalPages));

        Inventory inv = Bukkit.createInventory(null, 54, "§8Bans from " + target.getName() + " §7(" + page + "/" + totalPages + ")");

        // Rand
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

        int startIndex = (page - 1) * bansPerPage;
        int endIndex = Math.min(startIndex + bansPerPage, bans.size());

        for (int i = startIndex; i < endIndex; i++) {
            BanRecord record = bans.get(i);

            String reason = record.getReason() != null ? record.getReason() : "Unknown";
            String id = record.getId() != null ? record.getId() : "Unknown";
            String by = record.getBy() != null ? record.getBy() : "Unknown";
            String date = record.getDate() != null ? record.getDate() : "Unknown";
            String duration = record.getDuration() != null ? record.getDuration() : "Unknown";
            String unbanDate = record.getUnbanDate() != null ? record.getUnbanDate() : "Unknown";
            String unbanBy = record.getUnbanBy() != null ? record.getUnbanBy() : "Unknown";
            boolean permanent = record.isPermanent();

            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            assert meta != null;

            meta.setDisplayName("§cBan #" + (i + 1));

            if (permanent) {
                meta.setLore(List.of(
                        "§7Reason: §f" + reason,
                        "§7By: §f" + by,
                        "§7Date: §f" + date,
                        "§7Duration: §f" + duration,
                        "§7Unban date: §f" + unbanDate,
                        "§7Unban by: §f" + unbanBy,
                        "§7Ban id: §f" + id,
                        "§c§lPERMANENT – Right click possible"
                ));
            } else {
                meta.setLore(List.of(
                        "§7Reason: §f" + reason,
                        "§7By: §f" + by,
                        "§7Date: §f" + date,
                        "§7Duration: §f" + duration,
                        "§7Unban date: §f" + unbanDate,
                        "§7Unban by: §f" + unbanBy,
                        "§7Ban id: §f" + id,
                        "§7§oRight click: Make permanent",
                        "§e§oLeft click: Delete"
                ));
            }

            paper.setItemMeta(meta);
            int slot = GuiConstants.ALLOWED_SLOTS[i - startIndex];
            inv.setItem(slot, paper);
        }

        // Spieler-Kopf (Slot 53)
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName("§e" + target.getName());
        skullMeta.setLore(List.of(ChatColor.AQUA + "Total Bans: " + bans.size()));
        skull.setItemMeta(skullMeta);
        inv.setItem(53, skull);

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

        ItemStack sortBook = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = sortBook.getItemMeta();
        assert bookMeta != null;
        bookMeta.setDisplayName("§eSort: " + (newestFirst ? "§aNewest → Oldest" : "§cOldest → Newest"));
        bookMeta.setLore(List.of("§7Click to toggle sort order"));
        sortBook.setItemMeta(bookMeta);
        inv.setItem(17, sortBook);

        viewer.openInventory(inv);
        viewer.setMetadata("ban_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("ban_page", new FixedMetadataValue(plugin, page));
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