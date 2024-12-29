package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.util.Game;
import dev.osowiz.speedrunstats.util.Rank;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RankCommand extends SpeedrunCommandBase {

    private Game game;
    public RankCommand(SpeedrunStats plugin, Game game) {
        super(plugin);
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( !(sender instanceof Player) ) // if sender is not a player
        {
            return false;
        }
        Player player = (Player) sender;
        // get rank values in reverse

        for(Rank rank : Arrays.stream(Rank.values()).collect(Collectors.toList()).reversed()) {
            player.sendMessage(rank.toString());
        }
        SpeedRunner runner = game.getRunnerByID(player.getUniqueId());
        if(runner != null)
        {
            player.sendMessage("Your rank is: " + runner.rank);
        }

        return true;
    }
}
