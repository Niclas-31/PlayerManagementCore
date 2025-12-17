package de.niclasl.multiPlugin.effects.gui;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record AddEffectGui(MultiPlugin plugin) {

    private static final Set<PotionEffectType> CREATIVE_POTIONS = Set.of(
            PotionEffectType.NIGHT_VISION,
            PotionEffectType.INVISIBILITY,
            PotionEffectType.JUMP_BOOST,
            PotionEffectType.FIRE_RESISTANCE,
            PotionEffectType.SPEED,
            PotionEffectType.SLOWNESS,
            PotionEffectType.WATER_BREATHING,
            PotionEffectType.INSTANT_HEALTH,
            PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.POISON,
            PotionEffectType.REGENERATION,
            PotionEffectType.STRENGTH,
            PotionEffectType.WEAKNESS,
            PotionEffectType.LUCK,
            PotionEffectType.SLOW_FALLING,
            PotionEffectType.WIND_CHARGED,
            PotionEffectType.WEAVING,
            PotionEffectType.OOZING,
            PotionEffectType.INFESTED
    );

    public void open(Player viewer, Player target) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Add Effect to " + target.getName());

        int slot = 0;
        for (PotionEffectType type : CREATIVE_POTIONS) {
            if (type == null) continue;
            if (slot >= 45) break;

            ItemStack potion = createPotionItem(type);
            inv.setItem(slot++, potion);
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName("§e" + target.getName());
        skullMeta.setLore(List.of("§7Choose an effect to apply."));
        skull.setItemMeta(skullMeta);
        inv.setItem(53, skull);

        viewer.openInventory(inv);
        viewer.setMetadata("effect_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
    }

    private static ItemStack createPotionItem(PotionEffectType type) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        assert meta != null;

        if (type.equals(PotionEffectType.SLOWNESS)) {
            meta.setDisplayName("§dTurtle Master");
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 3, false, true, true), true);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, 400, 2, false, true, true), true);
            meta.setLore(Arrays.asList(
                    "§7Grants Slowness IV + Resistance III",
                    "§7Duration: 20s",
                    "§7Strength: 1",
                    "§7Click to add effect"
            ));
        } else {
            meta.addCustomEffect(new PotionEffect(type, 600, 0, false, true, true), true);
            String displayName = type.getName();
            if (displayName.contains(":")) displayName = displayName.split(":")[1];
            displayName = Arrays.stream(displayName.split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
            meta.setDisplayName("§a" + displayName);
            meta.setLore(Arrays.asList(
                    "§7Click to add effect",
                    "§fDuration: 30s",
                    "§fLevel: 1"
            ));
        }

        potion.setItemMeta(meta);
        return potion;
    }
}
