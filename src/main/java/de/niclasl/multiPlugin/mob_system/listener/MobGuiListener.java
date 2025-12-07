package de.niclasl.multiPlugin.mob_system.listener;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.GuiConstants;
import de.niclasl.multiPlugin.mob_system.MobCategories;
import de.niclasl.multiPlugin.mob_system.gui.MobGui;
import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import de.niclasl.multiPlugin.mob_system.model.MobSpawnRequest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class MobGuiListener implements Listener {

    private static MultiPlugin plugin;

    public MobGuiListener(MultiPlugin plugin) {
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

        UUID targetUUID = UUID.fromString(player.getMetadata("mob_target").getFirst().asString());
        int page = player.getMetadata("mob_page").getFirst().asInt();

        int slot = e.getSlot();

        // Schließen / Zurück
        if (slot == 26) {
            // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
            OfflinePlayer target = getTarget(player); // <- das musst du ggf. anpassen
            if (target != null) {
                plugin.getWatchGuiManager().open1(player, (Player) target);
            } else {
                player.sendMessage("§cError: Target player not found.");
                player.closeInventory();
            }
        }

        if (slot == 17) {
            // Filterbuch
            int idx = MobGui.playerFilterIndex.getOrDefault(player.getUniqueId(), -1);

            if (e.isLeftClick()) {
                // Von "Alle" auf A, oder durch A-Z, dann wieder "Alle"
                if (idx == -1) idx = 0;
                else if (idx == MobGui.ALPHABET.length - 1) idx = -1;
                else idx++;
            } else if (e.isRightClick()) {
                // Rückwärts: von "Alle" auf Z, sonst rückwärts
                if (idx == -1) idx = MobGui.ALPHABET.length - 1;
                else if (idx == 0) idx = -1;
                else idx--;
            }

            MobGui.playerFilterIndex.put(player.getUniqueId(), idx);
            OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(player.getMetadata("mob_target").getFirst().asString()));
            plugin.getMobGui().open(player, target, 1);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(player.getMetadata("mob_target").getFirst().asString()));

        // Hole gefilterte Mobs für den aktuellen Buchstaben
        int filterIndex = MobGui.playerFilterIndex.getOrDefault(player.getUniqueId(), -1); // -1 = Alle
        Character currentLetter = filterIndex >= 0 ? MobGui.ALPHABET[filterIndex] : null; // null = Alle
        List<MobSpawnRequest> requests = MobManager.getRequests(targetUUID);
        List<MobSpawnRequest> filteredRequests;
        if (currentLetter != null) {
            filteredRequests = requests.stream()
                    .filter(r -> r.getEntityType().name().startsWith(String.valueOf(currentLetter)))
                    .toList();
        } else {
            filteredRequests = requests; // Alle anzeigen
        }

        int mobsPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(filteredRequests.size() / (double) mobsPerPage);

        // Navigation
        if (slot == 35 && page > 1) { // zurück
            plugin.getMobGui().open(player, target, page - 1);
            return;
        }
        if (slot == 44 && page < totalPages) { // weiter
            plugin.getMobGui().open(player, target, page + 1);
            return;
        }

        // Inhaltsslot prüfen
        int indexInPage = -1;
        for (int i = 0; i < GuiConstants.ALLOWED_SLOTS.length; i++) {
            if (GuiConstants.ALLOWED_SLOTS[i] == slot) {
                indexInPage = i;
                break;
            }
        }
        if (indexInPage == -1) return;

        int index = (page - 1) * mobsPerPage + indexInPage;
        if (index >= filteredRequests.size()) {
            player.sendMessage("§cInvalid mob request.");
            return;
        }

        MobSpawnRequest request = filteredRequests.get(index);
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        EntityType entityType = request.getEntityType();

        // Peaceful Check
        if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL
                && (MobCategories.HOSTILE_MOBS.contains(entityType) || MobCategories.HOSTILE_EXCEPTIONS_IN_PEACEFUL.contains(entityType))) {
            player.sendMessage(ChatColor.RED + "The server is set to Peaceful. This mob cannot be spawned.");
            return;
        }

        if (e.isRightClick()) {
            player.closeInventory();
            MobManager.setPendingSpawn(player.getUniqueId(), request);
            player.sendMessage("§eEnter in chat how many §a" + entityType.name() + "§e should be spawned.");
        } else if (e.isLeftClick()) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                // Mob einmal spawnen
                Entity entity = player.getWorld().spawnEntity(player.getLocation(), entityType);

                // Spieler schützen
                MobManager.registerSpawn(entity, player);

                // Wenn Warden → neutral machen
                if (entity instanceof Warden warden) {
                    warden.setAware(false);
                    warden.setTarget(null);
                }

                player.sendMessage("§a1 §e" + entityType.name() + "§a was spawned.");
            });
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        MobSpawnRequest request = MobManager.getPendingSpawn(player.getUniqueId());
        if (request == null) return;

        event.setCancelled(true);

        try {
            int amount = Integer.parseInt(event.getMessage());
            if (amount <= 0 || amount > 100) {
                player.sendMessage("§cPlease enter a number between 1 and 100.");
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {

                for (int i = 0; i < amount; i++) {
                    Entity entity = player.getWorld().spawnEntity(player.getLocation(), request.getEntityType());

                    MobManager.registerSpawn(entity, player);

                    // Wenn Warden → neutral machen
                    if (entity instanceof Warden warden) {
                        warden.setAware(false);
                        warden.setTarget(null);
                    }
                }

                player.sendMessage("§a" + amount + " §e" + request.getEntityType().name() + "§a were spawned.");
            });

        } catch (NumberFormatException ex) {
            player.sendMessage("§cPlease enter a valid number.");
        } finally {
            MobManager.clearPendingSpawn(player.getUniqueId());
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        ItemStack nameTag = viewer.getOpenInventory().getTopInventory().getItem(53);
        if (nameTag == null || !nameTag.hasItemMeta()) return null;
        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;
        return Bukkit.getOfflinePlayer(ChatColor.stripColor(meta.getDisplayName()));
    }
}