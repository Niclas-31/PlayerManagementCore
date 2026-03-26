package de.niclasl.playerManagementCore.vanish_system.command;

import de.niclasl.playerManagementCore.vanish_system.manager.VanishManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class VanishCommand implements CommandExecutor, TabCompleter {

    private static VanishManager vanishManager;

    public VanishCommand(VanishManager vanishManager) {
        VanishCommand.vanishManager = vanishManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!sender.hasPermission("vanish")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
        }

        boolean isVanished = vanishManager.getVanishConfig().getBoolean(player.getUniqueId().toString(), false);
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
