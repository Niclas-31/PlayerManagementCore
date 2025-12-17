package de.niclasl.multiPlugin.audit.model;

public enum AuditAction {

    ADD,
    REMOVE,
    EXECUTE,
    UNKNOWN;

    public boolean equalsIgnoreCase(String value) {
        return name().equalsIgnoreCase(value);
    }

    public String getDisplayName() {
        return switch(this) {
            case ADD -> "Add";
            case REMOVE -> "Remove";
            case EXECUTE -> "Execute";
            case UNKNOWN -> "Unknown";
        };
    }
}