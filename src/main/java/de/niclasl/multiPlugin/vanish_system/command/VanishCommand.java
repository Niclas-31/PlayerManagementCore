package de.niclasl.multiPlugin.vanish_system.command;

import de.niclasl.multiPlugin.MultiPlugin;
import de.niclasl.multiPlugin.vanish_system.manager.VanishManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    private final MultiPlugin plugin;

    public VanishCommand(MultiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        boolean isVanished = plugin.getVanishConfig().getBoolean(player.getUniqueId().toString(), false);
        boolean newVanishState = !isVanished;

        VanishManager.setVanish(player.getUniqueId(), newVanishState);

        player.sendMessage(newVanishState
                ? "§aYou are now vanished."
                : "§cYou are no longer vanished.");

        return true;
    }
}
