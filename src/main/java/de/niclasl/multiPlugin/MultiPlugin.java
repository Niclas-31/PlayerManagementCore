package de.niclasl.multiPlugin;

import de.niclasl.multiPlugin.ban_system.commands.BanCommand;
import de.niclasl.multiPlugin.ban_system.commands.BanHistoryCommand;
import de.niclasl.multiPlugin.ban_system.commands.UnbanCommand;
import de.niclasl.multiPlugin.ban_system.gui.BanHistoryGui;
import de.niclasl.multiPlugin.ban_system.listener.BanHistoryGuiListener;
import de.niclasl.multiPlugin.ban_system.listener.LoginListener;
import de.niclasl.multiPlugin.filter.commands.MuteCommand;
import de.niclasl.multiPlugin.filter.commands.UnmuteCommand;
import de.niclasl.multiPlugin.filter.listener.ChatFilterListener;
import de.niclasl.multiPlugin.filter.manager.MuteManager;
import de.niclasl.multiPlugin.gamemode_manage.command.GamemodeCommand;
import de.niclasl.multiPlugin.gamemode_manage.gui.GamemodeGui;
import de.niclasl.multiPlugin.gamemode_manage.listener.GamemodeListener;
import de.niclasl.multiPlugin.manage_player.commands.*;
import de.niclasl.multiPlugin.manage_player.listener.PlayerMonitorListener;
import de.niclasl.multiPlugin.ban_system.manager.BanHistoryManager;
import de.niclasl.multiPlugin.ban_system.manager.ReasonManager;
import de.niclasl.multiPlugin.mob_system.commands.MobCommand;
import de.niclasl.multiPlugin.mob_system.commands.MobCountCommand;
import de.niclasl.multiPlugin.mob_system.gui.MobGui;
import de.niclasl.multiPlugin.mob_system.listener.MobGuiListener;
import de.niclasl.multiPlugin.mob_system.listener.PlayerJoinListener;
import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import de.niclasl.multiPlugin.permission.commands.PermissionCommand;
import de.niclasl.multiPlugin.playtime.listener.PlaytimeListener;
import de.niclasl.multiPlugin.playtime.manager.PlaytimeManager;
import de.niclasl.multiPlugin.portal.commands.PortalCommand;
import de.niclasl.multiPlugin.portal.manager.PortalManager;
import de.niclasl.multiPlugin.randomteleport.RandomTeleportCommand;
import de.niclasl.multiPlugin.report_system.commands.ReportCommand;
import de.niclasl.multiPlugin.report_system.commands.ReportGuiCommand;
import de.niclasl.multiPlugin.report_system.gui.ReportGui;
import de.niclasl.multiPlugin.report_system.listener.ReportListener;
import de.niclasl.multiPlugin.report_system.manager.ReportManager;
import de.niclasl.multiPlugin.spawn_protection.listener.SpawnProtectionListener;
import de.niclasl.multiPlugin.spawn_protection.listener.SpawnProtectionMovementListener;
import de.niclasl.multiPlugin.spawn_protection.manager.SpawnManager;
import de.niclasl.multiPlugin.stats.commands.StatsCommand;
import de.niclasl.multiPlugin.stats.gui.CraftedItemsGui;
import de.niclasl.multiPlugin.stats.gui.MinedBlocksGui;
import de.niclasl.multiPlugin.stats.gui.StatsGui;
import de.niclasl.multiPlugin.stats.gui.UsedItemsGui;
import de.niclasl.multiPlugin.stats.listener.CraftedItemsGuiListener;
import de.niclasl.multiPlugin.stats.listener.MinedBlocksGuiListener;
import de.niclasl.multiPlugin.stats.listener.StatsGuiListener;
import de.niclasl.multiPlugin.stats.listener.UsedItemsGuiListener;
import de.niclasl.multiPlugin.teleport.commands.DimensionCommand;
import de.niclasl.multiPlugin.teleport.commands.TeleportCommand;
import de.niclasl.multiPlugin.teleport.listener.DimensionGuiListener;
import de.niclasl.multiPlugin.teleport.manager.TeleportManager;
import de.niclasl.multiPlugin.vanish_system.command.VanishCommand;
import de.niclasl.multiPlugin.vanish_system.manager.VanishManager;
import de.niclasl.multiPlugin.warn_system.commands.UnwarnCommand;
import de.niclasl.multiPlugin.warn_system.commands.WarnCommand;
import de.niclasl.multiPlugin.warn_system.commands.WarnHistoryCommand;
import de.niclasl.multiPlugin.warn_system.gui.WarnGui;
import de.niclasl.multiPlugin.warn_system.listener.WarnGuiListener;
import de.niclasl.multiPlugin.warn_system.manage.WarnActionConfigManager;
import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MultiPlugin extends JavaPlugin {

    // reasons.yml Datei
    private static File reasonsFile;
    private static FileConfiguration reasonsConfig;
    private static File chatFilterFile;
    private static FileConfiguration chatFilterConfig;
    private static File vanishFile;
    private static FileConfiguration vanishConfig;

    @Override
    public void onEnable() {

        // 1. Konfigurationsdateien vorbereiten
        createReasonsFile(); // ← ganz am Anfang!
        loadReasonsFromFile();

        createChatFilterFile();
        loadChatFilterFromFile();

        createVanishFile();
        loadVanishConfig();

        // 2. Manager erstellen
        ReasonManager reasonManager = new ReasonManager(getReasonsConfig());
        BanHistoryManager banHistoryManager = new BanHistoryManager(getDataFolder());
        WarnManager warnManager = new WarnManager(getDataFolder());
        VanishManager vanishManager = new VanishManager(this);
        TeleportManager teleportManager = new TeleportManager(getDataFolder(), this);
        MuteManager muteManager = new MuteManager();
        ReportManager reportManager = new ReportManager(getDataFolder());
        MobManager mobManager = new MobManager(getDataFolder());
        WarnActionConfigManager warnActionConfigManager = new WarnActionConfigManager(this);
        PlaytimeManager playtimeManager = new PlaytimeManager(getDataFolder());
        SpawnManager spawnManager = new SpawnManager(this);
        PortalManager portalManager = new PortalManager();

        // 3. GUIs
        WarnGui warnGui = new WarnGui(this, warnManager);
        ReportGui reportGui = new ReportGui(this, reportManager);
        BanHistoryGui banHistoryGui = new BanHistoryGui(this);
        MobGui mobGui = new MobGui(this);
        StatsGui statsGui = new StatsGui(this);
        MinedBlocksGui minedBlocksGui = new MinedBlocksGui(this);
        UsedItemsGui usedItemsGui = new UsedItemsGui(this);
        CraftedItemsGui craftedItemsGui = new CraftedItemsGui(this);

        StatsCommand statsCommand = new StatsCommand(statsGui, minedBlocksGui, usedItemsGui, craftedItemsGui);

        // 4. Commands setzen
        Objects.requireNonNull(getCommand("manage")).setExecutor(new PlayerMonitorCommand());
        Objects.requireNonNull(getCommand("manage")).setTabCompleter(new PlayerMonitorCommand());

        Objects.requireNonNull(getCommand("vanish")).setExecutor(new VanishCommand(this, vanishManager));

        Objects.requireNonNull(getCommand("ban")).setExecutor(new BanCommand(reasonManager, banHistoryManager));
        Objects.requireNonNull(getCommand("ban")).setTabCompleter(new BanCommand(reasonManager, banHistoryManager));

        Objects.requireNonNull(getCommand("unban")).setExecutor(new UnbanCommand(banHistoryManager));
        Objects.requireNonNull(getCommand("unban")).setTabCompleter(new UnbanCommand(banHistoryManager));

        Objects.requireNonNull(getCommand("ban-history")).setExecutor(new BanHistoryCommand(banHistoryGui));
        Objects.requireNonNull(getCommand("ban-history")).setTabCompleter(new BanHistoryCommand(banHistoryGui));

        Objects.requireNonNull(getCommand("teleport-dimension")).setExecutor(new TeleportCommand(teleportManager));
        Objects.requireNonNull(getCommand("teleport-dimension")).setTabCompleter(new TeleportCommand(teleportManager));

        Objects.requireNonNull(getCommand("dimension")).setExecutor(new DimensionCommand());
        Objects.requireNonNull(getCommand("dimension")).setTabCompleter(new DimensionCommand());

        Objects.requireNonNull(getCommand("gamemode-gui")).setExecutor(new GamemodeCommand());
        Objects.requireNonNull(getCommand("gamemode-gui")).setTabCompleter(new GamemodeCommand());

        Objects.requireNonNull(getCommand("mute")).setExecutor(new MuteCommand(muteManager));
        Objects.requireNonNull(getCommand("mute")).setTabCompleter(new MuteCommand(muteManager));

        Objects.requireNonNull(getCommand("unmute")).setExecutor(new UnmuteCommand(muteManager));
        Objects.requireNonNull(getCommand("unmute")).setTabCompleter(new UnmuteCommand(muteManager));

        Objects.requireNonNull(getCommand("warn")).setExecutor(new WarnCommand(warnManager, banHistoryManager, this));
        Objects.requireNonNull(getCommand("warn")).setTabCompleter(new WarnCommand(warnManager, banHistoryManager, this));

        Objects.requireNonNull(getCommand("warn-history")).setExecutor(new WarnHistoryCommand(warnGui));
        Objects.requireNonNull(getCommand("warn-history")).setTabCompleter(new WarnHistoryCommand(warnGui));

        Objects.requireNonNull(getCommand("unwarn")).setExecutor(new UnwarnCommand(warnManager));
        Objects.requireNonNull(getCommand("unwarn")).setTabCompleter(new UnwarnCommand(warnManager));

        Objects.requireNonNull(getCommand("report")).setExecutor(new ReportCommand(reportManager));
        Objects.requireNonNull(getCommand("report")).setTabCompleter(new ReportCommand(reportManager));

        Objects.requireNonNull(getCommand("report-history")).setExecutor(new ReportGuiCommand(reportGui));
        Objects.requireNonNull(getCommand("report-history")).setTabCompleter(new ReportGuiCommand(reportGui));

        Objects.requireNonNull(getCommand("mob")).setExecutor(new MobCommand(mobGui));
        Objects.requireNonNull(getCommand("mob")).setTabCompleter(new MobCommand(mobGui));

        Objects.requireNonNull(getCommand("mobcount")).setExecutor(new MobCountCommand());
        Objects.requireNonNull(getCommand("mobcount")).setTabCompleter(new MobCountCommand());

        Objects.requireNonNull(getCommand("stats")).setExecutor(statsCommand);

        Objects.requireNonNull(getCommand("minedblocks")).setExecutor(statsCommand);

        Objects.requireNonNull(getCommand("useditems")).setExecutor(statsCommand);

        Objects.requireNonNull(getCommand("crafteditems")).setExecutor(statsCommand);

        Objects.requireNonNull(getCommand("randomteleport")).setExecutor(new RandomTeleportCommand(this));

        Objects.requireNonNull(getCommand("portal")).setExecutor(new PortalCommand());
        Objects.requireNonNull(getCommand("portal")).setTabCompleter(new PortalCommand());

        Objects.requireNonNull(getCommand("permission")).setExecutor(new PermissionCommand());
        Objects.requireNonNull(getCommand("permission")).setTabCompleter(new PermissionCommand());

        // 5. Listener registrieren
        getServer().getPluginManager().registerEvents(new BanHistoryGuiListener(banHistoryManager), this);
        getServer().getPluginManager().registerEvents(new PlayerMonitorListener(warnGui), this);
        getServer().getPluginManager().registerEvents(new LoginListener(banHistoryManager), this);
        getServer().getPluginManager().registerEvents(new DimensionGuiListener(teleportManager), this);
        getServer().getPluginManager().registerEvents(new GamemodeListener(), this);
        getServer().getPluginManager().registerEvents(new ChatFilterListener(chatFilterConfig, muteManager), this);
        getServer().getPluginManager().registerEvents(new WarnGuiListener(warnManager, warnGui), this);
        getServer().getPluginManager().registerEvents(new StatsGuiListener(), this);
        getServer().getPluginManager().registerEvents(new ReportListener(reportManager), this);
        getServer().getPluginManager().registerEvents(new MobGuiListener(mobManager, mobGui, this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(mobManager), this);
        getServer().getPluginManager().registerEvents(new MinedBlocksGuiListener(), this);
        getServer().getPluginManager().registerEvents(new UsedItemsGuiListener(), this);
        getServer().getPluginManager().registerEvents(new CraftedItemsGuiListener(), this);
        getServer().getPluginManager().registerEvents(new PlaytimeListener(this, playtimeManager), this);
        getServer().getPluginManager().registerEvents(new SpawnProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnProtectionMovementListener(this), this);
        getServer().getPluginManager().registerEvents(new PortalManager(), this);

        // GUI Init
        GamemodeGui.init(this);

        PortalManager.init(this);

        SpawnManager.loadAllSpawns();
    }

    @Override
    public void onDisable(){

        // Reasons file save
        saveReasonsToFile();

        // Save Vanish
        saveVanishConfig();

        // Chat Filter file save
        saveChatFilterToFile();
    }

    public void createReasonsFile() {
        reasonsFile = new File(getDataFolder(), "reasons.yml");
        List<String> defaultReasons;

        if (!reasonsFile.exists()) {
            try {
                reasonsFile.getParentFile().mkdirs();
                reasonsFile.createNewFile();

                reasonsConfig = new YamlConfiguration();
                defaultReasons = new ArrayList<>(List.of("Beleidigung", "Spam", "Hacking", "Bugusing", "Werbung"));
                reasonsConfig.set("reasons", defaultReasons);
                reasonsConfig.save(reasonsFile);

                System.out.println("reasons.yml created with default reasons.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            reasonsConfig = YamlConfiguration.loadConfiguration(reasonsFile);
        }
    }

    public void loadReasonsFromFile() {
        reasonsFile = new File(getDataFolder(), "reasons.yml");

        if (!reasonsFile.exists()) {
            reasonsFile.getParentFile().mkdirs();
            saveResource("reasons.yml", false); // Nur wenn du eine Default-Datei im JAR hast
        }

        reasonsConfig = YamlConfiguration.loadConfiguration(reasonsFile);
    }

    public void saveReasonsToFile() {
        if (reasonsConfig == null || reasonsFile == null) return;
        try {
            reasonsConfig.save(reasonsFile);
        } catch (IOException e) {
            getLogger().warning("Could not save reasons.yml");
            e.printStackTrace();
        }
    }

    public FileConfiguration getReasonsConfig() {
        return reasonsConfig;
    }

    public void createChatFilterFile() {
        chatFilterFile = new File(getDataFolder(), "banned-words.yml");
        List<String> chatFilter;
        if (!chatFilterFile.exists()) {
            try {
                chatFilterFile.getParentFile().mkdirs();
                chatFilterFile.createNewFile();

                chatFilterConfig = new YamlConfiguration();
                chatFilter = new ArrayList<>(List.of("scheiße", "idiot", "hurensohn", "niger", "fick dich"));
                chatFilterConfig.set("banned-words", chatFilter);
                chatFilterConfig.save(chatFilterFile);

                System.out.println("chatfilter.yml created with default words.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            chatFilterConfig = YamlConfiguration.loadConfiguration(chatFilterFile);
        }
    }

    public void loadChatFilterFromFile() {
        chatFilterFile = new File(getDataFolder(), "banned-words.yml");

        if (!chatFilterFile.exists()) {
            chatFilterFile.getParentFile().mkdirs();
            saveResource("banned-words.yml", false); // Nur wenn du eine Default-Datei im JAR hast
        }

        chatFilterConfig = YamlConfiguration.loadConfiguration(chatFilterFile);
    }

    public void saveChatFilterToFile() {
        if (chatFilterConfig == null || chatFilterFile == null) return;
        try {
            chatFilterConfig.save(chatFilterFile);
        } catch (IOException e) {
            getLogger().warning("Could not save banned-words.yml");
            e.printStackTrace();
        }
    }

    private void createVanishFile() {
        vanishFile = new File(getDataFolder(), "vanish.yml");

        if (!vanishFile.exists()) {
            try {
                getDataFolder().mkdirs(); // Plugin-Ordner erstellen, falls noch nicht da
                vanishFile.createNewFile(); // Vanish-Datei anlegen
                getLogger().info("Created vanish.yml successfully.");
            } catch (IOException e) {
                getLogger().severe("Could not create vanish.yml!");
                e.printStackTrace();
            }
        }

        vanishConfig = YamlConfiguration.loadConfiguration(vanishFile);
    }

    public FileConfiguration getVanishConfig() {
        return vanishConfig;
    }

    public void saveVanishConfig() {
        if (vanishConfig == null || vanishFile == null) {
            getLogger().warning("Vanish config not initialized.");
            return;
        }

        try {
            vanishConfig.save(vanishFile);
        } catch (IOException e) {
            getLogger().severe("Could not save vanish.yml!");
            e.printStackTrace();
        }
    }

    public void loadVanishConfig() {
        vanishFile = new File(getDataFolder(), "vanish.yml");

        if (!vanishFile.exists()) {
            try {
                vanishFile.getParentFile().mkdirs();
                vanishFile.createNewFile(); // <-- ohne saveResource
            } catch (IOException e) {
                getLogger().severe("Could not create vanish.yml!");
                e.printStackTrace();
            }
        }

        vanishConfig = YamlConfiguration.loadConfiguration(vanishFile);
    }
}
