package de.niclasl.multiPlugin.stats.gui;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

public class MinedBlocksGui {

    private static final int[] allowedSlots = {
            0, 1, 2, 3, 4, 5, 6, 7,
            9, 10, 11, 12, 13, 14, 15, 16,
            18, 19, 20, 21, 22, 23, 24, 25,
            27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43,
            45, 46, 47, 48, 49, 50, 51, 52
    };

    public static MultiPlugin plugin;

    public MinedBlocksGui(MultiPlugin plugin) {
        MinedBlocksGui.plugin = plugin;
    }

    public static void open(Player viewer, OfflinePlayer target, int page) {
        List<Material> minedBlocks = new ArrayList<>();

        // Alle Blöcke mit >0 Statistiken sammeln
        for (Material material : Material.values()) {
            try {
                if (target.getStatistic(Statistic.MINE_BLOCK, material) > 0) {
                    minedBlocks.add(material);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        minedBlocks.sort(Comparator.comparingInt((Material m) ->
                -target.getStatistic(Statistic.MINE_BLOCK, m) // sortieren nach Häufigkeit (absteigend)
        ));

        int blocksPerPage = allowedSlots.length;
        int totalPages = (int) Math.ceil(minedBlocks.size() / (double) blocksPerPage);
        if (totalPages == 0) totalPages = 1;
        page = Math.min(Math.max(page, 1), totalPages);

        Inventory gui = Bukkit.createInventory(null, 54, "§9Mined Blocks: " + target.getName() + " §7(" + page + "/" + totalPages + ")");

        // Rand
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{8,17}) {
            gui.setItem(i, glass);
        }

        if (totalPages == 1 || page == 1) {
            ItemStack glass1 = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
            for (int i : new int[]{35}) {
                gui.setItem(i, glass1);
            }
        }

        if (totalPages == 1 || totalPages == page) {
            ItemStack glass2 = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
            for (int i : new int[]{44}) {
                gui.setItem(i, glass2);
            }
        }

        int startIndex = (page - 1) * blocksPerPage;
        int endIndex = Math.min(startIndex + blocksPerPage, minedBlocks.size());

        int slotIndex = 0;

        for (int i = startIndex; i < endIndex; i++) {
            Material material = minedBlocks.get(i);
            int count = target.getStatistic(Statistic.MINE_BLOCK, material);

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            meta.setDisplayName("§a" + formatMaterialName(material));
            meta.setLore(List.of("§7Mined: §f" + count));
            item.setItemMeta(meta);

            gui.setItem(allowedSlots[slotIndex++], item);
        }

        // Kopf-Icon
        int headSlot = 53;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        skull.setItemMeta(meta);
        gui.setItem(headSlot, skull);

        // Zurück-Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName("§cBack");
            back.setItemMeta(backMeta);
            gui.setItem(26, back);
        }

        if (page > 1) {
            ItemStack backArrow = new ItemStack(Material.ARROW);
            ItemMeta backArrowMeta = backArrow.getItemMeta();
            if (backArrowMeta != null) {
                backArrowMeta.setDisplayName("§aPrevious page");
                backArrow.setItemMeta(backArrowMeta);
                gui.setItem(35, backArrow);
            }
        }

        if (page < totalPages) {
            ItemStack nextArrow = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextArrow.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName("§aNext page");
                nextArrow.setItemMeta(nextMeta);
                gui.setItem(44, nextArrow);
            }
        }

        viewer.openInventory(gui);
        viewer.setMetadata("block_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("block_page", new FixedMetadataValue(plugin, page));
    }

    private static String formatMaterialName(Material material) {
        return Arrays.stream(material.name().toLowerCase().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
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