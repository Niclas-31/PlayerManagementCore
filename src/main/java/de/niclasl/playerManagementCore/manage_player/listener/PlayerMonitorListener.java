package de.niclasl.playerManagementCore.manage_player.listener;

import de.niclasl.playerManagementCore.PlayerManagementCore;
import de.niclasl.playerManagementCore.armor.manager.RepairManager;
import de.niclasl.playerManagementCore.ban_system.gui.BanHistoryGui;
import de.niclasl.playerManagementCore.effects.gui.PlayerEffectsGui;
import de.niclasl.playerManagementCore.report_system.gui.ReportGui;
import de.niclasl.playerManagementCore.gamemode_manage.gui.GamemodeGui;
import de.niclasl.playerManagementCore.warn_system.gui.WarnGui;
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
    private final PlayerManagementCore plugin;

    public PlayerMonitorListener(WarnGui warnGui, PlayerEffectsGui playerEffectsGui, PlayerManagementCore plugin) {
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

        OfflinePlayer target = null;

        for (ItemStack item : event.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
                String name = ChatColor.stripColor(Objects.requireNonNull(item.getItemMeta()).getDisplayName());

                target = Bukkit.getOfflinePlayer(name);
                break;
            }
        }

        if (target == null) {
            player.sendMessage("§cCould not identify target player.");
            return;
        }

        Player online = target.getPlayer();

        Material clickedType = event.getCurrentItem().getType();
        String clickedName = event.getCurrentItem().getItemMeta() != null
                ? ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()) : "";

        switch (clickedType) {
            case ENDER_EYE -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                player.openInventory(online.getInventory());
            }
            case ENDER_CHEST -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                player.openInventory(online.getEnderChest());
            }
            case IRON_SWORD -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                online.setHealth(0);
                player.sendMessage("§cKill: §e" + target.getName());
                player.closeInventory();
            }
            case RED_DYE -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                online.setHealth(20);
                player.sendMessage("§aHeal: §e" + target.getName());
                player.closeInventory();
            }
            case GREEN_STAINED_GLASS_PANE -> {
                if (Bukkit.getBanList(BanList.Type.NAME).isBanned(Objects.requireNonNull(target.getName()))) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
                    player.sendMessage("§aPlayer §e" + target.getName() + " §a has been unbanned.");
                } else {
                    player.sendMessage("§e" + target.getName() + " §7is not banned.");
                }
                player.closeInventory();
            }
            case ENCHANTED_BOOK -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                warnGui.open(player, target, 1);
            }
            case ENDER_PEARL -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                plugin.getDimensionGui().open(player, target, 1);
            }
            case BOOK -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                BanHistoryGui.open(player, target, 1);
            }
            case RED_CONCRETE, LIME_CONCRETE -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                if (clickedName.contains("Vanish")) {
                    boolean isVanished = plugin.getVanishManager().isVanished(target.getUniqueId());
                    plugin.getVanishManager().setVanish(target.getUniqueId(), !isVanished);
                    player.sendMessage("§7Vanish for §e" + target.getName() + " §7is now " + (!isVanished ? "§aenabled" : "§cdisabled") + "§7.");
                }
            }
            case COMPASS -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                plugin.getStatsGui().open(player, target);
            }
            case COMMAND_BLOCK -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                GamemodeGui.open(player, online);
            }
            case KNOWLEDGE_BOOK -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                ReportGui.open(player, target, 1);
            }
            case SPAWNER -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                plugin.getMobGui().open(player, online, 1);
            }
            case BEDROCK -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                if (target.isOp()) {
                    player.sendMessage(ChatColor.RED + "You cannot kick an admin.");
                } else {
                    online.kickPlayer(ChatColor.RED + "You have been kicked from the server.");
                    player.closeInventory();
                }
            }
            case FEATHER -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                if (online.getAllowFlight()) {
                    online.setAllowFlight(false);
                    online.setFlying(false);
                    online.sendMessage("§cYour airplane mode has been disabled!");
                } else {
                    online.setAllowFlight(true);
                    online.setFlying(true);
                    online.sendMessage("§aYour airplane mode has been activated!");
                }
            }
            case DIAMOND_CHESTPLATE -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                if (clickedName.contains("Repair Items")) {
                    boolean isRepairing = RepairManager.isRepairEnabled(online);
                    RepairManager.setRepairEnabled(online, !isRepairing);

                    player.sendMessage("§7Repair Mode for §e" + target.getName() + " §7is now "
                            + (!isRepairing ? "§aenabled" : "§cdisabled") + "§7.");
                }
            }
            case POTION -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                playerEffectsGui.open(player, online);
            }
            case EXPERIENCE_BOTTLE -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                plugin.getEnchantGUI().open(player, target);
            }
            case NOTE_BLOCK -> {
                if (online == null) {
                    player.sendMessage("§cTarget player not online.");
                    return;
                }

                plugin.getAuditGui().open(player, target, 1);
            }
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