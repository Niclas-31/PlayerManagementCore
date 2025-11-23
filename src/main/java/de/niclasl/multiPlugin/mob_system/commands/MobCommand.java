package de.niclasl.multiPlugin.mob_system.commands;

import de.niclasl.multiPlugin.mob_system.gui.MobGui;
import de.niclasl.multiPlugin.mob_system.manager.MobManager;
import de.niclasl.multiPlugin.mob_system.model.MobSpawnRequest;
import de.niclasl.multiPlugin.GuiConstants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class MobCommand implements CommandExecutor, TabCompleter {

    private static MobGui mobGui;

    public MobCommand(MobGui mobGui) {
        MobCommand.mobGui = mobGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.mob")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUsage: /mob <player> [page]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }

        int page = 1; // default
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid page number.");
                return true;
            }
        }

        // --- Bestimme die Anzahl der Seiten (basierend auf tatsächlichen Requests) ---
        List<MobSpawnRequest> requests = MobManager.getRequests(target.getUniqueId());
        int mobsPerPage = GuiConstants.ALLOWED_SLOTS.length;
        int totalPages = (int) Math.ceil(requests.size() / (double) mobsPerPage);
        if (totalPages < 1) totalPages = 1; // fallback: mindestens Seite 1

        // --- Validierung: nur erlaubte Seiten zulassen ---
        if (page < 1 || page > totalPages) {
            player.sendMessage("§cPage not found. There are only §e" + totalPages + " §cPage(s).");
            return true;
        }

        // --- Metadaten setzen & GUI öffnen ---
        player.setMetadata("mob_target", new FixedMetadataValue(MobGui.plugin, target.getUniqueId().toString()));
        player.setMetadata("mob_page", new FixedMetadataValue(MobGui.plugin, page));
        mobGui.open(player, target, page);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Spieler-Vorschläge (bereits vorhanden)
            String partial = args[0].toLowerCase();
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                String name = p.getName();
                if (name != null && name.toLowerCase().startsWith(partial)) {
                    completions.add(name);
                }
            }
            return completions;
        }

        if (args.length == 2) {
            // Wenn ein (exakter) Spielername vorhanden ist, schlage gültige Seiten vor
            String playerName = args[0];
            OfflinePlayer target = null;
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (p.getName() != null && p.getName().equalsIgnoreCase(playerName)) {
                    target = p;
                    break;
                }
            }

            if (target == null) return completions;

            List<MobSpawnRequest> requests = MobManager.getRequests(target.getUniqueId());
            int mobsPerPage = GuiConstants.ALLOWED_SLOTS.length;
            int totalPages = (int) Math.ceil(requests.size() / (double) mobsPerPage);
            if (totalPages < 1) totalPages = 1;

            String partialPage = args[1].toLowerCase();
            for (int i = 1; i <= totalPages; i++) {
                String s = String.valueOf(i);
                if (partialPage.isEmpty() || s.startsWith(partialPage)) completions.add(s);
            }
        }

        return completions;
    }
}