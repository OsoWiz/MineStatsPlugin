package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.util.Game;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PointsCommand extends SpeedrunCommandBase{

    private Game game;
    public PointsCommand(SpeedrunStats plugin, Game game) {
        super(plugin);
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 1)
        {
            sender.sendMessage("Usage: /points <player>");
            return false;
        }
        if( !(sender instanceof Player) ) // if sender is not a player
        {
            return false;
        }
        Player player = (Player) sender;
        String playerName = player.getName();
        SpeedRunner runner = game.getRunnerByName(playerName);
        if(runner == null)
        {
            sender.sendMessage("Player not found");
            return false;
        }
        sender.sendMessage("Player " + playerName + " has " + runner.stats.getPoints() + " points");
        return true;
    }
}
