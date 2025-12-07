package de.niclasl.multiPlugin.teleport.gui;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.teleport.manager.TeleportManager;
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

public record DimensionGui(MultiPlugin plugin) {

    public void open(Player player, OfflinePlayer target, int page) {
        List<String> dimensions = TeleportManager.getAllDimensions();

        // Slots, die für Warnungen benutzt werden (48 Slots verteilt auf 6 Reihen, je 8 Slots, ohne die jeweils 9., 17., 26., 35., 44., 53. Slots)
        int[] allowedSlots = {
                0,1,2,3,4,5,6,7,
                9,10,11,12,13,14,15,16,
                18,19,20,21,22,23,24,25,
                27,28,29,30,31,32,33,34,
                36,37,38,39,40,41,42,43,
                45,46,47,48,49,50,51,52
        };

        int dimensionsPerPage = allowedSlots.length; // 48
        int totalPages = (int) Math.ceil(dimensions.size() / (double) dimensionsPerPage);
        if(totalPages == 0) totalPages = 1;

        page = Math.max(1, Math.min(page, totalPages));
        Inventory inv = Bukkit.createInventory(null, 54, "§8Teleport to Dimension §7(" + page + "/" + totalPages + ")");

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{8,17}) {
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

        int startIndex = (page - 1) * dimensionsPerPage;
        int endIndex = Math.min(startIndex + dimensionsPerPage, dimensions.size());

        for (int i = startIndex; i < endIndex; i++) {
            String dim = dimensions.get(i);
            int slot = allowedSlots[i - startIndex];

            Material mat = getMaterialForDimension(dim);
            String display = capitalize(dim);

            // NEUES ItemStack pro Durchlauf!
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GREEN + display);
                meta.setLore(List.of("§7Dimension: " + dim));
                item.setItemMeta(meta);
            }

            inv.setItem(slot, item);
        }

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

        int headSlot = 53;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        skull.setItemMeta(meta);
        inv.setItem(headSlot, skull);

        player.openInventory(inv);
        player.setMetadata("dimension_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        player.setMetadata("dimension_page", new FixedMetadataValue(plugin, page));
    }

    private static Material getMaterialForDimension(String dim) {
        return switch (dim.toLowerCase()) {
            case "overworld" -> Material.GRASS_BLOCK;
            case "nether" -> Material.NETHERRACK;
            case "end" -> Material.END_STONE;
            default -> Material.COMPASS;
        };
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}