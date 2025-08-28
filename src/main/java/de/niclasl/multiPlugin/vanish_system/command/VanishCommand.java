package de.niclasl.multiPlugin.vanish_system.command;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.vanish_system.manager.VanishManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private static MultiPlugin plugin;
    private static VanishManager vanishManager;

    public VanishCommand(MultiPlugin plugin, VanishManager vanishManager) {
        VanishCommand.plugin = plugin;
        VanishCommand.vanishManager = vanishManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
}
