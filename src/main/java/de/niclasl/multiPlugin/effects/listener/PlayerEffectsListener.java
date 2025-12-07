package de.niclasl.multiPlugin.effects.listener;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.effects.gui.PlayerEffectsGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.UUID;

public class PlayerEffectsListener implements Listener {

    private final MultiPlugin plugin;
    private static PlayerEffectsGui playerEffectsGui;

    public PlayerEffectsListener(MultiPlugin plugin, PlayerEffectsGui playerEffectsGui) {
        PlayerEffectsListener.playerEffectsGui = playerEffectsGui;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;
        if (event.getCurrentItem() == null) return;
        ItemStack clicked = event.getCurrentItem();

        // Prüfen ob es unser GUI ist
        if (event.getView().getTitle().startsWith("§8Effects of")) {
            event.setCancelled(true); // Kein Herausnehmen erlauben

            // Target UUID aus Metadata ziehen
            if (!viewer.hasMetadata("effect_target")) return;
            UUID targetUUID = UUID.fromString(viewer.getMetadata("effect_target").getFirst().asString());
            Player target1 = Bukkit.getPlayer(targetUUID);
            if (target1 == null) return;

            // Klick auf Barrier → zurück
            if (clicked.getType() == Material.BARRIER) {
                // Hole den Zielspieler (du brauchst eine Zuordnung: Wer betrachtet wen)
                OfflinePlayer target = getTarget(viewer); // <- das musst du ggf. anpassen
                if (target != null) {
                    plugin.getWatchGuiManager().open2(viewer, (Player) target);
                } else {
                    viewer.sendMessage("§cError: Target player not found.");
                    viewer.closeInventory();
                }
            }

            // Klick auf Book → neues Effekt-GUI öffnen
            if (clicked.getType() == Material.BOOK) {
                viewer.closeInventory();
                plugin.getAddEffectGui().open(viewer, target1); // <-- musst du bauen
                return;
            }

            // Klick auf Potion → Effekt bearbeiten
            if (clicked.getType() == Material.POTION) {
                String displayName = Objects.requireNonNull(clicked.getItemMeta()).getDisplayName().replace("§d", "");
                // DisplayName wie "Night Vision" → "NIGHT_VISION"
                String effectName = displayName.toUpperCase().replace(" ", "_");
                PotionEffectType type = PotionEffectType.getByName(effectName);
                if (type == null) {
                    viewer.sendMessage("§cUnknown effect: " + displayName);
                    return;
                }

                ClickType click = event.getClick();
                if (click == ClickType.LEFT) {
                    // Effekt entfernen
                    target1.removePotionEffect(type);
                    viewer.sendMessage("§cRemoved effect " + displayName + " from " + target1.getName() + ".");
                } else if (click == ClickType.RIGHT) {
                    // Effekt verlängern (30 Sekunden = 30 * 20 Ticks)
                    PotionEffect old = target1.getPotionEffect(type);
                    int amplifier = old != null ? old.getAmplifier() : 0;
                    int newDuration = (old != null ? old.getDuration() : 0) + (30 * 20);
                    target1.addPotionEffect(new PotionEffect(type, newDuration, amplifier));
                    viewer.sendMessage("§aEffect " + displayName + " extended for 30 seconds.");
                }

                // GUI neu öffnen für Aktualisierung
                playerEffectsGui.open(viewer, target1);
            }
        }
    }

    private OfflinePlayer getTarget(Player viewer) {
        ItemStack nameTag = viewer.getOpenInventory().getTopInventory().getItem(53);
        if (nameTag == null || !nameTag.hasItemMeta()) return null;
        ItemMeta meta = nameTag.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return null;
        return Bukkit.getOfflinePlayer(ChatColor.stripColor(meta.getDisplayName()));
    }
}
