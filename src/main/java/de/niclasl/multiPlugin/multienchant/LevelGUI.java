package de.niclasl.multiPlugin.multienchant;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

public record LevelGUI(MultiPlugin plugin) {

    public void open(Player viewer, OfflinePlayer target, Enchantment ench, int page) {
        int maxLevel = viewer.isOp() ? 255 : ench.getMaxLevel();
        int itemsPerPage = GuiConstants.ALLOWED_SLOTS_1.length;
        int totalPages = (int) Math.ceil((double) maxLevel / itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        // Page korrigieren (1-basiert)
        page = Math.min(Math.max(page, 1), totalPages);

        Inventory inv = Bukkit.createInventory(new LevelHolder(target, ench, page), 54,
                "§aSelect Level (" + ench.getKey().getKey() + ") §7(" + page + "/" + totalPages + ")");

        int startLevel = (page - 1) * itemsPerPage + 1;
        int endLevel = Math.min(startLevel + itemsPerPage - 1, maxLevel);

        for (int i = startLevel; i <= endLevel; i++) {
            ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName("§aLevel " + i);
            item.setItemMeta(meta);

            int slotIndex = GuiConstants.ALLOWED_SLOTS_1[i - startLevel];
            inv.setItem(slotIndex, item);
        }

        // Player Head
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName("§b" + target.getName());
        skull.setItemMeta(skullMeta);
        inv.setItem(48, skull);

        // Confirm Button
        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        assert confirmMeta != null;
        confirmMeta.setDisplayName("§aConfirm");
        confirm.setItemMeta(confirmMeta);
        inv.setItem(49, confirm);

        // Previous Page
        if (page > 1) {
            ItemStack back = new ItemStack(Material.ARROW);
            ItemMeta backMeta = back.getItemMeta();
            assert backMeta != null;
            backMeta.setDisplayName("§cPrevious Page");
            back.setItemMeta(backMeta);
            inv.setItem(45, back);
        }

        // Next Page
        if (page < totalPages) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = next.getItemMeta();
            assert nextMeta != null;
            nextMeta.setDisplayName("§aNext Page");
            next.setItemMeta(nextMeta);
            inv.setItem(53, next);
        }

        viewer.openInventory(inv);

        // Metadata für Listener
        viewer.setMetadata("level_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
        viewer.setMetadata("level_page", new FixedMetadataValue(plugin, page));
        viewer.setMetadata("level_ench", new FixedMetadataValue(plugin, ench.getKey().getKey()));
    }

    public record LevelHolder(OfflinePlayer target, Enchantment ench, int page) implements InventoryHolder {

        public OfflinePlayer getTarget() { return target; }
        public Enchantment getEnchantment() { return ench; }
        public int getPage() { return page; }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}