package de.niclasl.multiPlugin.warn_system.manage;

import de.niclasl.multiPlugin.warn_system.model.Warning;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarnManager {

    private final File warnFolder;

    public WarnManager(File pluginDataFolder) {
        this.warnFolder = new File(pluginDataFolder, "warnings");
        if (!warnFolder.exists()) {
            warnFolder.mkdirs();
        }
    }

    private File getFile(UUID uuid) {
        return new File(warnFolder, uuid.toString() + ".yml");
    }

    public List<Warning> getWarnings(UUID playerUUID) {
        File file = getFile(playerUUID);
        if (!file.exists()) return new ArrayList<>();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> rawList = config.getMapList("warnings");

        List<Warning> warnings = new ArrayList<>();
        for (Map<?, ?> raw : rawList) {
            String id = raw.containsKey("id") && raw.get("id") instanceof String
                    ? (String) raw.get("id") : UUID.randomUUID().toString();

            String reason = raw.containsKey("reason") && raw.get("reason") instanceof String
                    ? (String) raw.get("reason") : "Unknown";

            String date = raw.containsKey("date") && raw.get("date") instanceof String
                    ? (String) raw.get("date") : "Unknown";

            String from = raw.containsKey("by") && raw.get("by") instanceof String
                    ? (String) raw.get("by") : "System";

            boolean permanent = raw.containsKey("permanent") && raw.get("permanent") instanceof Boolean
                    ? (Boolean) raw.get("permanent") : false;

            int points = 0;
            if (raw.containsKey("points") && raw.get("points") instanceof Number) {
                points = ((Number) raw.get("points")).intValue();
            }

            warnings.add(new Warning(id, reason, date, from, permanent, points));
        }
        return warnings;
    }

    public void saveWarnings(UUID playerUUID, List<Warning> warnings) {
        File file = getFile(playerUUID);
        YamlConfiguration config = new YamlConfiguration();

        List<Map<String, Object>> warnList = new ArrayList<>();
        for (Warning warning : warnings) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", warning.getId());
            map.put("reason", warning.getReason()); // oder getReason(), je nach deiner Klasse
            map.put("date", warning.getDate());
            map.put("by", warning.getFrom());
            map.put("permanent", warning.isPermanent());
            map.put("points", warning.getPoints());
            warnList.add(map);
        }

        config.set("warnings", warnList);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWarning(UUID playerUUID, String reason, String by, String date) {
        List<Warning> warnings = getWarnings(playerUUID);

        Warning warning = new Warning(UUID.randomUUID().toString(), reason, date, by, false, 3);

        warnings.add(warning);
        saveWarnings(playerUUID, warnings);
    }

    public void removeWarning(UUID playerUUID, int index) {
        List<Warning> warnings = getWarnings(playerUUID);
        if (index >= 0 && index < warnings.size()) {
            warnings.remove(index);
            saveWarnings(playerUUID, warnings);
        }
    }

    public int getTotalPoints(UUID uuid) {
        List<Warning> warnings = getWarnings(uuid);
        int total = 0;
        for (Warning w : warnings) {
            total += w.getPoints();
        }
        return total;
    }
}