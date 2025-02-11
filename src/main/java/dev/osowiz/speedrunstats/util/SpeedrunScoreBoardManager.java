package dev.osowiz.speedrunstats.util;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import dev.osowiz.speedrunstats.enums.Statistic;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SpeedrunScoreBoardManager manages the scoreboards for the plugin.
 * IT IS ASSUMED THAT THIS CLASS IS ONLY CREATED AFTER THE FIRST WORLD LOAD
 */
public class SpeedrunScoreBoardManager {

    private ScoreboardManager manager;
    private HashMap<String, SpeedrunScoreBoard> scoreBoards = new HashMap<>();

    public SpeedrunScoreBoardManager(ScoreboardManager manager) {
        this.manager = manager;
        SpeedrunScoreBoard defaultBoard = new SpeedrunScoreBoard(manager.getNewScoreboard(), "");
        defaultBoard.setLine(ChatColor.BOLD + "Welcome to the server!", 15);
        defaultBoard.setLine("The game will begin shortly.", 14);
        defaultBoard.registerScore("default", 10, "Players: ", 0);
        scoreBoards.put("default", defaultBoard);
    }

    public void addScoreBoard(String name, SpeedrunScoreBoard board) {
        scoreBoards.put(name, board);
    }

    public void createScoreBoardWithContent(String key, String name, List<String> contentRows)
    {
        SpeedrunScoreBoard newScoreBoard = new SpeedrunScoreBoard(manager.getNewScoreboard(), name);
        newScoreBoard.setLines(contentRows);
        scoreBoards.put(key, newScoreBoard);
    }

    /**
     * Gets a board by name.
     * @param name
     * @return the board if it exists, null otherwise.
     */
    public SpeedrunScoreBoard getScoreBoard(String name) {
        return scoreBoards.get(name);
    }

    /**
     * Creates a new empty board, and stores it in the manager for the given name.
     * @param name
     * @return
     */
    public SpeedrunScoreBoard createScoreBoard(String name) {
        SpeedrunScoreBoard newBoard = new SpeedrunScoreBoard(manager.getNewScoreboard(), name);
        scoreBoards.put(name, newBoard);
        return newBoard;
    }

    public SpeedrunScoreBoard getDefaultScoreBoard() {
        return scoreBoards.get("default");
    }

    public void updateDefaultScoreBoard() {
        SpeedrunScoreBoard defaultBoard = scoreBoards.get("default");
        defaultBoard.setScore("default", Bukkit.getOnlinePlayers().size());
    }

    public boolean updateBoard(String name, SpeedrunScoreBoard update)
    {
        if(scoreBoards.containsKey(name))
        {
            scoreBoards.put(name, update);
            return true;
        }
        return false;
    }

    /**
     * Sets a board of the given name to all players if it exists.
     * @param name of the board.
     * @return boolean indicating success.
     */
    public boolean setBoardToAllPlayers(String name)
    {
        if(scoreBoards.containsKey(name)) {
            SpeedrunScoreBoard board = scoreBoards.get(name);
            Bukkit.getOnlinePlayers().forEach(board::setToPlayer);
        }
        return false;
    }

    public static SpeedrunScoreBoard getResultsBoard(List<SpeedrunTeam> teams)
    {
        teams.sort(Comparator.comparingInt(SpeedrunTeam::getCurrentObjectiveID));
        SpeedrunScoreBoard resBoard = new SpeedrunScoreBoard(Bukkit.getScoreboardManager().getNewScoreboard(), ChatColor.BOLD + "Results:");
        resBoard.setLines(teams.stream().map(team -> team.toString()).toArray(String[]::new));
        return resBoard;
    }

    public static void setResultsBoard(List<SpeedrunTeam> teams)
    {
        SpeedrunScoreBoard resBoard = getResultsBoard(teams);
        Bukkit.getOnlinePlayers().forEach(resBoard::setToPlayer);
    }

    public static void createAndSetTriviaBoard(Map<Statistic, SpeedRunner> mapping)
    {
        int line = 15;
        SpeedrunScoreBoard board = new SpeedrunScoreBoard(Bukkit.getScoreboardManager().getNewScoreboard(), ChatColor.BOLD + "Trivia:");

        for(Map.Entry<Statistic, SpeedRunner> entry : mapping.entrySet())
        {
            SpeedRunner runner = entry.getValue();
            board.setLine("Most " + entry.getKey().toString() + ": " +
                    entry.getKey().getFormattedValue(runner.spigotPlayer) + " by " +
                    ChatColor.BOLD + runner.getName(), line);
            line--;
        }

        Bukkit.getOnlinePlayers().forEach(board::setToPlayer);
    }

}
