package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommand extends SpeedrunCommandBase {


    public static final String name = "start";

    public StartCommand(SpeedrunStats plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp())
        {
            sender.sendMessage("You must be an operator to start the game");
            return false;
        }
        for(String arg : args)
        {
            sender.getServer().broadcastMessage(arg);
        }
        this.plugin.startGame(args);

        return true;
    }

}
