package dev.osowiz.speedrunstats.util;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;

public class SpeedrunScoreBoardManager {

    private ScoreboardManager manager = Bukkit.getScoreboardManager();
    private HashMap<String, Scoreboard> scoreBoards = new HashMap<>();
    private static final String pluginName = SpeedrunStats.class.getName();

    public SpeedrunScoreBoardManager() {
        Scoreboard defaultBoard =  manager.getNewScoreboard();
        scoreBoards.put("default", defaultBoard);
    }
    

}
