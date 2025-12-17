package de.niclasl.multiPlugin.audit;

import de.niclasl.multiPlugin.audit.model.AuditAction;
import de.niclasl.multiPlugin.audit.model.AuditEntry;
import de.niclasl.multiPlugin.audit.model.AuditType;
import de.niclasl.multiPlugin.audit.storage.AuditStorage;
import de.niclasl.multiPlugin.audit.storage.YamlAuditStorage;
import de.niclasl.multiPlugin.warn_system.model.Warning;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record AuditManager() {

    private static AuditStorage storage;

    public static void init(File dataFolder) {
        storage = new YamlAuditStorage(dataFolder);
    }

    public static void log(OfflinePlayer target, AuditType type,
                           AuditAction action,Player executor, String reason) {
        long time = System.currentTimeMillis();

        storage.add(new AuditEntry(target.getUniqueId(), type, action, executor.getUniqueId(), reason, time));
    }

    public AuditStorage getStorage() {
        return storage;
    }
}