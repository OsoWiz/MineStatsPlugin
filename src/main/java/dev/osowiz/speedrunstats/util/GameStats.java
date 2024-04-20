package dev.osowiz.speedrunstats.util;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class GameStats {
    Score points;
    Score kills;
    Score deaths;

    Score currentTime;
    Objective gameStatsObjective;
    public int currentObjectiveID;
    // Todo add more stats
    GameStats(Objective scoreboardObjective) {
        this.gameStatsObjective = scoreboardObjective;
        currentObjectiveID = 0;
        this.points = scoreboardObjective.getScore("Points");
        this.kills = scoreboardObjective.getScore("Kills");
        this.deaths = scoreboardObjective.getScore("Deaths");
        this.currentTime = scoreboardObjective.getScore("Time");

        points.setScore(0);
        kills.setScore(0);
        deaths.setScore(0);
        currentTime.setScore(0);
    }

    public String toString() {
        return "Points: " + this.points + ", Kills: " + kills.getScore() + " Deaths: " + deaths.getScore();
    }

    public void addPoints(int points)
    {
        this.points.setScore(this.points.getScore() + points);
    }

    public void setPoints(int points)
    {
        this.points.setScore(points);
    }

    public void addKill()
    {
        this.kills.setScore(this.kills.getScore() + 1);
    }

    public void addDeath()
    {
        this.deaths.setScore(this.deaths.getScore() + 1);
    }

    public int getPoints()
    {
        return this.points.getScore();
    }

    public int getKills()
    {
        return this.kills.getScore();
    }

    public int getDeaths()
    {
        return this.deaths.getScore();
    }

}
