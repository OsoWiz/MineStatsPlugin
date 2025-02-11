package dev.osowiz.speedrunstats.games;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.documents.GameDocument;
import dev.osowiz.speedrunstats.documents.PlayerDocument;
import dev.osowiz.speedrunstats.documents.RunDocument;
import dev.osowiz.speedrunstats.listeners.*;
import dev.osowiz.speedrunstats.util.*;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import dev.osowiz.speedrunstats.enums.Statistic;

import java.util.*;

public class StandardSpeedrun extends Game {

    private final String category = "standard";

    public StandardSpeedrun(SpeedrunStats plugin, SpeedrunConfig config) {
        super(plugin, config);
        this.statsHandler.addStatistic(Statistic.TARGET_HIT);
        this.statsHandler.addStatistic(Statistic.CHESTS_OPENED);
        this.statsHandler.addStatistic(Statistic.ANIMALS_BRED);
        this.statsHandler.addStatistic(Statistic.FISH_CAUGHT);
        this.statsHandler.addStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
        this.statsHandler.addStatistic(Statistic.DAMAGE_DEALT);
        this.statsHandler.addStatistic(Statistic.DAMAGE_TAKEN);
        this.statsHandler.addStatistic(Statistic.SNEAK_TIME);
        this.listeners.add(new PlayerJoinListener(plugin, this));
        this.listeners.add(new MovementPreventer(plugin));
        this.listeners.add(new StandardAdvancementListener(plugin, this));
        this.listeners.add(new StandardCatchupListener(plugin, this));
        this.listeners.add(new StandardKillDeathListener(plugin, this));
        this.listeners.add(new EndCrystalDestroyer(plugin));
        this.listeners.add(new PiglinbruteSpawnPreventer(plugin));
        this.listeners.add(new PiglinLootTableFixer(plugin, config.lootTable));
        this.listeners.add(new PlayerQuitListener(plugin));
        this.listeners.add(new BlockBreakPreventer(plugin));
        // this.listeners.add(new SpawnerFortifier(plugin));
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
        cancelTriviaBoards();
        plugin.getServer().broadcastMessage("Game starting in 10 to 20 seconds!");

        // create teams
        if(0 < config.standardTeamSize ) {
            this.teams = teamBuilder.build(runners);
            plugin.getServer().broadcastMessage("Teams are as follows:");
            this.teams.forEach(team -> {
                this.plugin.getServer().broadcastMessage(team.getTeamAsString());
            });
            Helpers.tellPlayersTheirTeam(teams);
        }
        // set up the start of the game

        Random random = new Random();
        int randomCountdown =  random.nextInt(11) + 10;
        Timer timer = new Timer();
        TimerTask startupTask = new TimerTask() {
            @Override
            public void run() {
                plugin.getServer().broadcastMessage("Game has started!");
                startTimens = System.nanoTime();
                isOn = true;
                plugin.getServer().getWorlds().forEach(world -> {
                    world.setPVP(true);
                });
                runners.forEach(runner -> {
                    runner.spigotPlayer.playSound(runner.spigotPlayer.getLocation(), Soundbank.getRandomGoatHorn(), 3.f, 1.f);
                });
                unregisterListener(BlockBreakPreventer.name);
                unregisterListener(MovementPreventer.name);
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

        plugin.uploadFinishedGameData();
        Map<Statistic, SpeedRunner> statsMap = statsHandler.calculateLeaders();
        SpeedrunScoreBoardManager.createAndSetTriviaBoard(statsMap);
    }

    @Override
    public int calculateFinalScore(SpeedRunner runner, float averageRank)
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

        if(runner.time < Double.POSITIVE_INFINITY)
        {
            double oneHour = 3600.d;
            double ratio = oneHour / runner.time;
            baseScore = (int) (baseScore * Math.max(1.d, ratio));
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
