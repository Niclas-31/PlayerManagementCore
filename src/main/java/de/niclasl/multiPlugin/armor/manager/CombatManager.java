package de.niclasl.multiPlugin.armor.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    // speichert: Spieler → letzte Zeit des Schadens
    private static final Map<UUID, Long> combatMap = new HashMap<>();

    // Wie lange bleibt man im Kampf? (Millisekunden)
    private static final long COMBAT_TIME = 5000; // = 5 Sekunden

    /** Spieler in Kampf setzen */
    public static void tagCombat(Player player) {
        combatMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /** Prüfen, ob Spieler im Kampf ist */
    public static boolean isInCombat(Player player) {
        Long lastHit = combatMap.get(player.getUniqueId());
        if (lastHit == null) return false;

        return (System.currentTimeMillis() - lastHit) < COMBAT_TIME;
    }

    /** Spieler aus Combat entfernen (wenn Zeit vorbei) */
    public static void cleanup() {
        long now = System.currentTimeMillis();

        combatMap.entrySet().removeIf(entry ->
                now - entry.getValue() >= COMBAT_TIME
        );
    }
}
