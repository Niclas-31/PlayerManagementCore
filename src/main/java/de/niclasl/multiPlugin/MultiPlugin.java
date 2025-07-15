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
import de.niclasl.multiPlugin.report_system.commands.ReportCommand;
import de.niclasl.multiPlugin.report_system.commands.ReportGuiCommand;
import de.niclasl.multiPlugin.report_system.gui.ReportGui;
import de.niclasl.multiPlugin.report_system.listener.ReportListener;
import de.niclasl.multiPlugin.report_system.manager.ReportManager;
import de.niclasl.multiPlugin.stats.listener.StatsGuiListener;
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
import de.niclasl.multiPlugin.warn_system.manage.WarnManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public final class MultiPlugin extends JavaPlugin {

    // reasons.yml Datei
    private static File reasonsFile;
    private static FileConfiguration reasonsConfig;
    private static List<String> banReasons = new ArrayList<>();
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
        TeleportManager teleportManager = new TeleportManager(getDataFolder());
        MuteManager muteManager = new MuteManager(getChatFilterConfig());
        ReportManager reportManager = new ReportManager(getDataFolder());

        // 3. GUIs
        WarnGui warnGui = new WarnGui(this, warnManager);
        ReportGui reportGui = new ReportGui(this, reportManager);
        BanHistoryGui banHistoryGui = new BanHistoryGui(this);

        // 4. Commands setzen
        Objects.requireNonNull(getCommand("manage")).setExecutor(new PlayerMonitorCommand());
        Objects.requireNonNull(getCommand("vanish")).setExecutor(new VanishCommand(this));
        Objects.requireNonNull(getCommand("ban")).setExecutor(new BanCommand(reasonManager, banHistoryManager));
        Objects.requireNonNull(getCommand("unban")).setExecutor(new UnbanCommand(banHistoryManager));
        Objects.requireNonNull(getCommand("ban-history")).setExecutor(new BanHistoryCommand());
        Objects.requireNonNull(getCommand("teleport-dimension")).setExecutor(new TeleportCommand(teleportManager));
        Objects.requireNonNull(getCommand("gamemode-gui")).setExecutor(new GamemodeCommand());
        Objects.requireNonNull(getCommand("mute")).setExecutor(new MuteCommand(muteManager));
        Objects.requireNonNull(getCommand("unmute")).setExecutor(new UnmuteCommand(muteManager));
        Objects.requireNonNull(getCommand("warn")).setExecutor(new WarnCommand(warnManager, banHistoryManager));
        Objects.requireNonNull(getCommand("warn-history")).setExecutor(new WarnHistoryCommand(warnGui));
        Objects.requireNonNull(getCommand("unwarn")).setExecutor(new UnwarnCommand(warnManager));
        Objects.requireNonNull(getCommand("report")).setExecutor(new ReportCommand(reportManager));
        Objects.requireNonNull(getCommand("report-gui")).setExecutor(new ReportGuiCommand());

        // 5. Listener registrieren
        getServer().getPluginManager().registerEvents(new BanHistoryGuiListener(banHistoryManager), this);
        getServer().getPluginManager().registerEvents(new PlayerMonitorListener(warnGui), this);
        getServer().getPluginManager().registerEvents(new LoginListener(banHistoryManager), this);
        getServer().getPluginManager().registerEvents(new DimensionGuiListener(teleportManager), this);
        getServer().getPluginManager().registerEvents(new GamemodeListener(), this);
        getServer().getPluginManager().registerEvents(new ChatFilterListener(chatFilterConfig, muteManager), this);
        getServer().getPluginManager().registerEvents(new WarnGuiListener(warnManager, warnGui), this);
        getServer().getPluginManager().registerEvents(new StatsGuiListener(), this);
        getServer().getPluginManager().registerEvents(new ReportListener(reportManager, reportGui), this);

        // GUI Init
        GamemodeGui.init(this);
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

    private void createReasonsFile() {
        reasonsFile = new File(getDataFolder(), "reasons.yml");
        if (!reasonsFile.exists()) {
            try {
                reasonsFile.getParentFile().mkdirs();
                reasonsFile.createNewFile();

                // Defaults setzen (nur beim ersten Mal)
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(reasonsFile);
                cfg.set("reasons", List.of("Hacking", "Beleidigung", "Griefing", "Werbung", "X-Ray", "AFK-Bot", "Flyhack", "KillAura", "Bugusing"));
                cfg.save(reasonsFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reasonsConfig = YamlConfiguration.loadConfiguration(reasonsFile);
    }

    // Lade Gründe aus reasons.yml in Liste
    public static void loadReasonsFromFile() {
        if (reasonsConfig.contains("reasons")) {
            banReasons = reasonsConfig.getStringList("reasons");
        }
    }

    // Speichere Gründe aus Liste in die Datei
    public void saveReasonsToFile() {
        reasonsConfig.set("reasons", banReasons);
        try {
            reasonsConfig.save(reasonsFile);
        } catch (IOException e) {
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

                System.out.println("chatfilter.yml erstellt mit Default-Wörtern.");
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

    public FileConfiguration getChatFilterConfig() {
        return chatFilterConfig;
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
