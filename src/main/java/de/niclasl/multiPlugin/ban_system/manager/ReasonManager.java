package de.niclasl.multiPlugin.ban_system.manager;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

public class ReasonManager {

    private static List<String> reasons;

    public ReasonManager(FileConfiguration config) {
        reasons = config.getStringList("reasons");
    }

    public boolean isValidReason(String reason) {
        return reasons.stream().anyMatch(r -> r.equalsIgnoreCase(reason));
    }

    public List<String> getBanReasons() {
        return reasons;
    }
}
