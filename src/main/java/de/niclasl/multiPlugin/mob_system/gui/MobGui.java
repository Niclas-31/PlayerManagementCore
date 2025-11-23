package de.niclasl.multiPlugin.mob_system.gui;

import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import de.niclasl.multiPlugin.mob_system.model.MobSpawnRequest;
import org.bukkit.Bukkit;
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

    public static final Character[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars()
            .mapToObj(c -> (char) c)
            .toArray(Character[]::new);

    public static HashMap<UUID, Integer> playerFilterIndex = new HashMap<>();
    public static MultiPlugin plugin;

    public MobGui(MultiPlugin plugin) {
        MobGui.plugin = plugin;
    }

    public static void open(Player viewer, OfflinePlayer target, int page) {

        // Filter laden
        int filterIndex = playerFilterIndex.getOrDefault(viewer.getUniqueId(), -1);
        Character filterChar = (filterIndex == -1 ? null : ALPHABET[filterIndex]);

        // Requests holen
        List<MobSpawnRequest> requests = MobManager.getRequests(target.getUniqueId());
        List<EntityType> allMobs = requests.stream()
                .map(MobSpawnRequest::getEntityType)
                .collect(Collectors.toList());

        // Filter anwenden
        if (filterChar != null) {
            allMobs = allMobs.stream()
                    .filter(type -> type.name().startsWith(filterChar.toString()))
                    .toList();
        }

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

        Inventory inv = Bukkit.createInventory(
                null,
                54,
                "§8Spawn Mobs for " + target.getName() + " §7(" + page + "/" + totalPages + ")"
        );

        // Rand
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        inv.setItem(8, glass);

        int startIndex = (page - 1) * mobsPerPage;

        for (int i = 0; i < mobsPerPage; i++) {
            int index = startIndex + i;
            if (index >= allMobs.size()) continue;

            int slot = allowedSlots[i];
            EntityType type = allMobs.get(index);

            String displayName = Arrays.stream(type.name().split("_"))
                    .map(w -> w.charAt(0) + w.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));

            Material egg = Material.matchMaterial(type.name() + "_SPAWN_EGG");
            ItemStack item = new ItemStack(egg != null ? egg : Material.EGG);

            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§a" + displayName);
            meta.setLore(List.of(
                    "§7Left-click to spawn",
                    "§eRight-click: Enter number"
            ));
            item.setItemMeta(meta);

            inv.setItem(slot, item);
        }

        // Kopf
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        meta.setLore(List.of("§bTotal: " + requests.size()));
        skull.setItemMeta(meta);
        inv.setItem(53, skull);

        // Back
        inv.setItem(26, createItem(Material.BARRIER, "§cBack"));

        // Page Controls
        if (page > 1)
            inv.setItem(35, createItem(Material.ARROW, "§aPrevious Page"));

        if (page < totalPages)
            inv.setItem(44, createItem(Material.ARROW, "§aNext Page"));

        // Filter-Buch
        String filterName = (filterChar == null ? "ALL" : filterChar.toString());
        inv.setItem(17, createItem(
                Material.BOOK,
                "§eFilter: §6" + filterName,
                "§7Left: Next letter",
                "§7Right: Previous letter"
        ));

        // Metadata setzen
        viewer.openInventory(inv);
        viewer.setMetadata("mob_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("mob_page", new FixedMetadataValue(plugin, page));
    }

    private static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta m = item.getItemMeta();
        assert m != null;
        m.setDisplayName(name);
        if (lore.length > 0) m.setLore(List.of(lore));
        item.setItemMeta(m);
        return item;
    }
}