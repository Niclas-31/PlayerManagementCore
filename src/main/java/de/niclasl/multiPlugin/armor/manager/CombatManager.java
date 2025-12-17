package de.niclasl.multiPlugin.armor.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private static final Map<UUID, Long> combatMap = new HashMap<>();

    private static final long COMBAT_TIME = 5000;

    public static void tagCombat(Player player) {
        combatMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public static boolean isInCombat(Player player) {
        Long lastHit = combatMap.get(player.getUniqueId());
        if (lastHit == null) return false;

        return (System.currentTimeMillis() - lastHit) < COMBAT_TIME;
    }

    public static void cleanup() {
        long now = System.currentTimeMillis();

        combatMap.entrySet().removeIf(entry ->
                now - entry.getValue() >= COMBAT_TIME
        );
    }
}
