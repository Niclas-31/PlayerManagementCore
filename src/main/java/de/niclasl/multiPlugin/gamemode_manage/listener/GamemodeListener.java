package de.niclasl.multiPlugin.gamemode_manage.listener;

import de.niclasl.multiPlugin.manage_player.gui.WatchGuiManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import static de.niclasl.multiPlugin.gamemode_manage.gui.GamemodeGui.plugin;

public class GamemodeListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        String title = event.getView().getTitle();
        if (!title.startsWith("§6Gamemode for ")) return;

        event.setCancelled(true);

        if (!viewer.hasMetadata("gm_target")) return;

        String targetName = viewer.getMetadata("gm_target").get(0).asString();
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            viewer.sendMessage("§cTarget player is not online.");
            viewer.closeInventory();
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        // Zurück-Button (z. B. Slot 31)
        if (clicked.getType() == Material.BARRIER) {
            WatchGuiManager.openPage2(viewer, target);
            return;
        }

        // Gamemode-Wechsel
        GameMode newMode = switch (clicked.getType()) {
            case IRON_SWORD -> GameMode.SURVIVAL;
            case GRASS_BLOCK -> GameMode.CREATIVE;
            case MAP -> GameMode.ADVENTURE;
            case ENDER_EYE -> GameMode.SPECTATOR;
            default -> null;
        };

        if (newMode == null) return;

        target.setGameMode(newMode);
        viewer.sendMessage("§aGamemode of §e" + target.getName() + "§a was changed to §6" + newMode.name() + "§a.");
        target.sendMessage("§aYour game mode was changed by §e" + viewer.getName() + "§a.");
        viewer.closeInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (player.hasMetadata("gm_target")) {
            player.removeMetadata("gm_target", plugin);
        }
    }
}