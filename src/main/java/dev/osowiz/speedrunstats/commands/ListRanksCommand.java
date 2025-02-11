package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.documents.RunDocument;
import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.util.Rank;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ListRanksCommand extends SpeedrunCommandBase {

    public static final String name = "listranks";
    private Game game;
    public ListRanksCommand(SpeedrunStats plugin, Game game) {
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
        for(Rank rank : Arrays.stream(Rank.values()).filter(rank -> rank.getCode() > 0).toList().reversed())
        {
            player.sendMessage(rank.toString() + " requires " + rank.getRequiredScore() + " points.");
        }
        SpeedRunner runner = game.getRunnerByID(player.getUniqueId());
        if(runner != null)
        {
            player.sendMessage("Your rank is: " + runner.rank);
        }

        return true;
    }
}
