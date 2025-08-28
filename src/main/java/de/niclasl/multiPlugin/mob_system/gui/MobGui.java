package de.niclasl.multiPlugin.mob_system.gui;

import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import de.niclasl.multiPlugin.mob_system.model.MobSpawnRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import de.niclasl.multiPlugin.MultiPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class MobGui {

    public static MultiPlugin plugin;

    public MobGui(MultiPlugin plugin) {
        MobGui.plugin = plugin;
    }

    public static void open(Player viewer, OfflinePlayer target, int page) {
        List<MobSpawnRequest> requests = MobManager.getRequests(target.getUniqueId());
        List<EntityType> allMobs = requests.stream()
                .map(MobSpawnRequest::getEntityType)
                .toList();

        int[] allowedSlots = {
                0,1,2,3,4,5,6,7,
                9,10,11,12,13,14,15,16,
                18,19,20,21,22,23,24,25,
                27,28,29,30,31,32,33,34,
                36,37,38,39,40,41,42,43,
                45,46,47,48,49,50,51,52
        };

        int mobsPerPage = allowedSlots.length;
        int totalPages = (int) Math.ceil(allMobs.size() / (double) mobsPerPage);
        if (totalPages == 0) totalPages = 1;
        page = Math.min(Math.max(page, 1), totalPages);

        Inventory inv = Bukkit.createInventory(null, 54, "§8Spawn Mobs for " + target.getName() + " §7(" + page + "/" + totalPages + ")");

        // Rand
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

        int startIndex = (page - 1) * mobsPerPage;

        for (int i = 0; i < mobsPerPage; i++) {
            int index = startIndex + i;
            if (index >= allMobs.size()) continue;

            int slot = allowedSlots[i];
            EntityType type = allMobs.get(index);
            String displayName = Arrays.stream(type.name().split("_"))
                    .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
            Material eggMat = Material.matchMaterial(type.name() + "_SPAWN_EGG");

            ItemStack item;
            if (eggMat != null) {
                item = new ItemStack(eggMat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§a" + displayName);
                    meta.setLore(List.of("§7Left-click to spawn", "§eRight-click: Enter number"));
                    item.setItemMeta(meta);
                }
            } else {
                item = new ItemStack(Material.EGG);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName("§c" + displayName);
                    meta.setLore(List.of("§7Left-click to spawn", "§eRight-click: Enter number", "§7No spawn egg available"));
                    item.setItemMeta(meta);
                }
            }

            inv.setItem(slot, item);
        }

        // Kopf-Icon
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        meta.setLore(List.of(ChatColor.AQUA + "Total Mobs: " + requests.size()));
        skull.setItemMeta(meta);

        inv.setItem(53, skull);

        // Zurück-Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);
        inv.setItem(26, back);

        // Navigation
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
        viewer.setMetadata("mob_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("mob_page", new FixedMetadataValue(plugin, page));
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