package de.niclasl.multiPlugin.ban_system.gui;

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

import java.util.List;

public class BanHistoryGui {

    private static MultiPlugin plugin;

    public BanHistoryGui(MultiPlugin plugin) {
        BanHistoryGui.plugin = plugin;
    }

    public static void open(Player viewer, OfflinePlayer target, int page) {
        List<BanRecord> bans = BanHistoryManager.getBanHistory(target.getUniqueId());

        int[] allowedSlots = {
                0,1,2,3,4,5,6,7,
                9,10,11,12,13,14,15,16,
                18,19,20,21,22,23,24,25,
                27,28,29,30,31,32,33,34,
                36,37,38,39,40,41,42,43,
                45,46,47,48,49,50,51,52
        };

        int bansPerPage = allowedSlots.length;
        int totalPages = (int) Math.ceil(bans.size() / (double) bansPerPage);
        if (totalPages == 0) totalPages = 1;

        page = Math.max(1, Math.min(page, totalPages));

        Inventory inv = Bukkit.createInventory(null, 54, "§8Bans from " + target.getName() + " §7(" + page + "/" + totalPages + ")");

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
            int slot = allowedSlots[i - startIndex];
            inv.setItem(slot, paper);
        }

        // Spieler-Kopf (Slot 53)
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName("§e" + target.getName());
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

        viewer.openInventory(inv);
        viewer.setMetadata("ban_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("ban_page", new FixedMetadataValue(plugin, page));
    }
}