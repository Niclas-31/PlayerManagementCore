package de.niclasl.multiPlugin.manage_player.gui;

import de.niclasl.multiPlugin.vanish_system.manager.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static de.niclasl.multiPlugin.gamemode_manage.gui.GamemodeGui.plugin;

public class WatchGuiManager {

    public static void openPage1(Player viewer, Player target) {
        Inventory managePlayerPage1 = Bukkit.createInventory(null, 36, "§8Manage: " + target.getName() + " §7(1/2)");

        // Rand
        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{1,2,3,4,5,6,7,9,17,18,  26,27,28,29,30,32,33,34}) {
            managePlayerPage1.setItem(i, glass);
        }

        ItemStack kick = new ItemStack(Material.BEDROCK);
        ItemMeta kickMeta = kick.getItemMeta();
        assert kickMeta != null;
        kickMeta.setDisplayName(ChatColor.RED + "Kick");
        kickMeta.setLore(List.of(ChatColor.RED + "Kick " + target.getName() + " from the Server."));
        kick.setItemMeta(kickMeta);

        managePlayerPage1.setItem(10, kick);

        ItemStack banHistory = new ItemStack(Material.BOOK);
        ItemMeta banHistoryMeta = banHistory.getItemMeta();
        assert banHistoryMeta != null;
        banHistoryMeta.setDisplayName(ChatColor.GOLD + "Ban History");
        banHistoryMeta.setLore(List.of(ChatColor.GOLD + "See the Ban History from " + target.getName() + "."));
        banHistory.setItemMeta(banHistoryMeta);

        managePlayerPage1.setItem(12, banHistory);

        ItemStack warnGui = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta warnGuiMeta = warnGui.getItemMeta();
        assert warnGuiMeta != null;
        warnGuiMeta.setDisplayName(ChatColor.GOLD + "Warn History");
        warnGuiMeta.setLore(List.of(ChatColor.GOLD + "See the Warn History from " + target.getName() + "."));
        warnGui.setItemMeta(warnGuiMeta);

        managePlayerPage1.setItem(14, warnGui);

        ItemStack reportItem = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta reportMeta = reportItem.getItemMeta();
        assert reportMeta != null;
        reportMeta.setDisplayName(ChatColor.GOLD + "Report History");
        reportMeta.setLore(List.of(ChatColor.GOLD + "See the Report History from " + target.getName() + "."));
        reportItem.setItemMeta(reportMeta);

        managePlayerPage1.setItem(16, reportItem);

        ItemStack unban = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta unbanMeta = unban.getItemMeta();
        assert unbanMeta != null;
        unbanMeta.setDisplayName(ChatColor.GREEN + "Unban");
        unbanMeta.setLore(List.of(ChatColor.GREEN + "Unban " + target.getName()));
        unban.setItemMeta(unbanMeta);

        managePlayerPage1.setItem(19, unban);

        ItemStack invsee = new ItemStack(Material.ENDER_EYE);
        ItemMeta invseeMeta = invsee.getItemMeta();
        assert invseeMeta != null;
        invseeMeta.setDisplayName(ChatColor.WHITE + "Invsee");
        invseeMeta.setLore(List.of(ChatColor.WHITE + "See the inventory from " + target.getName() + "."));
        invsee.setItemMeta(invseeMeta);

        managePlayerPage1.setItem(21, invsee);

        ItemStack ecSee = new ItemStack(Material.ENDER_CHEST);
        ItemMeta ecSeeMeta = ecSee.getItemMeta();
        assert ecSeeMeta != null;
        ecSeeMeta.setDisplayName(ChatColor.WHITE + "EC-See");
        ecSeeMeta.setLore(List.of(ChatColor.WHITE + "See the Ender Chest from " + target.getName() + "."));
        ecSee.setItemMeta(ecSeeMeta);

        managePlayerPage1.setItem(23, ecSee);

