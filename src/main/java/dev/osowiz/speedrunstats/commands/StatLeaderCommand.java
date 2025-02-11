package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.enums.Statistic;
import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class StatLeaderCommand extends SpeedrunCommandBase {

    public static String name = "leaders";

    private Game game;
    public StatLeaderCommand(SpeedrunStats plugin, Game game) {
        super(plugin);
        this.game = game;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player player)
        {
            Map<Statistic, SpeedRunner> leaders = game.getStatsHandler().calculateLeaders();
            player.sendMessage("The current leaders are:");
            for(Statistic stat : leaders.keySet())
            {
                SpeedRunner leader = leaders.get(stat);
                if(leader != null)
                    player.sendMessage(stat.getName() + ": " + stat.getFormattedValue(leader.spigotPlayer) + " by " + leader.spigotPlayer.getDisplayName());
                else
                    player.sendMessage("Leader in stat " + stat.name() + " is somehow null. Something is off " );
            }
        }
        return false;
    }

}
