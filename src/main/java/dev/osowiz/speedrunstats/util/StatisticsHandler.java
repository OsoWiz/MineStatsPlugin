package dev.osowiz.speedrunstats.util;

import org.bukkit.Statistic;

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
        TreeMap<Statistic, SpeedRunner> map = new TreeMap();

        for(Statistic stat : statsToCalulate)
        {
            SpeedRunner leader = null;
            int max = 0;
            for(SpeedRunner runner : game.runners)
            {
                int value = 0;
                if(stat.isSubstatistic()) { // whether this statistic requires an additional argument
                    value = getTypedStatistic(stat, runner);
                } else {
                    value = runner.spigotPlayer.getStatistic(stat);
                }

                if(max < value)
                {
                    max = value;
                    leader = runner;
                }
            }
            map.put(stat, leader);
        }
        return map;
    }

    /**
     * Returns the value of a statistic that requires an additional argument.
     * @param stat to query value for
     * @param runner to query value for
     * @return
     */
    private int getTypedStatistic(Statistic stat, SpeedRunner runner)
    {
        switch(stat)
        {
            default:
                return 0;
        }
    }

    /**
     * Logs the statistics and their possible arguments.
     * @param logger
     */
    public void logStats(Logger logger)
    {
        for(Statistic stat : Statistic.values())
        {
            if(stat.isSubstatistic())
            {
                Statistic.Type type = stat.getType();
                logger.info("Statistic " + stat.toString() + " requires an argument of type " + type.toString());
            } else {
                logger.info("Statistic " + stat.toString() + " requires no argument.");
            }
        }
    }

}
