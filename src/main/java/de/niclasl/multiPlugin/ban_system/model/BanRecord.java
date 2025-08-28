package de.niclasl.multiPlugin.ban_system.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanRecord {
    private final String id;
    private final String reason;
    private final String by;
    private final String date;
    private String duration;
    private boolean permanent;
    private String unbanDate;  // kann null sein
    private String unbanBy;    // kann null sein

    public BanRecord(String id, String reason, String by, String date, String duration, boolean permanent, String unbanDate, String unbanBy) {
        this.id = id;
        this.reason = reason;
        this.by = by;
        this.date = date;
        this.duration = duration;
        this.permanent = permanent;
        this.unbanDate = unbanDate;
        this.unbanBy = unbanBy;
    }

    // Getter und Setter für alle Felder

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public String getBy() {
        return by;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public String getUnbanDate() {
        return unbanDate;
    }

    public String getUnbanBy() {
        return unbanBy;
    }

    public void setPermanent(boolean b){
        this.permanent = b;
    }

    public void setDuration(String duration){
        this.duration = duration;
    }

    public void setUnbanDate(String unbanDate) {
        this.unbanDate = unbanDate;
    }

    public void setUnbanBy(String unbanBy) {
        this.unbanBy = unbanBy;
    }
    public String calculateExpectedUnbanDate() {
        if (this.date == null || this.duration == null || this.duration.isEmpty()) return null;

        try {
            // Datum des Bans parsen
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            LocalDateTime banTime = LocalDateTime.parse(this.date, formatter);

            // Dauer parsen
            Pattern pattern = Pattern.compile("(\\d+)([smhd])");
            Matcher matcher = pattern.matcher(duration);

            while (matcher.find()) {
                int value = Integer.parseInt(matcher.group(1));
                banTime = switch (matcher.group(2)) {
                    case "s" -> banTime.plusSeconds(value);
                    case "m" -> banTime.plusMinutes(value);
                    case "h" -> banTime.plusHours(value);
                    case "d" -> banTime.plusDays(value);
                    case "w" -> banTime.plusWeeks(value);
                    case "M" -> banTime.plusMonths(value);
                    case "y" -> banTime.plusYears(value);
                    default -> banTime;
                };
            }

            return formatter.format(banTime);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isExpired() {
        if (duration == null || date == null) return false;
        String expectedUnban = calculateExpectedUnbanDate();
        if (expectedUnban == null) return false;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime unbanTime = LocalDateTime.parse(expectedUnban, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        return now.isAfter(unbanTime);
    }
}