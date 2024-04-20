package dev.osowiz.speedrunstats.gametypes;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.listeners.PlayerJoinListener;
import dev.osowiz.speedrunstats.listeners.StandardAdvancementListener;
import dev.osowiz.speedrunstats.util.*;
import org.bson.Document;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class StandardSpeedrun extends Game {

    private final String category = "standard";

    public int leaderObjectiveID;
    public StandardSpeedrun(SpeedrunStats plugin, SpeedrunConfig config) {
        super.plugin = plugin;
        this.config = config;
        this.leaderObjectiveID = 0;
        this.listeners.add(new PlayerJoinListener(plugin, true));
        this.listeners.add(new StandardAdvancementListener(plugin, this));
        // register all listeners
        listeners.forEach(listener -> listener.register());
    }

    @Override
    public void startGame() {

        plugin.getServer().broadcastMessage("Game starting soon!");
        // create teams
        if(0 < config.standardTeamSize )
            Helpers.raffleTeamsBySize(runners, config.standardTeamSize); // todo change wording
        // set up the start of the game

        Random random = new Random();
        int randomCountdown =  random.nextInt(11) + 10;
        Timer timer = new Timer();
        TimerTask startupTask = new TimerTask() {
            @Override
            public void run() {
                plugin.getServer().broadcastMessage("Game has started!");
                startTimens = System.nanoTime();
                runners.forEach(runner -> {
                    runner.spigotPlayer.setAllowFlight(false);
                    runner.spigotPlayer.setFlying(false);
                    runner.spigotPlayer.setFlySpeed(Helpers.flySpeed);
                    runner.spigotPlayer.setWalkSpeed(Helpers.walkSpeed);}); // set walk speed back
                isOn = true;
            }
        };
        timer.schedule(startupTask, 1000 * randomCountdown);
    }

    @Override
    public void endGame()
    {

        plugin.getServer().broadcastMessage("Game has ended!");
        this.isOn = false;
        runners.forEach(runner -> runner.spigotPlayer.setAllowFlight(true)); // allow flight after the game

        ArrayList<Document> gameData = new ArrayList<Document>();
        ArrayList<Document> playerData = new ArrayList<Document>();
        for (SpeedRunner runner : this.runners) // one gamedoc per runner
        {
            Document gameDoc = new Document();
            gameDoc.append( "runnerid", runner.spigotPlayer.getUniqueId().toString() );
            gameDoc.append("runnername", runner.name);
            gameDoc.append("category", category);
            gameDoc.append("team", runner.teamID);
            gameDoc.append("time", runner.time);
            gameDoc.append("score", runner.stats.getPoints());
            gameDoc.append("kills", runner.stats.getKills());
            gameDoc.append("deaths", runner.stats.getDeaths());
            gameDoc.append("date", gameDate);
            gameData.add(gameDoc);

            playerData.add(runner.getUpdatedPlayerDocument());
        }

        plugin.uploadPlayerData(playerData);
        plugin.uploadGameData(gameData);
    }

    @Override
    public String getCategory() {
        return category;
    }

}
