package de.niclasl.multiPlugin.filter.manager;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteManager {

    private final Map<UUID, Long> mutedPlayers = new HashMap<>();

    public MuteManager(FileConfiguration chatFilterConfig) {
    }

    /**
     * Spieler muten für Dauer in Millisekunden
     * Dauer == 0 bedeutet permanent
     */
    public void mutePlayer(UUID uuid, long durationMillis) {
        long unmuteTime = durationMillis == 0 ? Long.MAX_VALUE : System.currentTimeMillis() + durationMillis;
        mutedPlayers.put(uuid, unmuteTime);
    }

    public boolean isMuted(UUID uuid) {
        if (!mutedPlayers.containsKey(uuid)) return false;

        long unmuteTime = mutedPlayers.get(uuid);
        if (System.currentTimeMillis() > unmuteTime) {
            mutedPlayers.remove(uuid);
            return false;
        }
        return true;
    }

    public void unmutePlayer(UUID uuid) {
        mutedPlayers.remove(uuid);
    }
}
