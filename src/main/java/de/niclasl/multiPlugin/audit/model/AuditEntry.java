package de.niclasl.multiPlugin.audit.model;

import java.util.UUID;

public class AuditEntry {

    private final UUID target;
    private final AuditType type;
    private final AuditAction action;
    private final UUID executor;
    private final String reason;
    private final long timestamp;

    public AuditEntry(
            UUID target,
            AuditType type,
            AuditAction action,
            UUID executor,
            String reason,
            long timestamp
    ) {
        // Admin / Console
        this.target = target;
        this.type= type;
        this.action = action;
        this.executor = executor;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public UUID getTarget() {
        return target;
    }

    public AuditType getType() {
        return type;
    }

    public UUID getExecutor() {
        return executor;
    }

    public Object getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public AuditAction getAction() {
        return action;
    }
}
