package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PointsCommand extends SpeedrunCommandBase {

    public static final String name = "points";
    private final Game game;
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
        if( !(sender instanceof Player player) ) // if sender is not a player
        {
            return false;
        }
        SpeedRunner runner = game.getRunnerByID(player.getUniqueId());
        if(runner == null)
        {
            sender.sendMessage("Player not found");
            return false;
        }
        sender.sendMessage("Player " + player.getName() + " has " + runner.stats.getPoints() + " points");
        return true;
    }
}
