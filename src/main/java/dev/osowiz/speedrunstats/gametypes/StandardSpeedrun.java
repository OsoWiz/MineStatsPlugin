package dev.osowiz.speedrunstats.gametypes;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.listeners.*;
import dev.osowiz.speedrunstats.util.*;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.configuration.Configuration;

import java.util.*;

public class StandardSpeedrun extends Game {

    private final String category = "standard";
    private SpeedrunScoreBoardManager scoreBoardManager;
    private StatisticsHandler statsHandler;
    public StandardSpeedrun(SpeedrunStats plugin, SpeedrunConfig config) {
        super.plugin = plugin;
        this.config = config;
        statsHandler = new StatisticsHandler(this);
        statsHandler.addStatistic(Statistic.SNEAK_TIME);
        statsHandler.addStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
        statsHandler.addStatistic(Statistic.DEATHS);
        this.listeners.add(new WorldListener(plugin, this));
        this.listeners.add(new PlayerJoinListener(plugin, this, true));
        this.listeners.add(new StandardAdvancementListener(plugin, this));
        this.listeners.add(new StandardCatchupListener(plugin, this));
        this.listeners.add(new StandardKillDeathListener(plugin, this));
        this.listeners.add(new EndCrystalDestroyer(plugin));
        this.listeners.add(new PiglinbruteSpawnPreventer(plugin));
        this.listeners.add(new PiglinLootTableFixer(plugin, config.lootTable));
        this.listeners.add(new PlayerQuitListener(plugin));
        this.listeners.add(new BlockBreakPreventer(plugin));
        // register all listeners
        listeners.forEach(SpeedrunListenerBase::register);

        // prevent pvp in all worlds at start
        this.plugin.getServer().getWorlds().forEach(world -> {
            this.plugin.getLogger().info("PVP is off in world " + world.getName());
            world.setPVP(false);
        });
    }

    @Override
    public void startGame() {
        runners.forEach(runner -> runner.clearScoreBoard());
        plugin.getServer().broadcastMessage("Game starting soon!");

        // create teams
        if(0 < config.standardTeamSize ) {
            this.teams = teamBuilder.build(runners);
            plugin.getServer().broadcastMessage("Teams are as follows:");
            this.teams.forEach(team -> {
                this.plugin.getServer().broadcastMessage(team.getTeamAsString() + " is ready to roll!");
            });
            Helpers.tellPlayersTheirTeam(teams);
        }
        // set up the start of the game

        Random random = new Random();
        int randomCountdown =  random.nextInt(11) + 14;
        Timer timer = new Timer();
        TimerTask startupTask = new TimerTask() {
            @Override
            public void run() {
                plugin.getServer().broadcastMessage("Game has started!");
                startTimens = System.nanoTime();
                runners.forEach(runner -> {
                    runner.spigotPlayer.setAllowFlight(false);
                    runner.spigotPlayer.setFlying(false);
                    runner.spigotPlayer.setWalkSpeed(Helpers.walkSpeed);
                    runner.spigotPlayer.setFlySpeed(Helpers.flySpeed);
                }); // set walk speed back
                isOn = true;
                plugin.getServer().getWorlds().forEach(world -> {
                    world.setPVP(true);
                });
                unregisterListener(BlockBreakPreventer.name);
            }
        };
        timer.schedule(startupTask, 1000 * randomCountdown);
    }

    @Override
    public void endGame()
    {
        this.isOn = false;
        runners.forEach(runner -> {
            runner.spigotPlayer.setGameMode(GameMode.SPECTATOR);
        }); // allow flight and spectating after the game
        // if a team game, send results to players
        if(this.isTeamGame())
            Helpers.sendResultsToPlayers(teams);

        ArrayList<Document> gameData = new ArrayList<Document>();
        ArrayList<Document> playerData = new ArrayList<Document>();
        float averageRank = getAverageRank();
        for (SpeedRunner runner : this.runners) // one gamedoc per runner
        {
            Document gameDoc = new Document();
            gameDoc.append( "runnerid", runner.spigotPlayer.getUniqueId().toString() );
            gameDoc.append("runnername", runner.name);
            gameDoc.append("category", category);
            gameDoc.append("team", runner.teamID);
            gameDoc.append("time", runner.time);
            gameDoc.append("score", calculateFinalScore(runner, averageRank));
            gameDoc.append("kills", runner.stats.getKills());
            gameDoc.append("deaths", runner.stats.getDeaths());
            gameDoc.append("date", gameDate);
            gameData.add(gameDoc);

            playerData.add(runner.getUpdatedPlayerDocument());
        }

        plugin.uploadPlayerData(playerData);
        plugin.uploadGameData(gameData);

        Map<Statistic, SpeedRunner> statsMap = statsHandler.calculateLeaders();
        this.scoreBoardManager.createAndSetTriviaBoard(statsMap);
    }

    public void registerScoreBoardManager() {
        this.scoreBoardManager = new SpeedrunScoreBoardManager(plugin.getServer().getScoreboardManager());
    }

    private int calculateFinalScore(SpeedRunner runner, float averageRank)
    {
        int baseScore = runner.stats.getPoints();
        baseScore += StandardSpeedrunScoring.calculateDeathScore(runner.stats.getDeaths());
        if(isTeamGame() && -1 < runner.teamID )
        {
            SpeedrunTeam runnerTeam = getTeamByID(runner.teamID);
            baseScore += StandardSpeedrunScoring.getEyeOfEnderScore(runnerTeam);
            baseScore = (int) ((float) baseScore * StandardSpeedrunScoring.calculateEloBoost(averageRank, runnerTeam.getAverageRank()));
        } else {
            baseScore += StandardSpeedrunScoring.getEyeOfEnderScore(runner);
            baseScore = (int) ((float) baseScore * StandardSpeedrunScoring.calculateEloBoost(averageRank, runner.rank.getCode()) );
        }

        return baseScore;
    }

    @Override
    public void addRunner(SpeedRunner runner) {
        super.addRunner(runner);
        scoreBoardManager.updateDefaultScoreBoard();
        scoreBoardManager.getDefaultScoreBoard().setToRunner(runner);
    }

    @Override
    public String getCategory() {
        return category;
    }

}
