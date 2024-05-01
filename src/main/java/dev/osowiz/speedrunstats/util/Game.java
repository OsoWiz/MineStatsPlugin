package dev.osowiz.speedrunstats.util;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.listeners.SpeedrunListenerBase;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Date;

public abstract class Game {

    // members
    protected ArrayList<SpeedRunner> runners = new ArrayList<SpeedRunner>();
    protected SpeedrunStats plugin;
    protected Date gameDate = new Date();
    protected long startTimens; // start time in nanoseconds
    protected ArrayList<SpeedrunTeam> teams = new ArrayList<SpeedrunTeam>();
    // each gametype has their respective listeners
    protected ArrayList<SpeedrunListenerBase> listeners = new ArrayList<SpeedrunListenerBase>();

    protected SpeedrunConfig config;
    Scoreboard defaultScoreBoard; // scoreboard to show when player joins the server

    public boolean isOn = false;

    // public methods

    public SpeedRunner getRunnerByName(String nameToSearch) {
        for(SpeedRunner runner : runners) {
            if(runner.name.equals(nameToSearch)){
                return runner;
            }
        }
        return null;
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

    public double getCooldownTime(){
        return config.catchupCooldown;
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

    /**
     * Returns the category of the game.
     * @return category of the game
     */
    public abstract String getCategory();


    // private methods

}
