package de.niclasl.multiPlugin.report_system.manager;

import de.niclasl.multiPlugin.report_system.model.Report;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.UUID.*;

public class ReportManager {

    private static File reportFolder;

    public ReportManager(File pluginDataFolder) {
        reportFolder = new File(pluginDataFolder, "reports");
        if (!reportFolder.exists()) {
            reportFolder.mkdirs();
        }
    }

    private File getFile(UUID uuid) {
        return new File(reportFolder, uuid.toString() + ".yml");
    }

    public List<Report> getReports(UUID playerUUID) {
        File file = getFile(playerUUID);
        if (!file.exists()) return new ArrayList<>();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> reportList = config.getMapList("reports");

        List<Report> reports = new ArrayList<>();
        for (Map<?, ?> raw : reportList) {
            String id = raw.containsKey("id") && raw.get("id") instanceof String
                    ? (String) raw.get("id") : UUID.randomUUID().toString();

            String reason = raw.containsKey("reason") && raw.get("reason") instanceof String
                    ? (String) raw.get("reason") : "Unknown";

            String time = raw.containsKey("time") && raw.get("time") instanceof String
                    ? (String) raw.get("time") : "Unknown";

            String from = raw.containsKey("reporter") && raw.get("reporter") instanceof String
                    ? (String) raw.get("reporter") : "System";

            String status = raw.containsKey("status") && raw.get("status") instanceof String
                    ? (String) raw.get("status") : "";

            boolean permanent = raw.containsKey("permanent") && raw.get("permanent") instanceof Boolean
                    ? (Boolean) raw.get("permanent") : false;

            reports.add(new Report(id, reason, time, from, status, permanent));
        }
        return reports;
    }

    public void saveReports(UUID playerUUID, List<Report> reports) {
        File file = getFile(playerUUID);
        YamlConfiguration config = new YamlConfiguration();

        List<Map<String, Object>> reportList = new ArrayList<>();
        for (Report report : reports) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", report.getId());
            map.put("reason", report.getReason());
            map.put("time", report.getTime());
            map.put("reporter", report.getFrom());
            map.put("status", report.getStatus());
            reportList.add(map);
        }

        config.set("reports", reportList);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addReport(UUID uniqueId, String reason, String name, String time, String status, boolean permanent) {
        List<Report> reports = getReports(uniqueId);

        Report report = new Report(randomUUID().toString(), reason, time, name, status, false);

        reports.add(report);
        saveReports(uniqueId, reports);
    }

    public void removeReport(UUID playerUUID, int index) {
        List<Report> reports = getReports(playerUUID);
        if (index >= 0 && index < reports.size()) {
            reports.remove(index);
            saveReports(playerUUID, reports);
        }
    }
}