        ItemStack teleportGui = new ItemStack(Material.ENDER_PEARL);
        ItemMeta teleportGuiMeta = teleportGui.getItemMeta();
        assert teleportGuiMeta != null;
        teleportGuiMeta.setDisplayName(ChatColor.WHITE + "Teleport");
        teleportGuiMeta.setLore(List.of(ChatColor.WHITE + "Teleport " + target.getName() + " to a dimension."));
        teleportGui.setItemMeta(teleportGuiMeta);

        managePlayerPage1.setItem(25, teleportGui);

        managePlayerPage1.setItem(31, createPlayerHead(target));

        ItemStack further = new ItemStack(Material.ARROW);
        ItemMeta furtherMeta = further.getItemMeta();
        assert furtherMeta != null;
        furtherMeta.setDisplayName(ChatColor.WHITE + "Further");
        further.setItemMeta(furtherMeta);

        managePlayerPage1.setItem(35, further);

        viewer.openInventory(managePlayerPage1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!viewer.getOpenInventory().getTitle().contains("§8Manage: " + target.getName() + " §7(1/2)")) {
                    cancel(); // GUI ist zu
                    return;
                }

                // Status-Item aktualisieren
                boolean isOnline = target.isOnline();
                Material statusMat = isOnline ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
                String statusName = isOnline ? "§aOnline" : "§cOffline";

                ItemStack statusItem = new ItemStack(statusMat);
                ItemMeta statusMeta = statusItem.getItemMeta();
                assert statusMeta != null;
                statusMeta.setDisplayName(statusName);
                statusItem.setItemMeta(statusMeta);
                viewer.getOpenInventory().getTopInventory().setItem(8, statusItem);

