package de.niclasl.multiPlugin.audit.storage;

import de.niclasl.multiPlugin.audit.model.AuditAction;
import de.niclasl.multiPlugin.audit.model.AuditEntry;
import de.niclasl.multiPlugin.audit.model.AuditType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class YamlAuditStorage implements AuditStorage {

    private static File folder;

    public YamlAuditStorage(File dataFolder) {
        folder = new File(dataFolder, "audits");
        if (!folder.exists()) folder.mkdirs();
    }

    private static File getFile(UUID target) {
        return new File(folder, target + ".yml");
    }

    public static List<AuditEntry> getEntries(UUID targetUUID) {
        File file = getFile(targetUUID);
        if (!file.exists()) return new ArrayList<>();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> rawList = config.getMapList("audits." + UUID.randomUUID());

        List<AuditEntry> entries = new ArrayList<>();
        for (Map<?, ?> raw : rawList) {
            UUID actor = raw.containsKey("actor") && raw.get("actor") instanceof UUID
                    ? (UUID) raw.get("actor") : UUID.randomUUID();

            AuditAction action = raw.containsKey("action") && raw.get("action") instanceof AuditAction
                    ? (AuditAction) raw.get("action") : AuditAction.UNKNOWN;

            AuditType type = raw.containsKey("type") && raw.get("type") instanceof AuditType
                    ? (AuditType) raw.get("type") : AuditType.NONE;

            String reason = raw.containsKey("context") && raw.get("context") instanceof String
                    ? (String) raw.get("context") : "Unknown";

            long time = raw.containsKey("time") && raw.get("time") instanceof Long
                    ? (long) raw.get("time") : 0;

            entries.add(new AuditEntry(targetUUID, type, action, actor, reason, time));
        }
        return entries;
    }

    @Override
    public void add(AuditEntry entry) {
        File file = getFile(entry.getTarget());
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        String path = "audits." + UUID.randomUUID();

        if (entry.getExecutor() != null) {
            cfg.set(path + ".actor", entry.getExecutor().toString());
        }

        cfg.set(path + ".action", entry.getAction().name());
        cfg.set(path + ".type", entry.getType().name());
        cfg.set(path + ".context", entry.getReason());
        cfg.set(path + ".time", entry.getTimestamp());

        try {
            cfg.save(file);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Could not save audit entry for " + entry.getTarget());
            e.printStackTrace();
        }
    }

    @Override
    public List<AuditEntry> getByTarget(UUID target) {
        File file = getFile(target);
        if (!file.exists()) return List.of();

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        var section = cfg.getConfigurationSection("audits");
        if (section == null) return List.of();

        List<AuditEntry> list = new ArrayList<>();

        for (String key : section.getKeys(false)) {
            String base = "audits." + key;

            UUID actor = null;
            String actorStr = cfg.getString(base + ".actor");
            if (actorStr != null) {
                try {
                    actor = UUID.fromString(actorStr);
                } catch (IllegalArgumentException ignored) {}
            }

            AuditAction action = AuditAction.valueOf(
                    cfg.getString(base + ".action", "UNKNOWN")
            );

            AuditType type = AuditType.valueOf(
                    cfg.getString(base + ".type", "GENERAL")
            );

            String reason = cfg.getString(base + ".context", "No reason");
            long time = cfg.getLong(base + ".time", System.currentTimeMillis());

            list.add(new AuditEntry(target, type, action, actor, reason, time));
        }

        list.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return list;
    }

    @Override
    public List<AuditEntry> getByActor(UUID actor) {
        List<AuditEntry> result = new ArrayList<>();

        for (AuditEntry entry : getAll()) {
            if (actor.equals(entry.getExecutor())) {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public List<AuditEntry> getAll() {
        List<AuditEntry> result = new ArrayList<>();

        File[] files = folder.listFiles((d, n) -> n.endsWith(".yml"));
        if (files == null) return result;

        for (File file : files) {
            try {
                UUID target = UUID.fromString(file.getName().replace(".yml", ""));
                result.addAll(getByTarget(target));
            } catch (IllegalArgumentException ignored) {}
        }

        result.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return result;
    }

    @Override
    public void clear(UUID target) {
        File file = getFile(target);
        if (file.exists()) file.delete();
    }
}