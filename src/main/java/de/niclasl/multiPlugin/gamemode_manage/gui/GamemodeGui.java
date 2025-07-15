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

    public static void open(Player klicker, Player target) {
        if (plugin == null) {
            klicker.sendMessage("§cGamemodeGui was not initialized.");
            return;
        }

        if (target == null) {
            klicker.sendMessage("§cTarget player not found.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 18, ChatColor.GOLD + "Gamemode for " + target.getName());

        inv.setItem(1, createItem(Material.IRON_SWORD, "§aSurvival"));
        inv.setItem(3, createItem(Material.GRASS_BLOCK, "§bCreative"));
        inv.setItem(5, createItem(Material.MAP, "§eAdventure"));
        inv.setItem(7, createItem(Material.ENDER_EYE, "§7Spectator"));

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta meta = back.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§cBack");
        back.setItemMeta(meta);
        inv.setItem(9, back);

        inv.setItem(13, createSkull(target.getName()));

        klicker.openInventory(inv);
        klicker.setMetadata("gm_target", new org.bukkit.metadata.FixedMetadataValue(plugin, target.getName()));
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