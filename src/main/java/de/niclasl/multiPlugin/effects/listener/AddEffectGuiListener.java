package de.niclasl.multiPlugin.effects.listener;

import de.niclasl.multiPlugin.effects.gui.PlayerEffectsGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AddEffectGuiListener implements Listener {

    public static final Map<String, PotionEffectType> EFFECT_MAP = new HashMap<>();
    private static PlayerEffectsGui playerEffectsGui;

    static {
        for (PotionEffectType type : PotionEffectType.values()) {
            if (type == null) continue;
            String key = type.getName();
            if (key.contains(":")) key = key.split(":")[1];

            String displayName = Arrays.stream(key.split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
            EFFECT_MAP.put(displayName, type);
        }
    }

    public AddEffectGuiListener(PlayerEffectsGui playerEffectsGui) {
        AddEffectGuiListener.playerEffectsGui = playerEffectsGui;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;
        Inventory inv = event.getClickedInventory();
        if (inv == null) return;

        if (!event.getView().getTitle().startsWith("§8Add Effect to")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        if (!viewer.hasMetadata("effect_target")) return;
        String uuidStr = viewer.getMetadata("effect_target").getFirst().asString();
        Player target = Bukkit.getPlayer(java.util.UUID.fromString(uuidStr));

        if (clicked.getType() == Material.BARRIER) {
            OfflinePlayer offlineTarget = getTarget(viewer);
            if (offlineTarget instanceof Player t) {
                playerEffectsGui.open(viewer, t);
            } else {
                viewer.sendMessage("§cError: Target player not found.");
                viewer.closeInventory();
            }
        }

        if (clicked.getType() == Material.POTION) {
            if (clicked.getType() != Material.POTION) return;

            String effectName = ChatColor.stripColor(meta.getDisplayName());
            PotionEffectType type = EFFECT_MAP.get(effectName);
            if (type == null) {
                viewer.sendMessage("§cUnknown effect: " + effectName);
                return;
            }

            PotionEffect effect = new PotionEffect(type, 20 * 30, 0);
            assert target != null;
            target.addPotionEffect(effect);

            viewer.sendMessage("§aAdded effect " + effectName + " to " + target.getName());
            viewer.closeInventory();

            playerEffectsGui.open(viewer, target);
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
