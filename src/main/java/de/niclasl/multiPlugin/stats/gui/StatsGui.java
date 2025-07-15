package de.niclasl.multiPlugin.stats.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class StatsGui {

    public static Object player;
    public static final Map<UUID, UUID> viewerTargets = new HashMap<>();

    public static void open(Player viewer, OfflinePlayer target) {
        Inventory inv = Bukkit.createInventory(null, 45, "§8Player Stats");

        boolean isOnline = target.isOnline();
        Player onlineTarget = isOnline ? Bukkit.getPlayer(target.getUniqueId()) : null;

        // Name & UUID
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        assert nameMeta != null;
        nameMeta.setDisplayName("§a" + target.getName());
        nameMeta.setLore(List.of(
                "§7UUID:",
                "§f" + target.getUniqueId()
        ));
        nameItem.setItemMeta(nameMeta);
        inv.setItem(10, nameItem);

        // Welt & Position
        ItemStack posItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta posMeta = posItem.getItemMeta();
        assert posMeta != null;
        posMeta.setDisplayName("§aWorld & Position");
        if (isOnline && onlineTarget != null) {
            Location loc = onlineTarget.getLocation();
            posMeta.setLore(List.of(
                    "§7World: §f" + Objects.requireNonNull(loc.getWorld()).getName(),
                    "§7X: §f" + loc.getBlockX(),
                    "§7Y: §f" + loc.getBlockY(),
                    "§7Z: §f" + loc.getBlockZ()
            ));
        } else {
            posMeta.setLore(List.of("§7Only available when the player is online."));
        }
        posItem.setItemMeta(posMeta);
        inv.setItem(12, posItem);

        // Gamemode & OP
        ItemStack gmItem = new ItemStack(Material.SHIELD);
        ItemMeta gmMeta = gmItem.getItemMeta();
        assert gmMeta != null;
        gmMeta.setDisplayName("§aGamemode & OP");
        if (isOnline) {
            assert onlineTarget != null;
            gmMeta.setLore(List.of(
                    "§7Gamemode: §f" + onlineTarget.getGameMode().name(),
                    "§7Operator: " + (onlineTarget.isOp() ? "§aYes" : "§cNo")
            ));
        } else {
            gmMeta.setLore(List.of("§7Only available when the player is online."));
        }
        gmItem.setItemMeta(gmMeta);
        inv.setItem(14, gmItem);

        // Join/Last Login
        ItemStack joinItem = new ItemStack(Material.CLOCK);
        ItemMeta joinMeta = joinItem.getItemMeta();
        assert joinMeta != null;
        joinMeta.setDisplayName("§aJoin data");
        joinMeta.setLore(List.of(
                "§7First Login:",
                "§f" + formatDate(target.getFirstPlayed()),
                "§7Last Login:",
                "§f" + formatDate(target.getLastPlayed()),
                "§7Status: " + (isOnline ? "§aOnline" : "§cOffline")
        ));
        joinItem.setItemMeta(joinMeta);
        inv.setItem(16, joinItem);

        // Level & XP
        ItemStack xpItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta xpMeta = xpItem.getItemMeta();
        assert xpMeta != null;
        xpMeta.setDisplayName("§aLevel & Experience");
        if (isOnline) {
            xpMeta.setLore(List.of(
                    "§7Level: §f" + onlineTarget.getLevel(),
                    "§7XP (current Level): §f" + Math.round(onlineTarget.getExp() * 100) + "%"
            ));
        } else {
            xpMeta.setLore(List.of("§7Only available when the player is online."));
        }
        xpItem.setItemMeta(xpMeta);
        inv.setItem(19, xpItem);

        // Kills / Deaths
        ItemStack killItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta killMeta = killItem.getItemMeta();
        assert killMeta != null;
        killMeta.setDisplayName("§aKills & Deaths");
        killMeta.setLore(List.of(
                "§7Player kills: §f" + target.getStatistic(Statistic.PLAYER_KILLS),
                "§7Deaths: §f" + target.getStatistic(Statistic.DEATHS),
                "§7Mobs killed: §f" + target.getStatistic(Statistic.MOB_KILLS)
        ));
        killItem.setItemMeta(killMeta);
        inv.setItem(21, killItem);

        // Laufdistanz / Sprünge
        ItemStack movementItem = new ItemStack(Material.FEATHER);
        ItemMeta moveMeta = movementItem.getItemMeta();
        assert moveMeta != null;
        moveMeta.setDisplayName("§aMovement");
        moveMeta.setLore(List.of(
                "§7Walked blocks: §f" + target.getStatistic(Statistic.WALK_ONE_CM) / 100 + " m",
                "§7Jumps: §f" + target.getStatistic(Statistic.JUMP)
        ));
        movementItem.setItemMeta(moveMeta);
        inv.setItem(23, movementItem);

        // Blöcke abgebaut
        ItemStack abbauen = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta abbauenMeta = abbauen.getItemMeta();
        assert abbauenMeta != null;
        abbauenMeta.setDisplayName("§aBlocks mined");
        abbauenMeta.setLore(List.of(
                "§7In total: §f" + target.getStatistic(Statistic.MINE_BLOCK, Material.STONE)
        ));
        abbauen.setItemMeta(abbauenMeta);
        inv.setItem(25, abbauen);

        // Blöcke platziert
        ItemStack platzieren = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta platzierenMeta = platzieren.getItemMeta();
        assert platzierenMeta != null;
        platzierenMeta.setDisplayName("§aUse Items");
        platzierenMeta.setLore(List.of(
                "§7In total: §f" + target.getStatistic(Statistic.USE_ITEM, Material.DIAMOND_SWORD)
        ));
        platzieren.setItemMeta(platzierenMeta);
        inv.setItem(28, platzieren);

        // Zurück-Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);
        inv.setItem(36, back);

        // Öffnen + Clickhandler
        viewer.openInventory(inv);

        // Event Listener in deiner Listenerklasse oder separater Handler
        // → wenn Slot 31 geklickt: ManagePlayerGui.open(viewer, target);
    }

    private static String formatDate(long millis) {
        if (millis <= 0) return "§7Unknown";
        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(millis));
    }

    public static void setPlayer(Object player) {
        StatsGui.player = player;
    }
}
