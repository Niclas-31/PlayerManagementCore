package de.niclasl.multiPlugin.multienchant;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public record EnchantGUI(MultiPlugin plugin) {

    public void open(Player p, OfflinePlayer target) {
        Inventory inv = Bukkit.createInventory(new EnchantSelectionHolder(), 54, "§6Choose an Enchantment");

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{46,47,48,50,51,52,53}) inv.setItem(i, glass);

        for (Enchantment ench : Enchantment.values()) {
            ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;

            meta.setDisplayName("§a" + ench.getKey().getKey());
            meta.setLore(Arrays.asList(
                    "§7Click to select level",
                    "§7MaxLevel: " + ench.getMaxLevel()
            ));

            // speichere den echten Enchantment Key
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "ench_key"),
                    PersistentDataType.STRING,
                    ench.getKey().getKey()
            );

            item.setItemMeta(meta);
            inv.addItem(item);
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§c⬅ Back");
        back.setItemMeta(backMeta);
        inv.setItem(45, back);

        int headSlot = 49;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§b" + target.getName());
        skull.setItemMeta(meta);
        inv.setItem(headSlot, skull);

        p.openInventory(inv);
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

    public static class EnchantSelectionHolder implements InventoryHolder {
        @Override
            public Inventory getInventory() {
                return null;
            }
        }
}
