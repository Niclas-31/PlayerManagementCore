package de.niclasl.multiPlugin.gamemode_manage.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GamemodeGui {

    public static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        GamemodeGui.plugin = plugin;
    }

    public static void open(Player player, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Gamemode for " + target.getName());

        // Rand
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{0,1,2,3,4,5,6,7,8,9,17,19,20,21,23,24,25,26}) {
            inv.setItem(i, glass);
        }

        inv.setItem(10, createItem(Material.IRON_SWORD, "§aSurvival"));
        inv.setItem(12, createItem(Material.GRASS_BLOCK, "§bCreative"));
        inv.setItem(14, createItem(Material.MAP, "§eAdventure"));
        inv.setItem(16, createItem(Material.ENDER_EYE, "§7Spectator"));

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta meta = back.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§cBack");
        back.setItemMeta(meta);
        inv.setItem(18, back);

        inv.setItem(22, createSkull(target.getName()));

        player.openInventory(inv);
        player.setMetadata("gm_target", new org.bukkit.metadata.FixedMetadataValue(plugin, target.getName()));
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createSkull(String playerName) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        meta.setDisplayName("§e" + playerName);
        skull.setItemMeta(meta);
        return skull;
    }
}