                // Welt- und Positions-Block aktualisieren
                if (isOnline) {
                    ItemStack worldInfo = new ItemStack(Material.GRASS_BLOCK);
                    ItemMeta meta = worldInfo.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName("§aWorld: " + target.getWorld().getName());
                    meta.setLore(List.of(
                            "§7X: " + target.getLocation().getBlockX(),
                            "§7Y: " + target.getLocation().getBlockY(),
                            "§7Z: " + target.getLocation().getBlockZ()
                    ));
                    worldInfo.setItemMeta(meta);
                    viewer.getOpenInventory().getTopInventory().setItem(0, worldInfo);
                }
            }
        }.runTaskTimer(plugin, 0, 10); // Alle 0,5 Sekunden
    }

    public static void openPage2(Player viewer, Player target) {
        Inventory managePlayerPage2 = Bukkit.createInventory(null, 36, "§8Manage: " + target.getName() + " §7(2/2)");

        UUID targetId = target.getUniqueId();

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{1,2,3,4,5,6,7,9,17,18,  26,28,29,30,32,33,34,35}) {
            managePlayerPage2.setItem(i, glass);
        }

        ItemStack mobSpawn = new ItemStack(Material.SPAWNER);
        ItemMeta mobSpawnMeta = mobSpawn.getItemMeta();
        assert mobSpawnMeta != null;
        mobSpawnMeta.setDisplayName(ChatColor.RED + "Mob Spawner");
        mobSpawnMeta.setLore(List.of(ChatColor.RED + "Spawn mobs by " + target.getName() + "."));
        mobSpawn.setItemMeta(mobSpawnMeta);

        managePlayerPage2.setItem(10, mobSpawn);

        ItemStack stats = new ItemStack(Material.COMPASS);
        ItemMeta statsMeta = stats.getItemMeta();
        assert statsMeta != null;
        statsMeta.setDisplayName(ChatColor.GOLD + "Player Stats");
        statsMeta.setLore(List.of(ChatColor.GOLD + "See the Ender Chest from " + target.getName() + "."));
        stats.setItemMeta(statsMeta);

        managePlayerPage2.setItem(12, stats);

        ItemStack kill = new ItemStack(Material.IRON_SWORD);
        ItemMeta killMeta = kill.getItemMeta();
        assert killMeta != null;
        killMeta.setDisplayName(ChatColor.RED + "Kill");
        killMeta.setLore(List.of(ChatColor.RED + "Kill " + target.getName(),
                ChatColor.RED + "Warning: You can also killed in Creative mode or in Spectator mode!"));
        kill.setItemMeta(killMeta);

        managePlayerPage2.setItem(14, kill);

        ItemStack heal = new ItemStack(Material.RED_DYE);
        ItemMeta healMeta = heal.getItemMeta();
        assert healMeta != null;
        healMeta.setDisplayName(ChatColor.WHITE + "Heal");
        healMeta.setLore(List.of(ChatColor.WHITE + "Heal " + target.getName()));
        heal.setItemMeta(healMeta);

        managePlayerPage2.setItem(16, heal);

        ItemStack gamemode = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta gamemodeMeta = gamemode.getItemMeta();
        assert gamemodeMeta != null;
        gamemodeMeta.setDisplayName(ChatColor.WHITE + "Gamemode Switcher");
        gamemodeMeta.setLore(List.of(ChatColor.WHITE + "Change Gamemode from " + target.getName() + "."));
        gamemode.setItemMeta(gamemodeMeta);

        managePlayerPage2.setItem(21, gamemode);

        managePlayerPage2.setItem(31, createPlayerHead(target));

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName(ChatColor.WHITE + "Back");
        back.setItemMeta(backMeta);

        managePlayerPage2.setItem(27, back);

        // weitere Items z.B. Mute, Permissions etc. hier

        viewer.openInventory(managePlayerPage2);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!viewer.getOpenInventory().getTitle().contains("Manage: " + target.getName() + " §7(2/2)")) {
                    cancel(); // GUI ist zu
                    return;
                }

                // Online-Status
                boolean isOnline = target.isOnline();
                Material statusMat = isOnline ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
                String statusName = isOnline ? "§aOnline" : "§cOffline";

                ItemStack statusItem = new ItemStack(statusMat);
                ItemMeta statusMeta = statusItem.getItemMeta();
                assert statusMeta != null;
                statusMeta.setDisplayName(statusName);
                statusItem.setItemMeta(statusMeta);
                viewer.getOpenInventory().getTopInventory().setItem(8, statusItem);

                // Welt und Position (nur wenn online)
                if (isOnline) {
                    ItemStack worldInfo = new ItemStack(Material.GRASS_BLOCK);
                    ItemMeta meta = worldInfo.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName("§aWorld: " + target.getWorld().getName());
                    meta.setLore(List.of(
                            "§7X: " + target.getLocation().getBlockX(),
                            "§7Y: " + target.getLocation().getBlockY(),
                            "§7Z: " + target.getLocation().getBlockZ()
                    ));
                    worldInfo.setItemMeta(meta);
                    viewer.getOpenInventory().getTopInventory().setItem(0, worldInfo);
                }

                // Vanish-Status (immer)
                boolean isVanished = VanishManager.isVanished(target.getUniqueId()); // oder dein eigenes System
                Material vanishMat = isVanished ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
                String vanishName = "§7Vanish: " + (isVanished ? "§aOn" : "§cOFF");

                ItemStack vanishItem = new ItemStack(vanishMat);
                ItemMeta vanishMeta = vanishItem.getItemMeta();
                assert vanishMeta != null;
                vanishMeta.setDisplayName(vanishName);
                vanishMeta.setLore(List.of("§7Click to toggle the Vanish mode from " + target.getName() + "."));
                vanishItem.setItemMeta(vanishMeta);
                viewer.getOpenInventory().getTopInventory().setItem(19, vanishItem);
            }
        }.runTaskTimer(plugin, 0, 10); // Alle 0,5 Sekunden
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPlayerHead(Player target) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = head.getItemMeta();
        if (meta instanceof org.bukkit.inventory.meta.SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(target);
            skullMeta.setDisplayName("§b" + target.getName());
            head.setItemMeta(skullMeta);
        }
        return head;
    }
}