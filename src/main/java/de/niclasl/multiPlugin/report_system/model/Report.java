package de.niclasl.multiPlugin.report_system.model;

public class Report {

    private final String id;
    private final String reason;
    private final String time;
    private final String from;
    private final String status;
    private boolean permanent;

    public Report(String id, String reason, String time, String from, String status, boolean permanent) {
        this.id = id;
        this.reason = reason;
        this.time = time;
        this.from = from;
        this.status = status;
        this.permanent = permanent;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public String getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }
}
