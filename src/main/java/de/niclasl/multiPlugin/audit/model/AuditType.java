package de.niclasl.multiPlugin.audit.model;

public enum AuditType {
    BAN,
    WARN,
    REPORT,
    TELEPORT,
    GENERAL,
    NONE;

    public boolean equalsIgnoreCase(String typeFilter) {
        if (typeFilter == null) return false;
        return this.name().equalsIgnoreCase(typeFilter);
    }

    public String getDisplayName() {
        return switch(this) {
            case BAN -> "Ban";
            case WARN -> "Warn";
            case REPORT -> "Report";
            case TELEPORT -> "Teleport";
            case GENERAL -> "General";
            case NONE -> "None";
        };
    }
}