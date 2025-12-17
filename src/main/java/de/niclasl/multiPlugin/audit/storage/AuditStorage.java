package de.niclasl.multiPlugin.audit.storage;

import de.niclasl.multiPlugin.audit.model.AuditEntry;

import java.util.List;
import java.util.UUID;

public interface AuditStorage {

    void add(AuditEntry entry);

    List<AuditEntry> getByTarget(UUID target);

    List<AuditEntry> getByActor(UUID actor);

    List<AuditEntry> getAll();

    void clear(UUID target);
}