package dev.osowiz.speedrunstats.games;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.documents.GameDocument;
import dev.osowiz.speedrunstats.listeners.ConfigurationAbandonListener;
import dev.osowiz.speedrunstats.listeners.SpeedrunListenerBase;
import dev.osowiz.speedrunstats.listeners.WorldListener;
import dev.osowiz.speedrunstats.prompts.TeamJoinPrompt;
import dev.osowiz.speedrunstats.runnable.ScoreboardSwapTask;
import dev.osowiz.speedrunstats.util.*;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public abstract class Game {

    // members
    protected ArrayList<SpeedRunner> runners = new ArrayList<SpeedRunner>();
    protected SpeedrunStats plugin;
    protected Date gameDate = new Date();
    protected long startTimens; // start time in nanoseconds
    protected List<SpeedrunTeam> teams = new ArrayList<SpeedrunTeam>();
    // public to allow easy access to prompts. todo pass the builder only?
    public TeamBuilder teamBuilder = new TeamBuilder();
    // each gametype has their respective listeners
    protected ArrayList<SpeedrunListenerBase> listeners = new ArrayList<SpeedrunListenerBase>();

    protected SpeedrunConfig config;
    protected StatisticsHandler statsHandler;
    protected SpeedrunScoreBoardManager scoreBoardManager;
    Scoreboard defaultScoreBoard; // scoreboard to show when player joins the server

    public boolean isOn = false;
    int triviaBoardTaskID = -1;
    public Game(SpeedrunStats plugin, SpeedrunConfig config)
    {
        this.plugin = plugin;
        this.config = config;
        this.listeners.add(new WorldListener(plugin, this));
        this.statsHandler = new StatisticsHandler(this);
    }

    // public methods

    public SpeedRunner getRunnerByName(String nameToSearch) {
        for(SpeedRunner runner : runners) {
            if(runner.getName().equals(nameToSearch)){
                return runner;
            }
        }
        return null;
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public StatisticsHandler getStatsHandler() {
        return statsHandler;
    }

    /**
     * Schedules a task to be run after a delay.
     * @param task to run
     * @param delayInSeconds delay in seconds
     */
    public void scheduleTask(Runnable task, float delayInSeconds)
    {
        plugin.scheduleTask(task, delayInSeconds);
    }

    public Date getGameDate()
    {
        return gameDate;
    }

    public void registerScoreBoardManager()
    {
        plugin.getLogger().info("Registering scoreboardmanager");
        this.scoreBoardManager = new SpeedrunScoreBoardManager(plugin.getServer().getScoreboardManager());
        this.triviaBoardTaskID = generateAndRunTriviaBoards();
    }

    protected int generateAndRunTriviaBoards()
    {
        plugin.getLogger().info("Generating trivia boards");
        // trivia boards are registered here currently. Perhaps this should be moved or configured later.
        List<GameDocument> top5FastestRuns = plugin.getDatabase().getFastestNGames(5);
        String fastestTitle = "Top 5 fastest runs:";
        String highestScoreTitle = "Top 5 highest scores:";
        String mostDeathsTitle = "Top 5 most deaths combined:";
        String mostKillsTitle = "Top 5 killers:";
        String highestRankTitle = "Top 5 highest ranks:";
        List<String> fastestTriviaLines = top5FastestRuns.stream().map(gameDocument -> ChatColor.BOLD + Helpers.timeToString(gameDocument.getCompletionTime()) + ChatColor.RESET +
                " by " + gameDocument.getWinnerNames() + " on " + gameDocument.getDate()).toList();
        List<String> highestScores =  plugin.getDatabase().getTopNPlayersByScore(5).stream().map(player ->
                ChatColor.BOLD + "" + Rank.fromCode(player.getRankCode()).getColor() + player.getName() + ChatColor.RESET
                        + " with " + player.getHighestScore()).toList();
        List<String> mostDeaths =  plugin.getDatabase().getTopNPlayersByDeaths(5).stream().map(player ->
                ChatColor.BOLD + "" + Rank.fromCode(player.getRankCode()).getColor() + player.getName() + ChatColor.RESET
                        + " with " + player.getAllDeaths()).toList();
        List<String> mostKills =  plugin.getDatabase().getTopNPlayersByKills(5).stream().map(player ->
                ChatColor.BOLD + "" + Rank.fromCode(player.getRankCode()).getColor() + player.getName() + ChatColor.RESET
                        + " with " + player.getAllKills()).toList();
        List<String> highestRanks =  plugin.getDatabase().getTopNRankedPlayers(5).stream().map(player ->
                ChatColor.BOLD + "" + Rank.fromCode(player.getRankCode()).getColor() + player.getName() + ChatColor.RESET
                        + " with " + Rank.fromCode(player.getRankCode()).getName()).toList();

        this.scoreBoardManager.createScoreBoardWithContent("top5fastest", fastestTitle, fastestTriviaLines);
        this.scoreBoardManager.createScoreBoardWithContent("top5highest", highestScoreTitle, highestScores);
        this.scoreBoardManager.createScoreBoardWithContent("top5deaths", mostDeathsTitle, mostDeaths);
        this.scoreBoardManager.createScoreBoardWithContent("top5kills", mostKillsTitle, mostKills);
        this.scoreBoardManager.createScoreBoardWithContent("top5ranks", highestRankTitle, highestRanks);
        List<String> triviaBoards = new ArrayList<>();
        triviaBoards.add("default");
        triviaBoards.add("top5fastest");
        triviaBoards.add("top5highest");
        triviaBoards.add("top5deaths");
        triviaBoards.add("top5kills");
        triviaBoards.add("top5ranks");
        return this.plugin.scheduleRecurrentTask(new ScoreboardSwapTask(triviaBoards, scoreBoardManager), 5, 10);
    }

    /**
     * Cancels the trivia board task and clears all scoreboards.
     */
    protected void cancelTriviaBoards()
    {
        if(triviaBoardTaskID != -1)
        {
            plugin.cancelTask(triviaBoardTaskID);
            runners.forEach(SpeedRunner::clearScoreBoard);
        }
    }

    public SpeedRunner getRunnerByID(UUID uid) {
        for(SpeedRunner runner : runners) {
            if(runner.uid.equals(uid)){
                return runner;
            }
        }
        return null;
    }

    public List<SpeedRunner> getRunners() {
        return runners;
    }

    public boolean containsRunner(UUID uid){
        for(SpeedRunner runner : runners){
            if(runner.spigotPlayer.getUniqueId().equals(uid)){
                return true;
            }
        }
        return false;
    }

    public boolean containsRunner(String name){
        for(SpeedRunner runner : runners){
            if(runner.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public boolean containsPlayer(Player player){
        for(SpeedRunner runner : runners){
            if(runner.spigotPlayer.getUniqueId().equals(player.getUniqueId())){
                return true;
            }
        }
        return false;
    }

    public void initScoreboard(){
        defaultScoreBoard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        Objective newObj = defaultScoreBoard.registerNewObjective("speedrun", "dummy", "Speedrun", RenderType.INTEGER);
        newObj.setDisplaySlot(DisplaySlot.SIDEBAR);
        newObj.setDisplayName( "Welcome to  " + gameDate.toString());
        Score numPlayers = newObj.getScore("Players: ");
        numPlayers.setScore(runners.size());
    }

    /**
     * Unregisters a listener from this game.
     * @param listenerName to unregister
     */
    public void unregisterListener(String listenerName)
    {
        this.listeners.stream().filter(listener -> listener.name.equals(listenerName)).forEach(SpeedrunListenerBase::unregister);
    }

    public SpeedrunConfig getConfig(){
        return config;
    }

    /**
     * Sets the configuration for the game.
     * @param config configuration for the game.
     */
    public void setConfig(SpeedrunConfig config){
        this.config = config;
    }

    /**
     * Adds a player to the game.
     */
    public void addRunner(SpeedRunner runner) {
        runners.add(runner);
    }

    public SpeedrunTeam getTeamByID(int teamID){
        for(SpeedrunTeam team : teams){
            if(team.teamID == teamID){
                return team;
            }
        }
        return null;
    }

    public boolean isTeamGame(){
        return !teams.isEmpty();
    }

    public int getTeamCount(){
        return teams.size();
    }

    public double getCooldownTime(){
        return config.catchupCooldown;
    }

    public float getAverageRank(){
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

    public int getHighScore(){
        int highScore = 0;
        for(SpeedRunner runner : runners) {
            if(runner.stats.getPoints() > highScore){
                highScore = runner.stats.getPoints();
            }
        }
        return highScore;
    }

    /**
     * Returns the start time of the game in nanoseconds.
     * @return start time in nanoseconds
     */
    public long getStartTime(){ return startTimens;}

    // public abstract methods

    /**
     * Starts the game.
     * Implementation depends on the type of game.
     */
    public abstract void startGame();


    /**
     * Ends the game.
     */
    public abstract void endGame();

    public abstract int calculateFinalScore(SpeedRunner runner, float averageRank);

    /**
     * Returns the category of the game.
     * @return category of the game
     */
    public abstract String getCategory();

    public void askPlayerChoices() {
        ConversationFactory factory = new ConversationFactory(plugin)
                .withFirstPrompt(new TeamJoinPrompt(this, teamBuilder.getTeamCount(runners.size())))
                .withEscapeSequence("cancel")
                .withTimeout(60)
                .thatExcludesNonPlayersWithMessage("You must be a player to join a team")
                .addConversationAbandonedListener(new ConfigurationAbandonListener());
        for(SpeedRunner runner : runners) {
           factory.buildConversation(runner.spigotPlayer).begin();
        }
    }

    /**
     * Broadcasts a message to all players.
     * @param message
     */
    public void broadcastMessage(String message){
        plugin.getServer().broadcastMessage(message);
    }


    // private methods

}
