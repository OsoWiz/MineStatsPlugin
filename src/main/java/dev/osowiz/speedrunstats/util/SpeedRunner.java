package dev.osowiz.speedrunstats.util;

import dev.osowiz.speedrunstats.SpeedrunDB;
import dev.osowiz.speedrunstats.documents.PlayerDocument;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

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

    private String name;
    public final UUID uid;
    public Rank rank;
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

    public SpeedRunner(Player player, int allKills, int allDeaths, int highestScore, Rank rank, double fastestTime)
    {
        this.spigotPlayer = player;
        name = player.getName();
        this.allKills = allKills;
        this.allDeaths = allDeaths;
        this.highestScore = highestScore;
        this.fastestTime = fastestTime;
        this.rank = rank;
        this.time = Double.POSITIVE_INFINITY;
        this.lastCooldown = System.nanoTime();
        this.uid = player.getUniqueId();
        this.stats = new GameStats();
    }

    public String getName() {
        return name;
    }

    /**
     * Called when the player has made an advancement in a single player game.
     * @param res advancementresult done
     */
    public boolean advancementDone(AdvancementResult res) {
        stats.addPoints(res.getPoints());
        if(-1 < res.getAdvancementLevel() && stats.currentObjectiveID <= res.getAdvancementLevel())
        {
            for(int i = stats.currentObjectiveID; i <= res.getAdvancementLevel(); i++)
            {
                stats.addPoints(StandardSpeedrunScoring.corePoints[i]);
            }
            stats.currentObjectiveID = res.getAdvancementLevel() + 1;
            return true;
        }
        return false;
    }

    /**
     * Sets the cooldown to the current time.
     */
    public void setCooldown()
    {
        lastCooldown = System.nanoTime();
    }

    /**
     * Returns the updated player document for mongodb.
     * @return
     */
    public Document getUpdatedPlayerDocument()
    {
        Document doc = new Document();
        doc.append("uid", spigotPlayer.getUniqueId().toString());
        doc.append("name", name);
        doc.append("kills", allKills + stats.kills);
        doc.append("deaths", allDeaths + stats.deaths);
        doc.append("games", gamesPlayed + 1);

        return doc;
    }

    public void clearScoreBoard()
    {
        this.spigotPlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public int getDeathsThisGame()
    {
        return stats.getDeaths();
    }

    public int getKillsThisGame()
    {
        return stats.getKills();
    }

}
