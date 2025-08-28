package de.niclasl.multiPlugin.stats.gui;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.playtime.manager.PlaytimeManager;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

public class StatsGui {

    public static Object player;
    private static MultiPlugin plugin;

    public StatsGui(MultiPlugin plugin) {
        StatsGui.plugin = plugin;
    }

    public static void open(Player viewer, OfflinePlayer target) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Stats: §7" + target.getName());

        boolean isOnline = target.isOnline();
        Player onlineTarget = isOnline ? Bukkit.getPlayer(target.getUniqueId()) : null;

        ItemStack glass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i : new int[]{0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,46,47,48,50,51,52,53}) {
            inv.setItem(i, glass);
        }

        // Kategorie: Spielerinfos
        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        assert nameMeta != null;
        nameMeta.setDisplayName("§b👤 Player info");
        nameMeta.setLore(List.of(
                "§7Name: §f" + target.getName(),
                "§7UUID: §f" + target.getUniqueId()
        ));
        nameItem.setItemMeta(nameMeta);
        inv.setItem(10, nameItem);

        ItemStack gmItem = new ItemStack(Material.SHIELD);
        ItemMeta gmMeta = gmItem.getItemMeta();
        assert gmMeta != null;
        gmMeta.setDisplayName("§a🛡️ Gamemode & OP");
        if (isOnline && onlineTarget != null) {
            gmMeta.setLore(List.of(
                    "§7Gamemode: §f" + onlineTarget.getGameMode().name(),
                    "§7Operator: " + (onlineTarget.isOp() ? "§aYes" : "§cNo")
            ));
        } else {
            gmMeta.setLore(List.of("§7Only available online."));
        }
        gmItem.setItemMeta(gmMeta);
        inv.setItem(14, gmItem);

        // Kategorie: Kampf & Überleben
        ItemStack killItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta killMeta = killItem.getItemMeta();
        assert killMeta != null;
        killMeta.setDisplayName("§c⚔ Battle Stats");
        killMeta.setLore(List.of(
                "§7Player kills: §f" + target.getStatistic(Statistic.PLAYER_KILLS),
                "§7Deaths: §f" + target.getStatistic(Statistic.DEATHS),
                "§7Mobs killed: §f" + target.getStatistic(Statistic.MOB_KILLS)
        ));
        killItem.setItemMeta(killMeta);
        inv.setItem(16, killItem);

        ItemStack damage = new ItemStack(Material.REDSTONE);
        ItemMeta dmgMeta = damage.getItemMeta();
        assert dmgMeta != null;
        dmgMeta.setDisplayName("§c💥 Damage");
        dmgMeta.setLore(List.of(
                "§7Damage Dealt: §f" + target.getStatistic(Statistic.DAMAGE_DEALT),
                "§7Damage Taken: §f" + target.getStatistic(Statistic.DAMAGE_TAKEN)
        ));
        damage.setItemMeta(dmgMeta);
        inv.setItem(19, damage);

        // Kategorie: Bewegung
        ItemStack movementItem = new ItemStack(Material.FEATHER);
        ItemMeta moveMeta = movementItem.getItemMeta();
        assert moveMeta != null;
        moveMeta.setDisplayName("§a🏃 Movement");
        moveMeta.setLore(List.of(
                "§7Walk: §f" + target.getStatistic(Statistic.WALK_ONE_CM) / 100 + "m",
                "§7Fly: §f" + target.getStatistic(Statistic.FLY_ONE_CM) / 100 + "m",
                "§7Boat: §f" + target.getStatistic(Statistic.BOAT_ONE_CM) / 100 + "m",
                "§7Jumps: §f" + target.getStatistic(Statistic.JUMP),
                "§7Swam: §f" + target.getStatistic(Statistic.SWIM_ONE_CM) / 100 + "m"
        ));
        movementItem.setItemMeta(moveMeta);
        inv.setItem(21, movementItem);

        // Kategorie: Interaktionen
        ItemStack interact = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta interactMeta = interact.getItemMeta();
        assert interactMeta != null;
        interactMeta.setDisplayName("§a🔧 Interactions");
        interactMeta.setLore(List.of(
                "§7Chests opened: §f" + target.getStatistic(Statistic.CHEST_OPENED),
                "§7Shulker Box opened: §f" + target.getStatistic(Statistic.SHULKER_BOX_OPENED),
                "§7Crafting Table used: §f" + target.getStatistic(Statistic.CRAFTING_TABLE_INTERACTION),
                "§7Interact with Anvil: §f" + target.getStatistic(Statistic.INTERACT_WITH_ANVIL),
                "§7Beds used: §f" + target.getStatistic(Statistic.SLEEP_IN_BED)
        ));
        interact.setItemMeta(interactMeta);
        inv.setItem(23, interact);

        // Kategorie: Blöcke & Items
        ItemStack mined = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta minedMeta = mined.getItemMeta();
        assert minedMeta != null;
        minedMeta.setDisplayName("§b🔨 Mined blocks");
        minedMeta.setLore(List.of("§7Show all mined blocks"));
        mined.setItemMeta(minedMeta);
        inv.setItem(25, mined);

        ItemStack use = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta useMeta = use.getItemMeta();
        assert useMeta != null;
        useMeta.setDisplayName("§b🔧 Items used");
        useMeta.setLore(List.of("§7Show all used items"));
        use.setItemMeta(useMeta);
        inv.setItem(28, use);

        ItemStack xpItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta xpMeta = xpItem.getItemMeta();
        assert xpMeta != null;
        xpMeta.setDisplayName("§a💫 Experience");
        if (isOnline && onlineTarget != null) {
            xpMeta.setLore(List.of(
                    "§7Level: §f" + onlineTarget.getLevel(),
                    "§7XP (current Level): §f" + Math.round(onlineTarget.getExp() * 100) + "%"
            ));
        } else {
            xpMeta.setLore(List.of("§7Only available online."));
        }
        xpItem.setItemMeta(xpMeta);
        inv.setItem(30, xpItem);

        ItemStack raids = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta raidMeta = raids.getItemMeta();
        assert raidMeta != null;
        raidMeta.setDisplayName("§6Raid Statistics");
        raidMeta.setLore(List.of(
                "§7Won Raids: §f" + target.getStatistic(Statistic.RAID_WIN),
                "§7Raids started: §f" + target.getStatistic(Statistic.RAID_TRIGGER)
        ));
        raids.setItemMeta(raidMeta);
        inv.setItem(32, raids);

        ItemStack villagerStats = new ItemStack(Material.EMERALD);
        ItemMeta villagerMeta = villagerStats.getItemMeta();
        assert villagerMeta != null;
        villagerMeta.setDisplayName("§aVillager interactions");
        villagerMeta.setLore(List.of(
                "§7Spoken to Villagers: §f" + target.getStatistic(Statistic.TALKED_TO_VILLAGER),
                "§7Traded with villagers: §f" + target.getStatistic(Statistic.TALKED_TO_VILLAGER)
        ));
        villagerStats.setItemMeta(villagerMeta);
        inv.setItem(34, villagerStats);

        ItemStack crafting = new ItemStack(Material.CRAFTER);
        ItemMeta craftingMeta = crafting.getItemMeta();
        assert craftingMeta != null;
        craftingMeta.setDisplayName("§b🧰 Items Crafted");
        craftingMeta.setLore(List.of("§7Show all crafted items"));
        crafting.setItemMeta(craftingMeta);
        inv.setItem(37, crafting);

        // Zurück-Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§c⬅ Back");
        back.setItemMeta(backMeta);
        inv.setItem(45, back);

        int headSlot = 49;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(target);
        meta.setDisplayName("§b" + target.getName());
        skull.setItemMeta(meta);
        inv.setItem(headSlot, skull);

        viewer.openInventory(inv);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Abbrechen, wenn Inventar nicht mehr offen ist
                if (!viewer.getOpenInventory().getTopInventory().equals(inv)) {
                    cancel();
                    return;
                }

                // Zeit aktualisieren
                YamlConfiguration playtimeConfig = PlaytimeManager.getPlayerConfig(target.getUniqueId());
                String playtimeFormatted = formatPlaytime(playtimeConfig);
                boolean isOnline = target.isOnline();

                ItemStack joinItem = new ItemStack(Material.CLOCK);
                ItemMeta joinMeta = joinItem.getItemMeta();
                assert joinMeta != null;
                joinMeta.setDisplayName("§a⏰ Playtime");
                joinMeta.setLore(List.of(
                        "§7First Login: §f" + formatDate(target.getFirstPlayed()),
                        "§7Last Login: §f" + formatDate(target.getLastPlayed()),
                        "§7Playtime: §f" + playtimeFormatted,
                        "§7Status: " + (isOnline ? "§aOnline" : "§cOffline")
                ));
                joinItem.setItemMeta(joinMeta);

                inv.setItem(12, joinItem); // Slot 12 updaten
            }
        }.runTaskTimer(plugin, 0L, 20L); // Jede Sekunde (20 Ticks)
    }

    private static String formatDate(long millis) {
        if (millis <= 0) return "§7Unknown";
        return new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(millis));
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

    public static String formatPlaytime(YamlConfiguration config) {
        int years = config.getInt("time.years");
        int months = config.getInt("time.months");
        int days = config.getInt("time.days");
        int hours = config.getInt("time.hours");
        int minutes = config.getInt("time.minutes");
        int seconds = config.getInt("time.seconds");

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append("y ");
        if (months > 0) sb.append(months).append("mo ");
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");

        String result = sb.toString().trim();
        return result.isEmpty() ? "0s" : result;
    }
}
