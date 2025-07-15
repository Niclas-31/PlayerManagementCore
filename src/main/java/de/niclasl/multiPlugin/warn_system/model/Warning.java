package de.niclasl.multiPlugin.warn_system.model;

import java.lang.annotation.Annotation;

public class Warning implements org.bukkit.Warning {
    private final String id;
    private final String reason;
    private final String date;
    private final String from;
    private boolean permanent;
    private int points;

    public Warning(String id, String reason, String date, String from, boolean permanent, int points) {
        this.id = id;
        this.reason = reason;
        this.date = date;
        this.from = from;
        this.permanent = permanent;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public String getDate() {
        return date;
    }

    public String getFrom() {
        return from;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    @Override
    public boolean value() {
        return false;
    }

    @Override
    public String reason() {
        return "";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
