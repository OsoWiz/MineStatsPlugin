package dev.osowiz.speedrunstats.util;

import org.bukkit.Color;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a team in the game
 */
public class SpeedrunTeam {
    private ArrayList<SpeedRunner> runners = new ArrayList<SpeedRunner>();

    private int currentObjectiveID = 0;

    public Team scoreBoardTeam;

    public Color teamColor;

    public int rank;
    public int teamID;

    public SpeedrunTeam(List<SpeedRunner> runners) {
        for(SpeedRunner runner : runners)
        {
            this.runners.add(runner);
            runner.teamID = teamID;
        }
    }

    public int getTeamSize() {
        return runners.size();
    }

    public void addRunner(SpeedRunner runner) {
        runners.add(runner);
        runner.teamID = teamID;
    }

    public boolean advancementDone(AdvancementResult res)
    {
        if(currentObjectiveID <= res.getAdvancementLevel())
        {
            int score = res.getPoints();
            // get the previous scores if any
            for(int i = currentObjectiveID; i < res.getAdvancementLevel(); i++)
            {
                score += StandardSpeedrunScoring.corePoints[i];
            }
            currentObjectiveID = res.getAdvancementLevel();
            for(SpeedRunner runner : runners)
            {
                runner.stats.addPoints(score);
            }
            return true;
        }
        return false;
    }

    public List<SpeedRunner> getRunners() {
        return runners;
    }

}
