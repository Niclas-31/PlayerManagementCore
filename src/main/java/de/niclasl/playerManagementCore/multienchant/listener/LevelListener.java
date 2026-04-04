package de.niclasl.playerManagementCore.multienchant.listener;

import de.niclasl.playerManagementCore.GuiConstants;
import de.niclasl.playerManagementCore.PlayerManagementCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;
import java.util.UUID;

public record LevelListener(PlayerManagementCore plugin) implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player viewer)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§aSelect Level ")) return;

        e.setCancelled(true);

        if (!viewer.hasMetadata("level_target")
                || !viewer.hasMetadata("level_page")
                || !viewer.hasMetadata("level_ench")) return;

        Player target = Bukkit.getPlayer(UUID.fromString(
                viewer.getMetadata("level_target").getFirst().asString()
        ));

        if (target == null) {
            viewer.sendMessage("§cTarget player is offline!");
            return;
        }

        int page = viewer.getMetadata("level_page").getFirst().asInt();

        Enchantment ench = Enchantment.getByKey(
                NamespacedKey.minecraft(
                        viewer.getMetadata("level_ench").getFirst().asString()
                )
        );

        if (ench == null) {
            viewer.sendMessage("§cInvalid enchantment!");
            return;
        }

        int slot = e.getSlot();

        int totalLevels = viewer.isOp() ? 255 : ench.getMaxLevel();

        int itemsPerPage = GuiConstants.ALLOWED_SLOTS_1.length;
        int totalPages = (int) Math.ceil(totalLevels / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1;

        page = Math.clamp(page, 1, totalPages);

        if (slot == 45 && page > 1) {
            plugin.getLevelGUI().open(viewer, target, ench, page - 1);
            return;
        }

        if (slot == 53 && page < totalPages) {
            plugin.getLevelGUI().open(viewer, target, ench, page + 1);
            return;
        }

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String name = ChatColor.stripColor(
                Objects.requireNonNull(clicked.getItemMeta()).getDisplayName()
        );

        if (name.startsWith("Level ")) {
            int level = Integer.parseInt(name.replace("Level ", ""));

            viewer.setMetadata("chosen_level", new FixedMetadataValue(plugin, level));

            int xpCost = target.isOp() ? 0 : getXpCost(level);

            viewer.sendMessage("§aLevel §e" + level + "§a selected! §7(Cost: §e" + xpCost + " XP§7)");
            return;
        }

        if (name.equalsIgnoreCase("Confirm")) {

            if (!viewer.hasMetadata("chosen_level")) {
                viewer.sendMessage("§cPlease select a level first!");
                return;
            }

            int level = viewer.getMetadata("chosen_level").getFirst().asInt();
            int xpCost = target.isOp() ? 0 : getXpCost(level);

            if (!target.isOp() && target.getLevel() < xpCost) {
                viewer.sendMessage("§cTarget needs §e" + xpCost + " Levels §cto apply this enchantment!");
                return;
            }

            if (!target.isOp()) {
                target.setLevel(target.getLevel() - xpCost);
            }

            target.getInventory()
                    .getItemInMainHand()
                    .addUnsafeEnchantment(ench, level);

            viewer.sendMessage("§aApplied §e"
                    + ench.getKeyOrThrow().getKey()
                    + "§a level §e" + level
                    + " §ato " + target.getName());

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
            default -> level * 15;
        };
    }
}