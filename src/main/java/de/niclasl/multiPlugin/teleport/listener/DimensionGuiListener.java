package de.niclasl.multiPlugin.teleport.listener;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import de.niclasl.multiPlugin.teleport.manager.TeleportManager;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class DimensionGuiListener implements Listener {

    private final TeleportManager teleportManager;

    public DimensionGuiListener(TeleportManager teleportManager) {
        this.teleportManager = teleportManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || !currentItem.hasItemMeta()) return;

        String title = event.getView().getTitle();
        if (!title.equals("§8Teleport to Dimension")) return;

        event.setCancelled(true);

        Material clicked = currentItem.getType();
        String fileName;
        String worldName;
        int slot = event.getSlot();

        if (slot == 26) {
            // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
            OfflinePlayer target = getTarget(player); // <- das musst du ggf. anpassen
            if (target != null) {
                WatchGuiManager.openPage2(player, (Player) target);
            } else {
                player.sendMessage("§cError: Target player not found.");
                player.closeInventory();
            }
        }

        switch (clicked) {
            case GRASS_BLOCK -> {
                fileName = "overworld.yml";
                worldName = "world";
            }
            case NETHERRACK -> {
                fileName = "nether.yml";
                worldName = "world_nether";
            }
            case END_STONE -> {
                fileName = "end.yml";
                worldName = "world_the_end";
            }
            case BARRIER, PLAYER_HEAD -> {
                return;
            }
            default -> {
                player.sendMessage("§cInvalid teleport target.");
                return;
            }
        }

        File locationFile = teleportManager.getLocationFile(fileName);
        if (locationFile == null || !locationFile.exists()) {
            player.sendMessage("§cLocation file §e" + fileName + "§c not found.");
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(locationFile);
        double x = config.getDouble("x");
        double y = config.getDouble("y");
        double z = config.getDouble("z");

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            player.sendMessage("§cWorld §e" + worldName + "§c not found.");
            return;
        }

        Location location = new Location(world, x, y, z);
        player.teleport(location);
        player.sendMessage("§aTeleported to §e" + formatWorldName(worldName) + "§a!");
        player.closeInventory();
    }

    private String formatWorldName(String worldName) {
        return switch (worldName) {
            case "world" -> "Overworld";
            case "world_nether" -> "Nether";
            case "world_the_end" -> "The End";
            default -> worldName;
        };
    }

    private OfflinePlayer getTarget(Player viewer) {
        Inventory inv = viewer.getOpenInventory().getTopInventory();

        // Slot 10 ist das NameTag-Item (laut StatsGui)
        ItemStack nameTag = inv.getItem(22);
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