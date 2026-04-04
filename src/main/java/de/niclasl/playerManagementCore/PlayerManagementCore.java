package de.niclasl.playerManagementCore;

import de.niclasl.playerManagementCore.armor.listener.DamageListener;
import de.niclasl.playerManagementCore.armor.manager.CombatManager;
import de.niclasl.playerManagementCore.armor.manager.RepairManager;
import de.niclasl.playerManagementCore.armor.task.RepairTask;
import de.niclasl.playerManagementCore.audit.AuditManager;
import de.niclasl.playerManagementCore.audit.command.AuditCommand;
import de.niclasl.playerManagementCore.audit.gui.AuditGui;
import de.niclasl.playerManagementCore.audit.listener.AuditGuiListener;
import de.niclasl.playerManagementCore.ban_system.commands.BanCommand;
import de.niclasl.playerManagementCore.ban_system.commands.BanHistoryCommand;
import de.niclasl.playerManagementCore.ban_system.commands.UnbanCommand;
import de.niclasl.playerManagementCore.ban_system.gui.BanHistoryGui;
import de.niclasl.playerManagementCore.ban_system.listener.BanHistoryGuiListener;
import de.niclasl.playerManagementCore.ban_system.listener.LoginListener;
import de.niclasl.playerManagementCore.effects.gui.AddEffectGui;
import de.niclasl.playerManagementCore.effects.gui.PlayerEffectsGui;
import de.niclasl.playerManagementCore.effects.listener.AddEffectGuiListener;
import de.niclasl.playerManagementCore.effects.listener.PlayerEffectsListener;
import de.niclasl.playerManagementCore.filter.commands.MuteCommand;
import de.niclasl.playerManagementCore.filter.commands.UnmuteCommand;
import de.niclasl.playerManagementCore.filter.listener.ChatFilterListener;
import de.niclasl.playerManagementCore.filter.manager.MuteManager;
import de.niclasl.playerManagementCore.gamemode_manage.command.GamemodeCommand;
import de.niclasl.playerManagementCore.gamemode_manage.gui.GamemodeGui;
import de.niclasl.playerManagementCore.gamemode_manage.listener.GamemodeListener;
import de.niclasl.playerManagementCore.manage_player.commands.*;
import de.niclasl.playerManagementCore.manage_player.gui.WatchGuiManager;
import de.niclasl.playerManagementCore.manage_player.listener.PlayerMonitorListener;
import de.niclasl.playerManagementCore.ban_system.manager.BanHistoryManager;
import de.niclasl.playerManagementCore.ban_system.manager.ReasonManager;
import de.niclasl.playerManagementCore.mob_system.commands.MobCommand;
import de.niclasl.playerManagementCore.mob_system.commands.MobCountCommand;
import de.niclasl.playerManagementCore.mob_system.gui.MobGui;
import de.niclasl.playerManagementCore.mob_system.listener.MobGuiListener;
import de.niclasl.playerManagementCore.mob_system.listener.MobIgnoreListener;
import de.niclasl.playerManagementCore.mob_system.listener.PlayerJoinListener;
import de.niclasl.playerManagementCore.mob_system.manager.MobManager;
import de.niclasl.playerManagementCore.multienchant.command.EnchantCommand;
import de.niclasl.playerManagementCore.multienchant.gui.EnchantGUI;
import de.niclasl.playerManagementCore.multienchant.gui.LevelGUI;
import de.niclasl.playerManagementCore.multienchant.listener.EnchantListener;
import de.niclasl.playerManagementCore.multienchant.listener.LevelListener;
import de.niclasl.playerManagementCore.playtime.listener.PlaytimeListener;
import de.niclasl.playerManagementCore.playtime.manager.PlaytimeManager;
import de.niclasl.playerManagementCore.portal.commands.PortalCommand;
import de.niclasl.playerManagementCore.portal.gui.PortalGui;
import de.niclasl.playerManagementCore.portal.listener.TeleportBlockerListener;
import de.niclasl.playerManagementCore.portal.manager.PortalConfigManager;
import de.niclasl.playerManagementCore.randomteleport.RandomTeleportCommand;
import de.niclasl.playerManagementCore.report_system.commands.ReportCommand;
import de.niclasl.playerManagementCore.report_system.commands.ReportHistoryCommand;
import de.niclasl.playerManagementCore.report_system.gui.ReportGui;
import de.niclasl.playerManagementCore.report_system.listener.ReportListener;
import de.niclasl.playerManagementCore.report_system.manager.ReportManager;
import de.niclasl.playerManagementCore.spawn_protection.command.SpawnProtectionCommand;
import de.niclasl.playerManagementCore.spawn_protection.listener.SpawnProtectionListener;
import de.niclasl.playerManagementCore.spawn_protection.listener.SpawnProtectionMovementListener;
import de.niclasl.playerManagementCore.spawn_protection.manager.SpawnManager;
import de.niclasl.playerManagementCore.spawn_protection.manager.SpawnProtectionConfigManager;
import de.niclasl.playerManagementCore.stats.commands.CraftedItemsCommand;
import de.niclasl.playerManagementCore.stats.commands.MinedBlocksCommand;
import de.niclasl.playerManagementCore.stats.commands.StatsCommand;
import de.niclasl.playerManagementCore.stats.commands.UsedItemsCommand;
import de.niclasl.playerManagementCore.stats.gui.CraftedItemsGui;
import de.niclasl.playerManagementCore.stats.gui.MinedBlocksGui;
import de.niclasl.playerManagementCore.stats.gui.StatsGui;
import de.niclasl.playerManagementCore.stats.gui.UsedItemsGui;
import de.niclasl.playerManagementCore.stats.listener.CraftedItemsGuiListener;
import de.niclasl.playerManagementCore.stats.listener.MinedBlocksGuiListener;
import de.niclasl.playerManagementCore.stats.listener.StatsGuiListener;
import de.niclasl.playerManagementCore.stats.listener.UsedItemsGuiListener;
import de.niclasl.playerManagementCore.teleport.commands.TeleportCommand;
import de.niclasl.playerManagementCore.teleport.gui.DimensionGui;
import de.niclasl.playerManagementCore.teleport.listener.DimensionGuiListener;
import de.niclasl.playerManagementCore.teleport.manager.TeleportManager;
import de.niclasl.playerManagementCore.vanish_system.command.VanishCommand;
import de.niclasl.playerManagementCore.vanish_system.manager.VanishManager;
import de.niclasl.playerManagementCore.warn_system.commands.UnwarnCommand;
import de.niclasl.playerManagementCore.warn_system.commands.WarnCommand;
import de.niclasl.playerManagementCore.warn_system.commands.WarnHistoryCommand;
import de.niclasl.playerManagementCore.warn_system.gui.WarnGui;
import de.niclasl.playerManagementCore.warn_system.listener.WarnGuiListener;
import de.niclasl.playerManagementCore.warn_system.manage.WarnManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class PlayerManagementCore extends JavaPlugin {

    private static File reasonsFile;
    private static FileConfiguration reasonsConfig;
    private static File chatFilterFile;
    private static FileConfiguration chatFilterConfig;

    private final LevelGUI levelGUI = new LevelGUI(this);
    private final AddEffectGui addEffectGui = new AddEffectGui(this);
    private final MobGui mobGui = new MobGui(this);
    private final EnchantGUI enchantGUI = new EnchantGUI(this);
    private final DimensionGui dimensionGui = new DimensionGui(this);
    private final StatsGui statsGui = new StatsGui(this);
    private final MinedBlocksGui minedBlocksGui = new MinedBlocksGui(this);
    private final UsedItemsGui usedItemsGui = new UsedItemsGui(this);
    private final CraftedItemsGui craftedItemsGui = new CraftedItemsGui(this);
    private final WatchGuiManager watchGuiManager = new WatchGuiManager(this);
    private final AuditGui auditGui = new AuditGui(this);
    private final AuditManager auditManager = new AuditManager();

    private final VanishManager vanishManager = new VanishManager(this);
    private final SpawnManager spawnManager = new SpawnManager(this);
    private final PortalConfigManager portalConfigManager = new PortalConfigManager(this);
    private final SpawnProtectionConfigManager spawnProtectionConfigManager = new SpawnProtectionConfigManager(this);

    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            getLogger().warning("No Vault-compatible economy found! Economy features will be disabled.");
        }

        getLogger().info("[PortalControl] Plugins müssen ihre Teleports über PortalApi anmelden, sonst werden sie blockiert!");
        getLogger().info("[PortalControl] Wenn das Plugin nicht auf der Whitelist ist, dann wird es nicht freigeschalten!");

        spawnProtectionConfigManager.createFile();
        spawnProtectionConfigManager.load();

        createReasonsFile();
        loadReasonsFromFile();

        createChatFilterFile();
        loadChatFilterFromFile();

        vanishManager.createVanishFile();
        vanishManager.loadVanishConfig();


        ReasonManager reasonManager = new ReasonManager(getReasonsConfig());
        BanHistoryManager banHistoryManager = new BanHistoryManager(getDataFolder());
        WarnManager warnManager = new WarnManager(getDataFolder());
        TeleportManager teleportManager = new TeleportManager(getDataFolder(), this);
        MuteManager muteManager = new MuteManager();
        ReportManager reportManager = new ReportManager(getDataFolder());
        MobManager mobManager = new MobManager(getDataFolder());
        PlaytimeManager playtimeManager = new PlaytimeManager(getDataFolder());


        WarnGui warnGui = new WarnGui(this, warnManager);
        ReportGui reportGui = new ReportGui(this);
        BanHistoryGui banHistoryGui = new BanHistoryGui(this);
        PlayerEffectsGui playerEffectsGui = new PlayerEffectsGui(this);


        Objects.requireNonNull(getCommand("manage")).setExecutor(new PlayerMonitorCommand(this));
        Objects.requireNonNull(getCommand("manage")).setTabCompleter(new PlayerMonitorCommand(this));

        Objects.requireNonNull(getCommand("vanish")).setExecutor(new VanishCommand(vanishManager));

        Objects.requireNonNull(getCommand("ban")).setExecutor(new BanCommand(reasonManager, banHistoryManager));
        Objects.requireNonNull(getCommand("ban")).setTabCompleter(new BanCommand(reasonManager, banHistoryManager));

        Objects.requireNonNull(getCommand("unban")).setExecutor(new UnbanCommand(banHistoryManager));
        Objects.requireNonNull(getCommand("unban")).setTabCompleter(new UnbanCommand(banHistoryManager));

        Objects.requireNonNull(getCommand("ban-history")).setExecutor(new BanHistoryCommand(banHistoryGui));
        Objects.requireNonNull(getCommand("ban-history")).setTabCompleter(new BanHistoryCommand(banHistoryGui));

        Objects.requireNonNull(getCommand("dimension")).setExecutor(new TeleportCommand(teleportManager, this));
        Objects.requireNonNull(getCommand("dimension")).setTabCompleter(new TeleportCommand(teleportManager, this));

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

        Objects.requireNonNull(getCommand("report-history")).setExecutor(new ReportHistoryCommand(reportGui));
        Objects.requireNonNull(getCommand("report-history")).setTabCompleter(new ReportHistoryCommand(reportGui));

        Objects.requireNonNull(getCommand("mob")).setExecutor(new MobCommand(mobGui, this));
        Objects.requireNonNull(getCommand("mob")).setTabCompleter(new MobCommand(mobGui, this));

        Objects.requireNonNull(getCommand("mobcount")).setExecutor(new MobCountCommand());
        Objects.requireNonNull(getCommand("mobcount")).setTabCompleter(new MobCountCommand());

        Objects.requireNonNull(getCommand("stats")).setExecutor(new StatsCommand(statsGui));
        Objects.requireNonNull(getCommand("stats")).setTabCompleter(new StatsCommand(statsGui));

        Objects.requireNonNull(getCommand("minedblocks")).setExecutor(new MinedBlocksCommand(minedBlocksGui));
        Objects.requireNonNull(getCommand("minedblocks")).setTabCompleter(new MinedBlocksCommand(minedBlocksGui));

        Objects.requireNonNull(getCommand("useditems")).setExecutor(new UsedItemsCommand(usedItemsGui));
        Objects.requireNonNull(getCommand("useditems")).setTabCompleter(new UsedItemsCommand(usedItemsGui));

        Objects.requireNonNull(getCommand("crafteditems")).setExecutor(new CraftedItemsCommand(craftedItemsGui));
        Objects.requireNonNull(getCommand("crafteditems")).setTabCompleter(new CraftedItemsCommand(craftedItemsGui));

        Objects.requireNonNull(getCommand("randomteleport")).setExecutor(new RandomTeleportCommand(this));
        Objects.requireNonNull(getCommand("randomteleport")).setTabCompleter(new RandomTeleportCommand(this));

        Objects.requireNonNull(getCommand("portal")).setExecutor(new PortalCommand(this));
        Objects.requireNonNull(getCommand("portal")).setTabCompleter(new PortalCommand(this));

        Objects.requireNonNull(getCommand("enchant-gui")).setExecutor(new EnchantCommand(this));

        Objects.requireNonNull(getCommand("audit")).setExecutor(new AuditCommand(this));
        Objects.requireNonNull(getCommand("audit")).setTabCompleter(new AuditCommand(this));

        Objects.requireNonNull(getCommand("protection")).setExecutor(new SpawnProtectionCommand(this));
        Objects.requireNonNull(getCommand("protection")).setTabCompleter(new SpawnProtectionCommand(this));


        getServer().getPluginManager().registerEvents(new BanHistoryGuiListener(banHistoryManager, this), this);
        getServer().getPluginManager().registerEvents(new PlayerMonitorListener(warnGui, playerEffectsGui, this), this);
        getServer().getPluginManager().registerEvents(new LoginListener(banHistoryManager), this);
        getServer().getPluginManager().registerEvents(new DimensionGuiListener(teleportManager, this), this);
        getServer().getPluginManager().registerEvents(new GamemodeListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatFilterListener(chatFilterConfig, muteManager), this);
        getServer().getPluginManager().registerEvents(new WarnGuiListener(warnManager, warnGui, this), this);
        getServer().getPluginManager().registerEvents(new StatsGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new ReportListener(reportManager, this), this);
        getServer().getPluginManager().registerEvents(new MobGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(mobManager), this);
        getServer().getPluginManager().registerEvents(new MinedBlocksGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new UsedItemsGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftedItemsGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new PlaytimeListener(this, playtimeManager), this);
        getServer().getPluginManager().registerEvents(new SpawnProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnProtectionMovementListener(this), this);
        getServer().getPluginManager().registerEvents(new TeleportBlockerListener(), this);
        getServer().getPluginManager().registerEvents(new MobIgnoreListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerEffectsListener(this, playerEffectsGui), this);
        getServer().getPluginManager().registerEvents(new AddEffectGuiListener(playerEffectsGui), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new PortalGui(this), this);
        getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
        getServer().getPluginManager().registerEvents(new LevelListener(this), this);
        getServer().getPluginManager().registerEvents(new AuditGuiListener(this), this);


        Bukkit.getScheduler().runTaskTimer(
                this,
                CombatManager::cleanup,
                20L, 20L
        );


        GamemodeGui.init(this);

        portalConfigManager.init();

        RepairManager.init(getDataFolder());

        AuditManager.init(getDataFolder());

        portalConfigManager.init();

        spawnManager.loadAllSpawns();


        RepairTask.startAutoRepair(this, 20, 10.0, 5);
    }

    @Override
    public void onDisable(){
        spawnProtectionConfigManager.save();
        saveReasonsToFile();
        vanishManager.saveVanishConfig();
        saveChatFilterToFile();
        RepairManager.save();
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
            saveResource("reasons.yml", false);
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
            saveResource("banned-words.yml", false);
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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        return rsp != null;
    }

    public LevelGUI getLevelGUI() {
        return levelGUI;
    }

    public AddEffectGui getAddEffectGui() {
        return addEffectGui;
    }

    public MobGui getMobGui() {
        return mobGui;
    }

    public EnchantGUI getEnchantGUI() {
        return enchantGUI;
    }

    public DimensionGui getDimensionGui() {
        return dimensionGui;
    }

    public StatsGui getStatsGui() {
        return statsGui;
    }

    public MinedBlocksGui getMinedBlocksGui() {
        return minedBlocksGui;
    }

    public UsedItemsGui getUsedItemsGui() {
        return usedItemsGui;
    }

    public CraftedItemsGui getCraftedItemsGui() {
        return craftedItemsGui;
    }

    public WatchGuiManager getWatchGuiManager() {
        return watchGuiManager;
    }

    public AuditGui getAuditGui() {
        return auditGui;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public PortalConfigManager getPortalConfigManager() {
        return portalConfigManager;
    }

    public AuditManager getAuditManager() {
        return auditManager;
    }

    public SpawnProtectionConfigManager getSpawnProtectionConfigManager() {
        return spawnProtectionConfigManager;
    }
}