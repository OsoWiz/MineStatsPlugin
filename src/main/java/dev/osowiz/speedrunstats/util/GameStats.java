package dev.osowiz.speedrunstats.util;

public class GameStats {
    int points;
    int kills;
    int deaths;

    double currentTime;
    public int currentObjectiveID;
    // Todo add more stats
    GameStats() {
        currentObjectiveID = 0;
        this.points = 0;
        this.kills = 0;
        this.deaths = 0;
        this.currentTime = 0.0;
    }

    public String toString() {
        return "Points: " + this.points + ", Kills: " + kills + " Deaths: " + deaths;
    }

    public void addPoints(int points)
    {
        this.points += points;
    }

    public void setPoints(int points)
    {
        this.points = points;
    }

    public void addKill()
    {
        this.kills++;
    }

    public void addDeath()
    {
        this.deaths++;
    }

    public int getPoints()
    {
        return this.points;
    }

    public int getKills()
    {
        return this.kills;
    }

    public int getDeaths()
    {
        return this.deaths;
    }

}
