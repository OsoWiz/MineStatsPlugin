package dev.osowiz.speedrunstats.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a team in the game
 */
public class SpeedrunTeam {

    public static final int NONE = -1;

    private ArrayList<SpeedRunner> runners = new ArrayList<SpeedRunner>();

    private int currentObjectiveID = 0;

    public Team scoreBoardTeam;

    public ChatColor teamColor;

    public int teamID;

    public SpeedrunTeam()
    {
        this.runners = new ArrayList<SpeedRunner>();
        this.teamID = -1;
    }

    public SpeedrunTeam(List<SpeedRunner> runners, int teamID) {
        for(SpeedRunner runner : runners)
        {
            this.runners.add(runner);
            runner.teamID = teamID;
        }
        this.teamID = teamID;
    }

    public int getTeamSize() {
        return runners.size();
    }

    public void addRunner(SpeedRunner runner) {
        runners.add(runner);
        runner.teamID = teamID;
    }

    /**
     * Adds an advancementresult to the team.
     * @param res
     * @return true if new core advancement, false otherwise.
     */
    public boolean advancementDone(AdvancementResult res)
    {
        boolean isNew = currentObjectiveID <= res.getAdvancementLevel();
        if(isNew)
        {
            int score = res.getPoints();
            // get the previous scores if any
            for(int i = currentObjectiveID; i < res.getAdvancementLevel(); i++)
            {
                score += StandardSpeedrunScoring.corePoints[i];
            }
            currentObjectiveID = res.getAdvancementLevel() + 1;
            for(SpeedRunner runner : runners)
            {
                runner.stats.addPoints(score);
                runner.stats.currentObjectiveID = currentObjectiveID;
            }
        }

        return isNew;
    }

    public List<SpeedRunner> getRunners() {
        return runners;
    }

    public void setRunners(List<SpeedRunner> runners) {
        this.runners = new ArrayList<>(runners);
        for (SpeedRunner runner : runners) {
            runner.teamID = teamID;
        }
    }

    /**
     * Returns the team name and the players in the team as a string.
     * @return
     */
    public String getTeamAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.teamColor +  "Team " + teamColor.name()+ ": ");
        sb.append(getPlayersAsString());
        return sb.toString();
    }

    public int size() {
        return runners.size();
    }

    /**
     * Returns the team as a string with each player on a new line.
     * @return team as string
     */
    public String getTeamAsStringNewLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.teamColor +  "Team " + teamColor.name()+ ": \n");
        sb.append(getPlayersAsString("\n"));
        return sb.toString();
    }

    public String toString() {
        return this.teamColor + "Team " + this.teamColor.name() + ChatColor.RESET;
    }

    public String getPlayersAsString()
    {
        return getPlayersAsString(", ");
    }

    public String getPlayersAsString(String delimiter)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.teamColor);
        for(SpeedRunner runner : runners) {
            sb.append(runner.getName());
            sb.append(delimiter);
        }
        sb.delete(sb.length() - delimiter.length(), sb.length()); // delete last delimiter
        return sb.toString();
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
        runners.forEach(runner -> runner.teamID = teamID);
    }

    public int getCurrentObjectiveID() {
        return currentObjectiveID;
    }

    public float getAverageRank() {
        if(runners.isEmpty())
        {
            return 0.f;
        }

        float sum = 0;
        for(SpeedRunner runner : runners) {
            sum += runner.rank.getCode();
        }
        return sum / runners.size();
    }

}
