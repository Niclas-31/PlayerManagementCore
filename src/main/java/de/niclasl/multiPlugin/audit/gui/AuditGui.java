package de.niclasl.multiPlugin.audit.gui;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.audit.model.AuditEntry;
import de.niclasl.multiPlugin.audit.model.AuditType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class AuditGui {

    private static MultiPlugin plugin;

    private static final Map<UUID, AuditType> playerFilter = new HashMap<>();

    public AuditGui(MultiPlugin plugin) {
        AuditGui.plugin = plugin;
    }

    public void open(Player viewer, OfflinePlayer target, int page) {

        List<AuditEntry> entries =
                new ArrayList<>(plugin.getAuditManager().getStorage().getByTarget(target.getUniqueId()));

        AuditType filter = playerFilter.get(viewer.getUniqueId());
        if (filter != null) {
            entries.removeIf(e -> e.getType() != filter);
        }

        entries.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

        int[] allowedSlots = GuiConstants.ALLOWED_SLOTS;
        int perPage = allowedSlots.length;
        int totalPages = (int) Math.ceil(entries.size() / (double) perPage);
        if (totalPages == 0) totalPages = 1;
        page = Math.min(Math.max(page, 1), totalPages);

        Inventory inv = Bukkit.createInventory(
                null,
                54,
                "§8Audit from " + target.getName() + " §7(" + page + "/" + totalPages + ")"
        );

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        inv.setItem(8, glass);
        if (page == 1) inv.setItem(35, glass);
        if (page == totalPages) inv.setItem(44, glass);

        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, entries.size());

        for (int i = start; i < end; i++) {
            AuditEntry e = entries.get(i);
            int slot = allowedSlots[i - start];

            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();

            assert meta != null;
            meta.setDisplayName("§e" + e.getType().getDisplayName() + " §7• §f" + e.getAction().getDisplayName());

            meta.setLore(List.of(
                    "§7By: §f" + (e.getExecutor() == null
                            ? "Console"
                            : Bukkit.getOfflinePlayer(e.getExecutor()).getName()),
                    "§7Reason: §f" + e.getReason(),
                    "§7Date: §f" + new Date(e.getTimestamp())
            ));

            paper.setItemMeta(meta);
            inv.setItem(slot, paper);
        }

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName("§e" + target.getName());
        skullMeta.setLore(List.of(
                "§7Entries: §f" + entries.size(),
                "§7Filter: §f" + (filter == null ? "All" : filter.getDisplayName())
        ));
        skull.setItemMeta(skullMeta);
        inv.setItem(53, skull);

        inv.setItem(26, createItem(Material.BARRIER, "§fBack"));

        if (page > 1)
            inv.setItem(35, createItem(Material.ARROW, "§aPrevious page"));
        if (page < totalPages)
            inv.setItem(44, createItem(Material.ARROW, "§aNext page"));

        inv.setItem(17, createItem(
                Material.BOOK,
                "§eFilter",
                "§7Current: §f" + (filter == null ? "All" : filter.getDisplayName()),
                "§7Click to change"
        ));

        viewer.openInventory(inv);

        viewer.setMetadata("audit_target",
                new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("audit_page",
                new FixedMetadataValue(plugin, page));
    }

    public static void nextFilter(Player player) {
        AuditType current = playerFilter.get(player.getUniqueId());
        AuditType[] values = AuditType.values();

        if (current == null) {
            playerFilter.put(player.getUniqueId(), values[0]);
            return;
        }

        int next = current.ordinal() + 1;
        playerFilter.put(player.getUniqueId(),
                next >= values.length ? null : values[next]);
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public int getTotalPages(OfflinePlayer target) {
        List<AuditEntry> entries = plugin.getAuditManager().getStorage().getByTarget(target.getUniqueId());

        int entriesPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(entries.size() / (double) entriesPerPage);
        if (totalPages == 0) totalPages = 1;
        return totalPages;
    }
}