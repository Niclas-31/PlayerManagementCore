package de.niclasl.multiPlugin.ban_system.manager;

import de.niclasl.multiPlugin.ban_system.model.BanRecord;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BanHistoryManager {

    private static File folder;

    public BanHistoryManager(File dataFolder) {
        folder = new File(dataFolder, "bans");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static File getPlayerFile(UUID uuid) {
        return new File(folder, uuid.toString() + ".yml");
    }

    public void removeBanHistory(UUID playerUUID, int index) {
        List<BanRecord> bans = getBanHistory(playerUUID);
        if (index >= 0 && index < bans.size()) {
            bans.remove(index);
            saveBanHistory(playerUUID, bans);
        }
    }

    public void saveBanHistory(UUID uuid, List<BanRecord> bans) {
        File file = getPlayerFile(uuid);
        YamlConfiguration config = new YamlConfiguration();

        List<Map<String, Object>> serialized = new ArrayList<>();
        for (BanRecord record : bans) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", record.getId());
            map.put("reason", record.getReason());
            map.put("by", record.getBy());
            map.put("date", record.getDate());
            map.put("permanent", record.isPermanent());
            map.put("duration", record.getDuration());
            map.put("unbanDate", record.getUnbanDate());
            map.put("unbanBy", record.getUnbanBy());
            serialized.add(map);
        }

        config.set("bans", serialized);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<BanRecord> getBanHistory(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (!file.exists()) return new ArrayList<>();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> rawList = config.getMapList("bans");

        List<BanRecord> history = new ArrayList<>();
        for (Map<?, ?> raw : rawList) {
            String id = raw.containsKey("id") && raw.get("id") instanceof String
                    ? (String) raw.get("id") : UUID.randomUUID().toString();

            String reason = raw.containsKey("reason") && raw.get("reason") instanceof String
                    ? (String) raw.get("reason") : "Unknown";

            String date = raw.containsKey("date") && raw.get("date") instanceof String
                    ? (String) raw.get("date") : "Unknown";

            String by = raw.containsKey("by") && raw.get("by") instanceof String
                    ? (String) raw.get("by") : "System";

            boolean permanent = raw.containsKey("permanent") && raw.get("permanent") instanceof Boolean
                    ? (Boolean) raw.get("permanent") : false;

            String duration = raw.containsKey("duration") && raw.get("duration") instanceof String
                    ? (String) raw.get("duration") : null;

            String unbanDate = raw.containsKey("unbanDate") && raw.get("unbanDate") instanceof String
                    ? (String) raw.get("unbanDate") : null;

            String unbanBy = raw.containsKey("unbanBy") && raw.get("unbanBy") instanceof String
                    ? (String) raw.get("unbanBy") : null;

            BanRecord record = new BanRecord(id, reason, by, date, duration, permanent, unbanDate, unbanBy);
            history.add(record);
        }
        return history;
    }

    public void addBan(UUID uuid, String reason, String by, String duration, String unbanBy, String unbanDate) {
        List<BanRecord> history = getBanHistory(uuid);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        String id = UUID.randomUUID().toString();
        BanRecord record = new BanRecord(id, reason, by, now, duration, false, unbanBy, unbanDate);
        BanRecord newBan = new BanRecord(id, reason, by, now, duration, false, unbanBy, unbanDate);
        record.setDuration(duration);

        history.add(newBan);
        saveBanHistory(uuid, history);
    }

    public void updateLastBanWithUnban(UUID uuid, String unbanBy) {
        List<BanRecord> history = getBanHistory(uuid);
        if (history.isEmpty()) return;

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        for (int i = history.size() - 1; i >= 0; i--) {
            BanRecord record = history.get(i);
            if (record.getUnbanDate() == null) { // noch nicht entbannt
                record.setUnbanDate(now);
                record.setUnbanBy(unbanBy);
                break;
            }
        }

        saveBanHistory(uuid, history);
    }
}