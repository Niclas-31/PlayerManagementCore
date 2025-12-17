package de.niclasl.multiPlugin.mob_system;

import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public class MobCategories {

    public static final EnumSet<EntityType> HOSTILE_MOBS = EnumSet.of(
            EntityType.BLAZE, // 1
            EntityType.BOGGED, // 2
            EntityType.BREEZE, // 3
            EntityType.CREAKING, // 4
            EntityType.CREEPER, // 5
            EntityType.ELDER_GUARDIAN, // 6
            EntityType.ENDERMITE, // 7
            EntityType.EVOKER, // 9
            EntityType.GHAST, // 10
            EntityType.GUARDIAN, // 11

            EntityType.HOGLIN, // 12
            EntityType.HUSK, // 13
            EntityType.MAGMA_CUBE, // 14
            EntityType.PARCHED, // 15
            EntityType.PHANTOM, // 16
            EntityType.PIGLIN_BRUTE, // 17
            EntityType.PILLAGER, // 18
            EntityType.RAVAGER, // 19
            EntityType.SHULKER, // 20
            EntityType.SILVERFISH, // 21
            EntityType.SKELETON, // 22

            EntityType.SLIME, // 23
            EntityType.STRAY, // 24
            EntityType.VEX, // 25
            EntityType.VINDICATOR, // 26
            EntityType.WARDEN, // 27
            EntityType.WITCH, // 28
            EntityType.WITHER_SKELETON, // 29
            EntityType.ZOGLIN, // 30
            EntityType.ZOMBIE, // 31
            EntityType.ZOMBIE_VILLAGER // 32
    );

    public static final Set<EntityType> BOSS_MOBS = EnumSet.of(
            EntityType.ENDER_DRAGON, // 33
            EntityType.WITHER // 34
    );

    public static final Set<EntityType> NEUTRAL_MOBS = EnumSet.of(
            EntityType.BEE, // 35
            EntityType.CAVE_SPIDER, // 36
            EntityType.DOLPHIN, // 37
            EntityType.DROWNED, // 38
            EntityType.ENDERMAN, // 39
            EntityType.FOX, // 40
            EntityType.GOAT, // 41
            EntityType.IRON_GOLEM, // 42
            EntityType.LLAMA, // 43

            EntityType.NAUTILUS, // 44
            EntityType.PANDA, // 45
            EntityType.PIGLIN, // 46
            EntityType.POLAR_BEAR, // 47
            EntityType.PUFFERFISH, // 48
            EntityType.SPIDER, // 49
            EntityType.TRADER_LLAMA, // 50
            EntityType.WOLF, // 51
            EntityType.ZOMBIE_NAUTILUS, // 52
            EntityType.ZOMBIFIED_PIGLIN // 53
    );

    public static final Set<EntityType> PASSIVE_MOBS = EnumSet.of(
            EntityType.ALLAY, // 54
            EntityType.ARMADILLO, // 55
            EntityType.AXOLOTL, // 56
            EntityType.BAT, // 57
            EntityType.CAMEL, // 58
            EntityType.CAMEL_HUSK, // 59
            EntityType.CAT, // 60
            EntityType.CHICKEN, // 61
            EntityType.COD, // 62
            EntityType.COPPER_GOLEM, // 63
            EntityType.COW, // 64
            EntityType.DONKEY, // 65

            EntityType.FROG, // 66
            EntityType.GLOW_SQUID, // 67
            EntityType.HAPPY_GHAST, // 68
            EntityType.HORSE, // 69
            EntityType.MOOSHROOM, // 70
            EntityType.MULE, // 71
            EntityType.OCELOT, // 72
            EntityType.PARROT, // 73
            EntityType.PIG, // 74
            EntityType.RABBIT, // 75
            EntityType.SALMON, // 76

            EntityType.SHEEP, // 77
            EntityType.SKELETON_HORSE, // 78
            EntityType.SNIFFER, // 79
            EntityType.SNOW_GOLEM, // 80
            EntityType.SQUID, // 81
            EntityType.STRIDER, // 82
            EntityType.TADPOLE, // 83
            EntityType.TROPICAL_FISH, // 84
            EntityType.TURTLE, // 85
            EntityType.VILLAGER, // 86
            EntityType.WANDERING_TRADER, // 87
            EntityType.ZOMBIE_HORSE // 88
    );
}