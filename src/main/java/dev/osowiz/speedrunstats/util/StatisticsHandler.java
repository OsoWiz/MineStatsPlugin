package dev.osowiz.speedrunstats.util;

import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.enums.Statistic;

import java.util.*;
import java.util.logging.Logger;

public class StatisticsHandler {

    private Game game;

    private TreeSet<Statistic> statsToCalulate = new TreeSet<Statistic>();
    public StatisticsHandler(Game game) {
        this.game = game;
    }

    /**
     * Adds a statistic to the list of statistics to calculate, unless already added.
     * @param stat
     * @return
     */
    public boolean addStatistic(Statistic stat)
    {
        return statsToCalulate.add(stat);
    }

    public Map<Statistic, SpeedRunner> calculateLeaders()
    {
        TreeMap<Statistic, SpeedRunner> map = new TreeMap<>();
        if(game.getRunners().isEmpty())
            {
            game.getLogger().info("No runners to calculate leaders for.");
            return map;
        }
        for(Statistic stat : statsToCalulate)
        {
            SpeedRunner leader = game.getRunners().getFirst();
            int max = stat.getValueForPlayer(leader.spigotPlayer);
            for(SpeedRunner runner : game.getRunners().subList(1, game.getRunners().size()))
            {
                int value = stat.getValueForPlayer(runner.spigotPlayer);
                if(max < value) // this does not handle ties very well. TODO FIX
                {
                    max = value;
                    leader = runner;
                }
            }
            map.put(stat, leader);
        }
        return map;
    }

}
