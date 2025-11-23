package de.niclasl.multiPlugin.mob_system;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class MobUtils {

    public static List<String> getDifficulties(EntityType type) {
        List<String> difficulties = new ArrayList<>();

        // Peaceful
        if (MobCategories.PASSIVE_MOBS.contains(type)
                || MobCategories.NEUTRAL_MOBS.contains(type)
                || MobCategories.CAN_IN_PEACEFUL.contains(type)
                || MobCategories.UNUSED_MOBS.contains(type)
        ) {
            difficulties.add("Peaceful");
            difficulties.add("Easy");
            difficulties.add("Normal");
            difficulties.add("Hard");
        }

        // Hostile Exceptions (neutral in Peaceful)
        if (MobCategories.HOSTILE_EXCEPTIONS_IN_PEACEFUL.contains(type)
                || MobCategories.HOSTILE_MOBS.contains(type)) {
            difficulties.add("Easy");
            difficulties.add("Normal");
            difficulties.add("Hard");
        }

        return difficulties;
    }
}
