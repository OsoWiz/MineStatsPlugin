package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.documents.RunDocument;
import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RankCommand extends SpeedrunCommandBase {

    public static final String name = "rank";
    private Game game;
    public RankCommand(SpeedrunStats plugin, Game game) {
        super(plugin);
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( !(sender instanceof Player player) ) // if sender is not a player
        {
            return false;
        }
        // get rank values in reverse
        SpeedRunner runner = game.getRunnerByID(player.getUniqueId());
        if(runner != null)
        {
            List<RunDocument> runsOfThePlayer = this.plugin.getConsideredRunsForPlayer(player.getUniqueId());
            if(runsOfThePlayer.isEmpty())
            {
                player.sendMessage("You have no runs considered for ranking.");
                return true;
            }
            player.sendMessage("You have " + runsOfThePlayer.size() + " runs considered for ranking.");
            player.sendMessage("The average score from your runs is " + runsOfThePlayer.stream().map(runDocument -> runDocument.getScore()).reduce(0, (a,b) -> a + b) / runsOfThePlayer.size() + ".");
            player.sendMessage("The weighted score of your runs is " + plugin.getWeightedScore(runsOfThePlayer) + ".");
            player.sendMessage("Your rank is: " + plugin.calculateRank(player.getUniqueId()));
        }

        return true;
    }
}
