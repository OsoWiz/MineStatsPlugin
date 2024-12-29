package dev.osowiz.speedrunstats.util;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class SpeedrunScoreBoard {

    private Scoreboard board;
    private Objective objective;

    public SpeedrunScoreBoard(Scoreboard board, String displayName) {
        this.board = board;
        objective = board.registerNewObjective(SpeedrunStats.class.getName(), Criteria.DUMMY, displayName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void registerScore(String scoreKey, int slot, String prefix, int initialScore) {
        Team scoreTeam = board.registerNewTeam(scoreKey);
        scoreTeam.addEntry(getSlotKey(slot));
        scoreTeam.setPrefix(prefix);
        scoreTeam.setSuffix(initialScore + "");
        objective.getScore(scoreKey).setScore(slot); // we also need to set, otherwise this won't show up.
    }

    public String getSlotKey(int slot) {
        return ChatColor.values()[slot].toString();
    }

    /**
     * Sets a score based on the key of the score. (Needs to be a registered score key)
     * @param scoreKey to update.
     * @param score to set.
     */
    public void setScore(String scoreKey, int score)
    {
        // Teams can be used to set lines with prefix and a suffix.
        Team scoreTeam = board.getTeam(scoreKey);
        if(scoreTeam != null)
        {
            scoreTeam.setSuffix(score + "");
        }
    }

    /**
     * Increments the score by the given amount.
     * @param scoreKey
     * @param increment
     */
    public void incrementScore(String scoreKey, int increment)
    {
        Team scoreTeam = board.getTeam(scoreKey);
        if(scoreTeam != null)
        {
            try
            {
                int currentScore = Integer.parseInt(scoreTeam.getSuffix());
                scoreTeam.setSuffix(currentScore + increment + "");
            } catch (NumberFormatException e) {
                System.out.println("Score was not a number.");
            }
        }
    }

    public void increment(String scoreKey) {
        incrementScore(scoreKey, 1);
    }
    public void decrement(String scoreKey) {
        incrementScore(scoreKey, -1);
    }

    /**
     * Sets the given text to the given line in the scoreboard. Does nothing if line is not between 0 and 15.
     * @param text to set.
     * @param line to insert to.
     */
    public void setLine(String text, int line) {
        if(-1 < line && line < 16)
            objective.getScore(text).setScore(line);
    }


    /**
     * Sets multiple lines of text in the scoreboard from the top according to the order they are passed in.
     * @param lines to set to the scoreboard.
     */
    public void setLines(String ... lines) {
        int slot = 15; // start from the top
        for(String line : lines) {
            if(slot < 0)
                break;
            objective.getScore(line).setScore(slot--);
        }
    }

    /**
     * Sets this board to the given player.
     * @param playerToSet
     */
    public void setToPlayer(Player playerToSet) {
        playerToSet.setScoreboard(board);
    }

    public void setToRunner(SpeedRunner runner) {
        runner.spigotPlayer.setScoreboard(board);
    }

    /**
     * Returns the actual scoreboard object.
     * @return
     */
    public Scoreboard getBoard() {
        return board;
    }
}
