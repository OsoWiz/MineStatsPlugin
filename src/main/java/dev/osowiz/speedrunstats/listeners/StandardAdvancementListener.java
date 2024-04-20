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

    private StandardSpeedrun game;
    public StandardAdvancementListener(Plugin plugin, StandardSpeedrun speedrunGame) {
        super(plugin, "StandardAdvancementListener");
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
        // check if the advancement is not a core advancement
        if(res.getAdvancementLevel() < 0)
        { // non core
            runner.stats.addPoints(res.getPoints());
            runner.spigotPlayer.sendMessage("You have gained " + res.getPoints() + " points for " + event.getAdvancement().toString());
            return;
        }
        // core advancement
        if(runner.teamID < 0)
        { // single player
            runner.advancementDone(res);
        }

        // otherwise, this is a team advancement.
        SpeedrunTeam team = game.getTeamByID(runner.teamID);

        team.advancementDone(res);

        if(res.getAdvancementLevel() == StandardSpeedrunScoring.finalMission)
        {
            // end the game
            long endTimens = System.nanoTime();
            double elapsedTimeInSeconds = ((double) (endTimens - game.getStartTime())) / 1e9; // divide by billion to get seconds
            if(team != null) {
                team.getRunners().forEach(teamRunner -> {
                    teamRunner.time = elapsedTimeInSeconds;
                });
            } else {
                runner.time = elapsedTimeInSeconds;
            }
            game.endGame();
            this.unregister(); // no more advancements points
        }
    } // end of function

}
