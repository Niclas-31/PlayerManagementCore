package de.niclasl.multiPlugin.teleport.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class DimensionGui {

    public static void open(Player player, OfflinePlayer target) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8Teleport to Dimension");

        // Rand
        ItemStack glass = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        assert glassMeta != null;
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < 27; i++) {
            if (i == 22 || i == 26) continue;
            gui.setItem(i, glass);
        }


        // Kopf ganz rechts unten (Slot 53)
        int headSlot = 22;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§e" + target.getName());
        skull.setItemMeta(meta);
        gui.setItem(headSlot, skull);

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName(ChatColor.WHITE + "Back");
        back.setItemMeta(backMeta);
        gui.setItem(26, back);

        // Overworld
        ItemStack overworld = createItem(Material.GRASS_BLOCK, "§aOverworld", "§7Teleport the player to the Overworld.");
        gui.setItem(11, overworld);

        // Nether
        ItemStack nether = createItem(Material.NETHERRACK, "§cNether", "§7Teleport the player to the Nether.");
        gui.setItem(13, nether);

        // End
        ItemStack end = createItem(Material.END_STONE, "§5End", "§7Teleport the player to the End.");
        gui.setItem(15, end);

        player.openInventory(gui);
    }

    private static ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
