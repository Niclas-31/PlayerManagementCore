package de.niclasl.multiPlugin.effects.gui;

import de.niclasl.multiPlugin.MultiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record PlayerEffectsGui(MultiPlugin plugin) {

    public void open(Player viewer, Player target) {
        Inventory inv = Bukkit.createInventory(null, 54, "§8Effects of " + target.getName());

        int slot = 0;

        for (PotionEffect effect : target.getActivePotionEffects()) {
            if (slot >= 45) break;

            ItemStack item = createPotionItem(effect);
            inv.setItem(slot, item);
            slot++;
        }

        // Add effect Button
        ItemStack add = new ItemStack(Material.BOOK);
        ItemMeta addMeta = add.getItemMeta();
        assert addMeta != null;
        addMeta.setDisplayName("§aAdd effect");
        add.setItemMeta(addMeta);
        inv.setItem(45, add);

        // Back Button
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        assert backMeta != null;
        backMeta.setDisplayName("§cBack");
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        // Spieler-Head mit Info
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName("§e" + target.getName());
        skullMeta.setLore(List.of("§7Active effects: " + target.getActivePotionEffects().size()));
        skull.setItemMeta(skullMeta);
        inv.setItem(53, skull);

        viewer.openInventory(inv);

        // Metadata speichern, um zu wissen, auf welchen Spieler die GUI sich bezieht
        viewer.setMetadata("effect_target", new FixedMetadataValue(plugin, target.getUniqueId().toString()));
    }

    private ItemStack createPotionItem(PotionEffect effect) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        assert meta != null;

        // Turtle Master erkennen: Slowness IV + Resistance III
        if (effect.getType().equals(PotionEffectType.SLOWNESS) && effect.getAmplifier() == 3) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, effect.getDuration(), 3, false, true, true), true);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.RESISTANCE, effect.getDuration(), 2, false, true, true), true);
            meta.setDisplayName("§dTurtle Master");
        } else {
            // Normale Potion als CustomEffect
            meta.addCustomEffect(new PotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier(), false, true, true), true);

            // Name formatieren
            String displayName = effect.getType().getName();
            if (displayName.contains(":")) displayName = displayName.split(":")[1];
            displayName = Arrays.stream(displayName.split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
            meta.setDisplayName("§d" + displayName);
        }

        meta.setLore(Arrays.asList(
                "§7Duration: " + (effect.getDuration() / 20) + "s",
                "§7Strength: " + (effect.getAmplifier() + 1),
                "§aLeft click: Remove",
                "§eRight click: Extend"
        ));

        potion.setItemMeta(meta);
        return potion;
    }
}