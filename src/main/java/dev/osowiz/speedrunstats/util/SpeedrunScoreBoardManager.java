package dev.osowiz.speedrunstats.util;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SpeedrunScoreBoardManager manages the scoreboards for the plugin.
 * CREATE ONLY AFTER THE FIRST WORLD LOAD
 */
public class SpeedrunScoreBoardManager {

    private ScoreboardManager manager;
    private HashMap<String, SpeedrunScoreBoard> scoreBoards = new HashMap<>();
    private static final String pluginName = SpeedrunStats.class.getName();

    public SpeedrunScoreBoardManager(ScoreboardManager manager) {
        this.manager = manager;
        SpeedrunScoreBoard defaultBoard = new SpeedrunScoreBoard(manager.getNewScoreboard(), "Default");
        defaultBoard.setLine(ChatColor.BOLD + "Welcome to the server!", 15);
        defaultBoard.setLine("The game will begin shortly.", 14);
        defaultBoard.registerScore("default", 10, "Players: ", 0);
        scoreBoards.put("default", defaultBoard);
    }

    public void addScoreBoard(String name, SpeedrunScoreBoard board) {
        scoreBoards.put(name, board);
    }

    public SpeedrunScoreBoard getScoreBoard(String name) {
        return scoreBoards.get(name);
    }

    public SpeedrunScoreBoard getDefaultScoreBoard() {
        return scoreBoards.get("default");
    }

    public void updateDefaultScoreBoard() {
        SpeedrunScoreBoard defaultBoard = scoreBoards.get("default");
        defaultBoard.setScore("default", Bukkit.getOnlinePlayers().size());
    }

    public static SpeedrunScoreBoard getResultsBoard(List<SpeedrunTeam> teams)
    {
        teams.sort((a, b) -> a.getCurrentObjectiveID() - b.getCurrentObjectiveID());
        SpeedrunScoreBoard resBoard = new SpeedrunScoreBoard(Bukkit.getScoreboardManager().getNewScoreboard(), ChatColor.BOLD + "Results:");
        resBoard.setLines(teams.stream().map(team -> team.toString()).toArray(String[]::new));
        return resBoard;
    }

    public static void setResultsBoard(List<SpeedrunTeam> teams)
    {
        SpeedrunScoreBoard resBoard = getResultsBoard(teams);
        Bukkit.getOnlinePlayers().forEach(player -> resBoard.setToPlayer(player));
    }

    public static void createAndSetTriviaBoard(Map<Statistic, SpeedRunner> mapping)
    {
        int line = 15;
        SpeedrunScoreBoard board = new SpeedrunScoreBoard(Bukkit.getScoreboardManager().getNewScoreboard(), ChatColor.BOLD + "Trivia:");

        for(Map.Entry<Statistic, SpeedRunner> entry : mapping.entrySet())
        {
            SpeedRunner runner = entry.getValue();
            board.setLine("Most " + entry.getKey().toString() + ": " +
                    runner.spigotPlayer.getStatistic(entry.getKey()) + " by " +
                    ChatColor.BOLD + runner.name, line);
            line--;
        }

        Bukkit.getOnlinePlayers().forEach(player -> board.setToPlayer(player));
    }

}
