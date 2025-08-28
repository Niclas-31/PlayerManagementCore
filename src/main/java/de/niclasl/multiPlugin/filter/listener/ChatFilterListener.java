package de.niclasl.multiPlugin.filter.listener;

import de.niclasl.multiPlugin.filter.manager.MuteManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatFilterListener implements Listener {

    private static FileConfiguration chatFilterConfig;
    private static MuteManager muteManager;

    public ChatFilterListener(FileConfiguration chatFilterConfig, MuteManager muteManager) {
        ChatFilterListener.chatFilterConfig = chatFilterConfig;
        ChatFilterListener.muteManager = muteManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Prüfen, ob Spieler gemutet ist
        if (muteManager.isMuted(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are muted and cannot chat.");
            event.setCancelled(true);
            return;
        }

        String message = event.getMessage().toLowerCase();
        List<String> bannedWords = chatFilterConfig.getStringList("banned-words");

        for (String word : bannedWords) {
            if (message.contains(word.toLowerCase())) {
                player.sendMessage(ChatColor.RED + "Your message contains forbidden words.");
                event.setCancelled(true);
                return;
            }
        }
    }
}
