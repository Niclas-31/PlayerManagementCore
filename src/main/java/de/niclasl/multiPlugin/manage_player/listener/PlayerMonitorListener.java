package de.niclasl.multiPlugin.manage_player.listener;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.armor.manager.RepairManager;
import de.niclasl.multiPlugin.ban_system.gui.BanHistoryGui;
import de.niclasl.multiPlugin.effects.gui.PlayerEffectsGui;
import de.niclasl.multiPlugin.report_system.gui.ReportGui;
import de.niclasl.multiPlugin.gamemode_manage.gui.GamemodeGui;
import de.niclasl.multiPlugin.warn_system.gui.WarnGui;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class PlayerMonitorListener implements Listener {

    private static WarnGui warnGui;
    private static PlayerEffectsGui playerEffectsGui;
    private final MultiPlugin plugin;

    public PlayerMonitorListener(WarnGui warnGui, PlayerEffectsGui playerEffectsGui, MultiPlugin plugin) {
        PlayerMonitorListener.warnGui = warnGui;
        PlayerMonitorListener.playerEffectsGui = playerEffectsGui;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!title.startsWith("Manage: ")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        // Zielspieler über Spielerkopf ermitteln
        Player target = null;
        for (ItemStack item : event.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
                String name = ChatColor.stripColor(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
                target = Bukkit.getPlayerExact(name);
                if (target != null) break;
            }
        }

        if (target == null) {
            player.sendMessage("§cCould not identify target player.");
            return;
        }

        // Alle anderen Items weiter wie gewohnt behandeln:
        Material clickedType = event.getCurrentItem().getType();
        String clickedName = event.getCurrentItem().getItemMeta() != null
                ? ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()) : "";

        switch (clickedType) {
            case ENDER_EYE -> player.openInventory(target.getInventory());
            case ENDER_CHEST -> player.openInventory(target.getEnderChest());
            case IRON_SWORD -> {
                target.setHealth(0);
                player.sendMessage("§cKill: §e" + target.getName());
                player.closeInventory();
            }
            case RED_DYE -> {
                target.setHealth(20);
                player.sendMessage("§aHeal: §e" + target.getName());
                player.closeInventory();
            }
            case GREEN_STAINED_GLASS_PANE -> {
                if (Bukkit.getBanList(BanList.Type.NAME).isBanned(target.getName())) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
                    player.sendMessage("§aPlayer §e" + target.getName() + " §a has been unbanned.");
                } else {
                    player.sendMessage("§e" + target.getName() + " §7is not banned.");
                }
                player.closeInventory();
            }
            case ENCHANTED_BOOK -> warnGui.open(player, target, 1);
            case ENDER_PEARL -> plugin.getDimensionGui().open(player, target, 1);
            case BOOK -> BanHistoryGui.open(player, target, 1);
            case RED_CONCRETE, LIME_CONCRETE -> {
                if (clickedName.contains("Vanish")) {
                    boolean isVanished = plugin.getVanishManager().isVanished(target.getUniqueId());
                    plugin.getVanishManager().setVanish(target.getUniqueId(), !isVanished);
                    player.sendMessage("§7Vanish for §e" + target.getName() + " §7is now " + (!isVanished ? "§aenabled" : "§cdisabled") + "§7.");
                }
            }
            case COMPASS -> plugin.getStatsGui().open(player, target);
            case COMMAND_BLOCK -> GamemodeGui.open(player, target);
            case KNOWLEDGE_BOOK -> ReportGui.open(player, target, 1);
            case SPAWNER -> plugin.getMobGui().open(player, target, 1);
            case BEDROCK -> {
                if (target.isOp()) {
                    player.sendMessage(ChatColor.RED + "You cannot kick an admin.");
                } else {
                    target.kickPlayer(ChatColor.RED + "You have been kicked from the server.");
                    player.closeInventory();
                }
            }
            case FEATHER -> {
                if (target.getAllowFlight()) {
                    target.setAllowFlight(false);
                    target.setFlying(false);
                    target.sendMessage("§cYour airplane mode has been disabled!");
                } else {
                    target.setAllowFlight(true);
                    target.setFlying(true); // optional direkt starten
                    target.sendMessage("§aYour airplane mode has been activated!");
                }
            }
            case DIAMOND_CHESTPLATE -> {
                if (clickedName.contains("Repair Items")) {
                    boolean isRepairing = RepairManager.isRepairEnabled(target);
                    RepairManager.setRepairEnabled(target, !isRepairing);

                    player.sendMessage("§7Repair Mode for §e" + target.getName() + " §7is now "
                            + (!isRepairing ? "§aenabled" : "§cdisabled") + "§7.");
                }
            }
            case POTION -> playerEffectsGui.open(player, target);
            case EXPERIENCE_BOTTLE -> plugin.getEnchantGUI().open(player, target);
            case ARROW -> {
                if (slot == 53) {
                    plugin.getWatchGuiManager().open2(player, target);
                } else if (slot == 45) {
                    plugin.getWatchGuiManager().open1(player, target);
                }
            }
        }
    }
}