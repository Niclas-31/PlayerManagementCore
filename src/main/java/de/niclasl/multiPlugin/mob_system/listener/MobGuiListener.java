package de.niclasl.multiPlugin.mob_system.listener;

import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import de.niclasl.multiPlugin.mob_system.MobCategories;
import de.niclasl.multiPlugin.mob_system.gui.MobGui;
import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import de.niclasl.multiPlugin.mob_system.model.MobSpawnRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class MobGuiListener implements Listener {
    private static MobManager mobManager;
    private static MobGui mobGui;
    private static MultiPlugin plugin;

    public MobGuiListener(MobManager mobManager, MobGui mobGui, MultiPlugin plugin) {
        MobGuiListener.mobManager = mobManager;
        MobGuiListener.mobGui = mobGui;
        MobGuiListener.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() == null) return;

        String title = e.getView().getTitle();
        if (!title.startsWith("§8Spawn Mobs for ")) return;

        e.setCancelled(true);

        if (!player.hasMetadata("mob_target") || !player.hasMetadata("mob_page")) return;

        UUID targetUUID = UUID.fromString(player.getMetadata("mob_target").get(0).asString());
        int page = player.getMetadata("mob_page").get(0).asInt();

        int slot = e.getSlot();

        if (slot == 26) { // zurück zur Übersicht
            OfflinePlayer target = getTarget(player);
            if (target instanceof Player onlineTarget)
                WatchGuiManager.openPage2(player, onlineTarget);
            else {
                player.sendMessage("§cTarget player not found.");
                player.closeInventory();
            }
            return;
        }

        // Navigation
        if (slot == 35) { // zurück
            if (page > 1) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                mobGui.open(player, target, page - 1);
            }
            return;
        }

        if (slot == 44) { // weiter
            List<MobSpawnRequest> mobs = mobManager.getRequests(targetUUID);
            int mobsPerPage = GuiConstants.ALLOWED_SLOTS.length;
            int totalPages = (int) Math.ceil(mobs.size() / (double) mobsPerPage);
            if (page < totalPages) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUID);
                mobGui.open(player, target, page + 1);
            }
            return;
        }

        // Gültiger Inhaltsslot?
        int indexInPage = -1;
        for (int i = 0; i < GuiConstants.ALLOWED_SLOTS.length; i++) {
            if (GuiConstants.ALLOWED_SLOTS[i] == slot) {
                indexInPage = i;
                break;
            }
        }
        if (indexInPage == -1) return; // Kein gültiger Slot

        List<MobSpawnRequest> spawns = mobManager.getRequests(targetUUID);

        int index = (page - 1) * GuiConstants.ALLOWED_SLOTS.length + indexInPage;
        if (index >= spawns.size()) {
            player.sendMessage("§cInvalid mob request.");
            return;
        }

        MobSpawnRequest request = spawns.get(index);

        ItemStack clickedItem = e.getCurrentItem();
        EntityType entityType = getEntityTypeFromItem(clickedItem);

        if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL
                && MobCategories.HOSTILE_MOBS.contains(entityType)) {

            player.sendMessage(ChatColor.RED + "The server is set to Peaceful and therefore this mob cannot be spawned.");
            return;
        }

        if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL
                && MobCategories.HOSTILE_EXCEPTIONS_IN_PEACEFUL.contains(entityType)) {

            player.sendMessage(ChatColor.RED + "The server is set to Peaceful and therefore this mob cannot be spawned.");
            return;
        }

        if (e.isRightClick()) {
            player.closeInventory();
            mobManager.setPendingSpawn(player.getUniqueId(), request);
            player.sendMessage("§eEnter in chat how many §a" + request.getEntityType().name() + "§e should be spawned.");
        }

        if (e.isLeftClick()) {
            // Spawn 1 Mob vom Typ des Requests
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.getWorld().spawnEntity(player.getLocation(), request.getEntityType());
                player.sendMessage("§a1 §e" + request.getEntityType().name() + "§a was spawned.");
            });
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        MobSpawnRequest request = MobManager.getPendingSpawn(uuid);
        if (request == null) return;

        event.setCancelled(true);

        try {
            int amount = Integer.parseInt(event.getMessage());
            if (amount <= 0 || amount > 100) {
                player.sendMessage("§cPlease enter a number between 1 and 100.");
                return;
            }

            // Mobs spawnen
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (int i = 0; i < amount; i++) {
                    player.getWorld().spawnEntity(player.getLocation(), request.getEntityType());
                }
                player.sendMessage("§a" + amount + " §e" + request.getEntityType().name() + "§a were spawned.");
            });
        } catch (NumberFormatException e) {
            player.sendMessage("§cPlease enter a valid number.");
        } finally {
            MobManager.clearPendingSpawn(uuid);
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();
        ItemStack nameTag = inv.getItem(53); // oder anderer Slot
        if (nameTag == null || !nameTag.hasItemMeta()) return null;
        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;
        return Bukkit.getOfflinePlayer(ChatColor.stripColor(meta.getDisplayName()));
    }

    private EntityType getEntityTypeFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        // Strip color and format the name to match enum (e.g. "Wither Skeleton" → "WITHER_SKELETON")
        String name = ChatColor.stripColor(meta.getDisplayName())
                .toUpperCase()
                .replace(' ', '_')
                .replace("-", "_");

        try {
            return EntityType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}