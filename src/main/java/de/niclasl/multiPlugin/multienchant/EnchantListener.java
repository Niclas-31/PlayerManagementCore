package de.niclasl.multiPlugin.multienchant;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public record EnchantListener(MultiPlugin plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        Inventory inv = e.getInventory();
        InventoryHolder holder = inv.getHolder();

        // Stelle sicher, dass wir wirklich im EnchantGUI sind
        if (!(holder instanceof EnchantGUI.EnchantSelectionHolder)) return;

        e.setCancelled(true);

        int slot = e.getRawSlot();
        ItemStack clicked = e.getCurrentItem();

        // ==========================
        // BUTTON: BACK (slot 45)
        // ==========================
        if (slot == 45) {
            if (p.hasPermission("manage.player")) {

                OfflinePlayer targetOffline = getTarget(p);

                if (targetOffline != null) {
                    Player target = targetOffline.getPlayer();

                    if (target != null && target.isOnline()) {
                        plugin.getWatchGuiManager().open2(p, target);
                    }
                } else {
                    p.sendMessage("§cError: Target player not found.");
                }

            }
            return;
        }

        // ==========================
        // ENCHANT-CLICK
        // ==========================
        if (clicked == null || !clicked.hasItemMeta()) return;

        String key = Objects.requireNonNull(clicked.getItemMeta()).getPersistentDataContainer().get(
                new NamespacedKey(plugin, "ench_key"),
                PersistentDataType.STRING
        );
        if (key == null) return; // kein enchant-item

        Enchantment chosen = Enchantment.getByKey(NamespacedKey.minecraft(key));

        if (chosen == null) {
            p.sendMessage("§cError: Enchantment not found.");
            return;
        }

        OfflinePlayer target = getTarget(p);

        if (target == null) {
            p.sendMessage("§cError: Target not found.");
            return;
        }

        plugin.getLevelGUI().open(p, target, chosen, 1);
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();

        // Slot 10 ist das NameTag-Item (laut StatsGui)
        ItemStack nameTag = inv.getItem(49);
        if (nameTag == null || !nameTag.hasItemMeta()) return null;

        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;

        String displayName = meta.getDisplayName();
        // displayName hat Format: "§aSpielername", wir entfernen den §a-Code
        String playerName = ChatColor.stripColor(displayName);

        if (playerName.isEmpty()) return null;

        return Bukkit.getOfflinePlayer(playerName);
    }
}