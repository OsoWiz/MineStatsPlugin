package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.games.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.*;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.Plugin;

import static dev.osowiz.speedrunstats.util.StandardSpeedrunScoring.noMission;

public class StandardAdvancementListener extends SpeedrunListenerBase {

    private final StandardSpeedrun game;
    public static final String name = "StandardAdvancementListener";
    public StandardAdvancementListener(SpeedrunStats plugin, StandardSpeedrun speedrunGame) {
        super(plugin, name);
        this.game = speedrunGame;
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        SpeedRunner runner = game.getRunnerByID(event.getPlayer().getUniqueId());
        if(runner == null) {
            return;
        }
        String advancementKey = event.getAdvancement().getKey().getKey();
        AdvancementResult res = StandardSpeedrunScoring.getScoreForAdvancement(advancementKey);
        SpeedrunTeam team = null;
        if(game.isTeamGame() && res.getAdvancementLevel() > noMission)
        {   // team advancement
            team = game.getTeamByID(runner.teamID);
            team.advancementDone(res);
        }
        else {
            runner.advancementDone(res);
        }

        if(res.getAdvancementLevel() == StandardSpeedrunScoring.finalMission)
        {
            // end the game
            long endTimens = System.nanoTime();
            double elapsedTimeInSeconds = ((double) (endTimens - game.getStartTime())) / 1e9; // divide by billion to get seconds
            if(team != null) {
                team.getRunners().forEach(teamRunner -> {
                    teamRunner.time = elapsedTimeInSeconds;
                });
                plugin.getServer().broadcastMessage(team.teamColor + "Team " + team.teamID + " " + ChatColor.RESET + " has finished the game in " + Helpers.timeToString(elapsedTimeInSeconds) + "!");
            } else {
                runner.time = elapsedTimeInSeconds;
                plugin.getServer().broadcastMessage(runner.getName() + " has finished the game in " + Helpers.timeToString(elapsedTimeInSeconds) + "!");
            }
            game.endGame();
            this.unregister(); // no more advancements points
        }
    } // end of function

}
