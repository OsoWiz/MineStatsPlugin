package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.gametypes.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.AdvancementResult;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import dev.osowiz.speedrunstats.util.SpeedrunTeam;
import dev.osowiz.speedrunstats.util.StandardSpeedrunScoring;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.Plugin;

public class StandardAdvancementListener extends SpeedrunListenerBase {

    private final StandardSpeedrun game;
    public static final String name = "StandardAdvancementListener";
    public StandardAdvancementListener(Plugin plugin, StandardSpeedrun speedrunGame) {
        super(plugin, name);
        this.game = speedrunGame;
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        SpeedRunner runner = game.getRunnerByName(event.getPlayer().getName());
        if(runner == null) {
            return;
        }
        String advancementKey = event.getAdvancement().getKey().getKey();
        AdvancementResult res = StandardSpeedrunScoring.getScoreForAdvancement(advancementKey);
        SpeedrunTeam team = null;
        if(game.isTeamGame())
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
                plugin.getServer().broadcastMessage("Team " + team.teamID + " has finished the game in " + elapsedTimeInSeconds + " seconds!");
            } else {
                runner.time = elapsedTimeInSeconds;
                plugin.getServer().broadcastMessage(runner.name + " has finished the game in " + elapsedTimeInSeconds + " seconds!");
            }
            game.endGame();
            this.unregister(); // no more advancements points
        }
    } // end of function

}
