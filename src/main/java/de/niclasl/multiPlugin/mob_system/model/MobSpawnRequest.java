package de.niclasl.multiPlugin.mob_system.model;

import org.bukkit.entity.EntityType;

public class MobSpawnRequest{

    private final EntityType type;

    public MobSpawnRequest(EntityType type) {
        this.type = type;
    }

    public EntityType getEntityType() {
        return type;
    }

    @Override
    public String toString() {
        return "MobSpawnRequest{" +
                "type=" + type +
                '}';
    }
}
