package dev.osowiz.speedrunstats.util;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * This class represents a player who is participating in a speedrun.
 *
 */
public class SpeedRunner {

    // all kills and deaths of the player
    int allKills;
    int allDeaths;
    // gamemode dependent stats for fun
    int highestScore;
    double fastestTime;
    Scoreboard runnerBoard;

    public String name;
    public int rank;
    public int gamesPlayed;
    public int teamID = -1;
    public double time;
    public long lastCooldown;

    /**
     * Player object of this runner.
     */
    public Player spigotPlayer;

    /**
     * Stats of this run.
     */
    public GameStats stats;

    public SpeedRunner(Player player, int allKills, int allDeaths, int highestScore, int rank, double fastestTime)
    {
        this.spigotPlayer = player;
        name = player.getName();
        this.allKills = allKills;
        this.allDeaths = allDeaths;
        this.highestScore = highestScore;
        this.fastestTime = fastestTime;
        this.rank = rank;
        this.time = 1e6;
        this.lastCooldown = System.nanoTime();
        runnerBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective statsObjective = runnerBoard.registerNewObjective("statsboard", Criteria.DUMMY, "Your stats", RenderType.INTEGER);
        statsObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        this.stats = new GameStats(statsObjective);
        this.spigotPlayer.setScoreboard(runnerBoard);
    }

    /**
     * Called when the player has made an advancement in a single player game.
     * @param res advancementresult done
     */
    public void advancementDone(AdvancementResult res) {
        stats.addPoints(res.getPoints());
        if(-1 < res.getAdvancementLevel() && stats.currentObjectiveID < res.getAdvancementLevel())
        {
            for(int i = stats.currentObjectiveID; i < res.getAdvancementLevel(); i++)
            {
                stats.addPoints(StandardSpeedrunScoring.corePoints[i]);
            }
            stats.currentObjectiveID = res.getAdvancementLevel();
        }
    }

    /**
     * Sets the cooldown to the current time.
     */
    public void setCooldown()
    {
        lastCooldown = System.nanoTime();
    }



    public Document getUpdatedPlayerDocument()
    {
        Document doc = new Document();
        doc.append("uid", spigotPlayer.getUniqueId().toString());
        doc.append("name", name);
        doc.append("kills", allKills + stats.kills.getScore());
        doc.append("deaths", allDeaths + stats.deaths.getScore());
        doc.append("games", gamesPlayed + 1);

        return doc;
    }

}
