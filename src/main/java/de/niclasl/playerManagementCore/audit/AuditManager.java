package de.niclasl.playerManagementCore.audit;

import de.niclasl.playerManagementCore.audit.model.AuditAction;
import de.niclasl.playerManagementCore.audit.model.AuditEntry;
import de.niclasl.playerManagementCore.audit.model.AuditType;
import de.niclasl.playerManagementCore.audit.storage.AuditStorage;
import de.niclasl.playerManagementCore.audit.storage.YamlAuditStorage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;

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