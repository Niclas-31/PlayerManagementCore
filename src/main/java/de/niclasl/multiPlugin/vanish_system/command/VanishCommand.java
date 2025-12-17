package de.niclasl.multiPlugin.vanish_system.command;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.vanish_system.manager.VanishManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class VanishCommand implements CommandExecutor, TabCompleter {

    private static MultiPlugin plugin;
    private static VanishManager vanishManager;

    public VanishCommand(MultiPlugin plugin, VanishManager vanishManager) {
        VanishCommand.plugin = plugin;
        VanishCommand.vanishManager = vanishManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("multiplugin.vanish")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
        }

        boolean isVanished = plugin.getVanishConfig().getBoolean(player.getUniqueId().toString(), false);
        boolean newVanishState = !isVanished;

        vanishManager.setVanish(player.getUniqueId(), newVanishState);

        player.sendMessage(newVanishState
                ? "§aYou are now vanished."
                : "§cYou are no longer vanished.");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NonNull [] strings) {
        return List.of();
    }
}
