package de.niclasl.multiPlugin.mob_system.manager;

import de.niclasl.multiPlugin.mob_system.MobCategories;
import de.niclasl.multiPlugin.mob_system.model.MobSpawnRequest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MobManager {

    private static File mobFolder;
    private static final Map<UUID, MobSpawnRequest> spawnInputMap = new HashMap<>();

    public MobManager(File pluginDataFolder) {
        mobFolder = new File(pluginDataFolder, "mob_spawns");
        if (!mobFolder.exists()) {
            mobFolder.mkdirs();
        }
    }

    private static File getFile(UUID uuid) {
        return new File(mobFolder, uuid.toString() + ".yml");
    }

    public void checkAndCreateDefaultIfAbsent(UUID target) {
        File file = getFile(target);
        if (file.exists()) return; // Datei existiert bereits → nichts tun

        List<MobSpawnRequest> defaultSpawns = new ArrayList<>();

        Set<EntityType> allRelevantMobs = EnumSet.noneOf(EntityType.class);
        allRelevantMobs.addAll(MobCategories.HOSTILE_MOBS);
        allRelevantMobs.addAll(MobCategories.NEUTRAL_MOBS);
        allRelevantMobs.addAll(MobCategories.PASSIVE_MOBS);
        allRelevantMobs.addAll(MobCategories.CAN_HOGLIN_IN_PEACEFUL);

        for (EntityType type : allRelevantMobs) {
            defaultSpawns.add(new MobSpawnRequest(type));
        }

        saveSpawns(target, defaultSpawns);
    }

    public static List<MobSpawnRequest> getRequests(UUID target) {
        File file = getFile(target);

        if (!file.exists()) {
            List<MobSpawnRequest> defaults = createDefaultMobList();
            saveSpawns(target, defaults);
            return defaults;
        }

        List<MobSpawnRequest> loaded = loadSpawns(target);

        // Wenn etwas schieflief oder keines gültigen Mobs drin sind → neu erzeugen
        if (loaded.isEmpty()) {
            List<MobSpawnRequest> defaults = createDefaultMobList();
            saveSpawns(target, defaults);
            return defaults;
        }

        return loaded;
    }

    public static List<MobSpawnRequest> loadSpawns(UUID playerUUID) {
        File file = getFile(playerUUID);
        if (!file.exists()) return new ArrayList<>();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> rawList = config.getMapList("spawns");

        List<MobSpawnRequest> spawns = new ArrayList<>();
        for (Map<?, ?> raw : rawList) {
            String typeName = raw.containsKey("type") && raw.get("type") instanceof String
                    ? (String) raw.get("type") : null;

            if (typeName != null) {
                try {
                    EntityType entityType = EntityType.valueOf(typeName);
                    spawns.add(new MobSpawnRequest(entityType));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Ungültiger EntityType in Datei: " + typeName);
                }
            } else {
                Bukkit.getLogger().warning("Fehlender oder ungültiger 'type' Eintrag in spawns-Datei: " + raw);
            }
        }

        return spawns;
    }

    public static void saveSpawns(UUID uuid, List<MobSpawnRequest> spawns) {
        File file = getFile(uuid);
        YamlConfiguration config = new YamlConfiguration();

        List<Map<String, Object>> spawnList = new ArrayList<>();
        for (MobSpawnRequest spawn : spawns) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", spawn.getEntityType().name());
            spawnList.add(map);
        }

        config.set("spawns", spawnList);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setPendingSpawn(UUID playerUUID, MobSpawnRequest request) {
        spawnInputMap.put(playerUUID, request);
    }

    public static MobSpawnRequest getPendingSpawn(UUID playerUUID) {
        return spawnInputMap.get(playerUUID);
    }

    public static void clearPendingSpawn(UUID playerUUID) {
        spawnInputMap.remove(playerUUID);
    }

    public static List<MobSpawnRequest> createDefaultMobList() {
        Set<EntityType> allRelevantMobs = EnumSet.noneOf(EntityType.class);
        allRelevantMobs.addAll(MobCategories.HOSTILE_MOBS);
        allRelevantMobs.addAll(MobCategories.NEUTRAL_MOBS);
        allRelevantMobs.addAll(MobCategories.PASSIVE_MOBS);
        allRelevantMobs.addAll(MobCategories.CAN_HOGLIN_IN_PEACEFUL);

        return allRelevantMobs.stream()
                .map(MobSpawnRequest::new)
                .toList(); // Java 16+; falls du Java 8 nutzt: collect(Collectors.toList())
    }
}