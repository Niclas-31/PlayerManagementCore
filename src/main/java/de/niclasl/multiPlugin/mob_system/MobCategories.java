package de.niclasl.multiPlugin.mob_system;

import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public class MobCategories {

    public static final Set<EntityType> UNUSED_MOBS = EnumSet.of(

            EntityType.ZOMBIE_HORSE
    );

    public static final EnumSet<EntityType> HOSTILE_MOBS = EnumSet.of(
            EntityType.BLAZE,
            EntityType.BOGGED,
            EntityType.BREEZE,
            EntityType.CREAKING,
            EntityType.CREEPER,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDERMITE,
            EntityType.ENDER_DRAGON,
            EntityType.EVOKER,
            EntityType.GHAST,
            EntityType.GUARDIAN,
            EntityType.HUSK,
            EntityType.MAGMA_CUBE,
            EntityType.PHANTOM,
            EntityType.PIGLIN_BRUTE,
            EntityType.PILLAGER,
            EntityType.RAVAGER,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SLIME,
            EntityType.STRAY,
            EntityType.VEX,
            EntityType.VINDICATOR,
            EntityType.WARDEN,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.ZOGLIN,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_VILLAGER
    );

    public static final Set<EntityType> NEUTRAL_MOBS = EnumSet.of(
            EntityType.BEE,
            EntityType.DOLPHIN,
            EntityType.FOX,
            EntityType.GOAT,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.PANDA,
            EntityType.PIGLIN,
            EntityType.POLAR_BEAR,
            EntityType.TRADER_LLAMA,
            EntityType.WOLF
    );

    public static final Set<EntityType> PASSIVE_MOBS = EnumSet.of(
            EntityType.ALLAY,
            EntityType.ARMADILLO,
            EntityType.AXOLOTL,
            EntityType.CAMEL,
            EntityType.CAT,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.DONKEY,
            EntityType.FROG,
            EntityType.GLOW_SQUID,
            EntityType.HAPPY_GHAST,
            EntityType.HORSE,
            EntityType.MOOSHROOM,
            EntityType.MULE,
            EntityType.OCELOT,
            EntityType.PARROT,
            EntityType.PIG,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SKELETON_HORSE,
            EntityType.SNIFFER,
            EntityType.SNOW_GOLEM,
            EntityType.SQUID,
            EntityType.STRIDER,
            EntityType.TADPOLE,
            EntityType.TROPICAL_FISH,
            EntityType.TURTLE,
            EntityType.VILLAGER,
            EntityType.WANDERING_TRADER
    );

    public static final Set<EntityType> HOSTILE_EXCEPTIONS_IN_PEACEFUL = Set.of(
            EntityType.ENDERMAN,
            EntityType.SPIDER,
            EntityType.CAVE_SPIDER,
            EntityType.DROWNED,
            EntityType.ZOMBIFIED_PIGLIN
    );

    public static final Set<EntityType> CAN_HOGLIN_IN_PEACEFUL = Set.of(
            EntityType.HOGLIN
    );
}
