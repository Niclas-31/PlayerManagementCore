package de.niclasl.multiPlugin.multienchant;

import de.niclasl.multiPlugin.GuiConstants;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;
import java.util.UUID;

public record LevelListener(MultiPlugin plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player viewer)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§aSelect Level ")) return; // Prüfen, dass es LevelGUI ist
        e.setCancelled(true);

        if (!viewer.hasMetadata("level_target") || !viewer.hasMetadata("level_page") || !viewer.hasMetadata("level_ench")) return;

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(viewer.getMetadata("level_target").getFirst().asString()));
        int page = viewer.getMetadata("level_page").getFirst().asInt();
        Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(viewer.getMetadata("level_ench").getFirst().asString()));

        int slot = e.getSlot();
        int totalLevels;
        if (viewer.isOp()) {
            totalLevels = 255;
        } else {
            assert ench != null;
            totalLevels = ench.getMaxLevel();
        }
        int itemsPerPage = GuiConstants.ALLOWED_SLOTS_1.length;
        int totalPages = (int) Math.ceil(totalLevels / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        // Page korrigieren
        page = Math.min(Math.max(page, 1), totalPages);

        // ==========================
        // Navigation
        // ==========================
        if (slot == 45 && page > 1) { // Previous Page
            plugin.getLevelGUI().open(viewer, target, ench, page - 1);
            return;
        }
        if (slot == 53 && page < totalPages) { // Next Page
            plugin.getLevelGUI().open(viewer, target, ench, page + 1);
            return;
        }

        // ==========================
        // Level auswählen
        // ==========================
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        String name = ChatColor.stripColor(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName());

        if (name.startsWith("Level ")) {
            int level = Integer.parseInt(name.replace("Level ", ""));
            viewer.setMetadata("chosen_level", new FixedMetadataValue(plugin, level));
            int xpCost = viewer.isOp() ? 0 : getXpCost(level); // Funktion wie vorher
            viewer.sendMessage("§aLevel §e" + level + "§a selected! §7(Cost: §e" + xpCost + " XP§7)");
            return;
        }

        // ==========================
        // Confirm
        // ==========================
        if (name.equalsIgnoreCase("Confirm")) {
            if (!viewer.hasMetadata("chosen_level")) {
                viewer.sendMessage("§cPlease select a level first!");
                return;
            }

            int level = viewer.getMetadata("chosen_level").getFirst().asInt();
            int xpCost = viewer.isOp() ? 0 : getXpCost(level);

            // Prüfen XP
            if (!viewer.isOp() && viewer.getLevel() < xpCost) {
                viewer.sendMessage("§cYou need §e" + xpCost + " Levels §cto apply this enchantment!");
                return;
            }

            if (!viewer.isOp()) viewer.setLevel(viewer.getLevel() - xpCost);

            if (target.getPlayer() != null && target.getPlayer().isOnline()) {
                assert ench != null;
                target.getPlayer().getInventory().getItemInMainHand().addUnsafeEnchantment(ench, level);
                viewer.sendMessage("§aApplied §e" + ench.getKey().getKey() + "§a level §e" + level + " §ato " + target.getName());
            }

            viewer.closeInventory();
        }
    }

    private int getXpCost(int level) {
        return switch (level) {
            case 1 -> 5;
            case 2 -> 12;
            case 3 -> 20;
            case 4 -> 40;
            case 5 -> 60;
            default -> level * 15; // alles ab Level 6: Level * 15 XP
        };
    }
}