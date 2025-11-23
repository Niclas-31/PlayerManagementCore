package de.niclasl.multiPlugin.mob_system;

import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public class MobCategories {

    public static final Set<EntityType> UNUSED_MOBS = EnumSet.of(

            EntityType.ZOMBIE_HORSE // 33
    );

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
            EntityType.HUSK, // 13
            EntityType.MAGMA_CUBE, // 14
            EntityType.PHANTOM, // 15
            EntityType.PIGLIN_BRUTE, // 16
            EntityType.PILLAGER, // 17
            EntityType.RAVAGER, // 18
            EntityType.SHULKER, // 19
            EntityType.SILVERFISH, // 20
            EntityType.SKELETON, // 21
            EntityType.SLIME, // 22
            EntityType.STRAY, // 23
            EntityType.VEX, // 24
            EntityType.VINDICATOR, // 25
            EntityType.WARDEN, // 26
            EntityType.WITCH, // 27
            EntityType.WITHER, // 28
            EntityType.WITHER_SKELETON, // 29
            EntityType.ZOGLIN, // 30
            EntityType.ZOMBIE, // 31
            EntityType.ZOMBIE_VILLAGER // 32
    );

    public static final Set<EntityType> NEUTRAL_MOBS = EnumSet.of(
            EntityType.BEE, // 34
            EntityType.DOLPHIN, // 36
            EntityType.FOX, // 39
            EntityType.GOAT, // 40
            EntityType.IRON_GOLEM, // 41
            EntityType.LLAMA, // 42
            EntityType.PANDA, // 43
            EntityType.PIGLIN, // 44
            EntityType.POLAR_BEAR, // 45
            EntityType.TRADER_LLAMA, // 47
            EntityType.WOLF // 48
    );

    public static final Set<EntityType> PASSIVE_MOBS = EnumSet.of(
            EntityType.ALLAY, // 50
            EntityType.ARMADILLO, // 51
            EntityType.AXOLOTL, // 52
            EntityType.BAT, // 53
            EntityType.CAMEL, // 54
            EntityType.CAT, // 55
            EntityType.CHICKEN, // 56
            EntityType.COD, // 57
            EntityType.COW, // 58
            EntityType.DONKEY, // 59
            EntityType.FROG, // 60
            EntityType.GLOW_SQUID, // 61
            EntityType.HAPPY_GHAST, // 62
            EntityType.HORSE, // 63
            EntityType.MOOSHROOM, // 64
            EntityType.MULE, // 65
            EntityType.OCELOT, // 66
            EntityType.PARROT, // 67
            EntityType.PIG, // 68
            EntityType.PUFFERFISH, // 69
            EntityType.RABBIT, // 70
            EntityType.SALMON, // 71
            EntityType.SHEEP, // 72
            EntityType.SKELETON_HORSE, // 73
            EntityType.SNIFFER, // 74
            EntityType.SNOW_GOLEM, // 75
            EntityType.SQUID, // 76
            EntityType.STRIDER, // 77
            EntityType.TADPOLE, // 78
            EntityType.TROPICAL_FISH, // 79
            EntityType.TURTLE, // 80
            EntityType.VILLAGER, // 81
            EntityType.WANDERING_TRADER // 82
    );

    public static final Set<EntityType> HOSTILE_EXCEPTIONS_IN_PEACEFUL = Set.of(
            EntityType.ENDERMAN, // 38
            EntityType.SPIDER, // 46
            EntityType.CAVE_SPIDER, // 35
            EntityType.DROWNED, // 37
            EntityType.ZOMBIFIED_PIGLIN, // 49
            EntityType.GIANT, // 83
            EntityType.ILLUSIONER // 84
    );

    public static final Set<EntityType> CAN_IN_PEACEFUL = Set.of(
            EntityType.ENDER_DRAGON, // 8
            EntityType.HOGLIN // 12
    );
}
