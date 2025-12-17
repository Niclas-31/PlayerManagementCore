package de.niclasl.multiPlugin.portal.gui;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.portal.PortalType;
import de.niclasl.multiPlugin.portal.manager.PortalConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public record PortalGui(MultiPlugin plugin) implements Listener {

    private static final String TITLE = "Portal Settings";

    public static void openFor(Player player) {
        Inventory inv = Bukkit.createInventory(null, 18, TITLE);

        int slot = 0;
        for (PortalType t : PortalType.values()) {
            if (slot >= 11) break;
            boolean enabled = PortalConfigManager.isPortalEnabled(t);
            ItemStack item = new ItemStack(enabled ? Material.GREEN_WOOL : Material.RED_WOOL);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setDisplayName((enabled ? ChatColor.GREEN : ChatColor.RED) + t.name());
            meta.setLore(List.of("Click to toggle"));
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent ev) {
        if (!(ev.getWhoClicked() instanceof Player p)) return;
        ev.getView().getTitle();
        if (!ev.getView().getTitle().equals(TITLE)) return;
        ev.setCancelled(true);
        ItemStack clicked = ev.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String name = Objects.requireNonNull(clicked.getItemMeta()).getDisplayName();
        name = ChatColor.stripColor(name);

        try {
            PortalType type = PortalType.valueOf(name);
            boolean enabled = PortalConfigManager.isPortalEnabled(type);
            plugin.getPortalConfigManager().setPortalEnabled(type, !enabled);
            p.sendMessage("Set " + type.name() + " = " + !enabled);
            p.closeInventory();
        } catch (IllegalArgumentException ignored) {}
    }
}
