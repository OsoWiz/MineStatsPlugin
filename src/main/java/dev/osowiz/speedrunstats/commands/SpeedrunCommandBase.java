package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class SpeedrunCommandBase implements CommandExecutor {

    protected SpeedrunStats plugin;
    public SpeedrunCommandBase(SpeedrunStats plugin) {
        this.plugin = plugin;
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

}
