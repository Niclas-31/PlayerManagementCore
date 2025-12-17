package de.niclasl.multiPlugin.audit.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.audit.gui.AuditGui;
import de.niclasl.multiPlugin.audit.model.AuditEntry;
import de.niclasl.multiPlugin.audit.storage.YamlAuditStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class AuditGuiListener implements Listener {

    private final MultiPlugin plugin;

    public AuditGuiListener(MultiPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§8Audit from ")) return;

        e.setCancelled(true);

        if (!player.hasMetadata("audit_target") || !player.hasMetadata("audit_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("audit_target").getFirst().asString());
        int page = player.getMetadata("audit_page").getFirst().asInt();

        int slot = e.getSlot();


        if (slot == 26) {
            OfflinePlayer target = getTarget(player);
            if (target != null) {
                plugin.getWatchGuiManager().open2(player, (Player) target);
            } else {
                player.sendMessage("§cError: Target player not found.");
                player.closeInventory();
            }
        }

        if (slot == 35) {
            if (page > 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                plugin.getAuditGui().open(player, target, page - 1);
            }
            return;
        }

        if (slot == 44) {
            List<AuditEntry> entries = YamlAuditStorage.getEntries(targetUUID);
            int entriesPerPage = GuiConstants.ALLOWED_SLOTS.length;
            int totalPages = (int) Math.ceil(entries.size() / (double) entriesPerPage);
            if (page < totalPages) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                plugin.getAuditGui().open(player, target, page + 1);
            }
            return;
        }

        if (slot == 17) {
            AuditGui.nextFilter(player);
            OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(player.getMetadata("audit_target").getFirst().asString()));
            plugin.getAuditGui().open(player, target, 1);
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();

        ItemStack nameTag = inv.getItem(53);
        if (nameTag == null || !nameTag.hasItemMeta()) return null;

        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;

        String displayName = meta.getDisplayName();

        String playerName = ChatColor.stripColor(displayName);

        if (playerName.isEmpty()) return null;

        return Bukkit.getOfflinePlayer(playerName);
    }
}