package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.gametypes.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.Game;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StandardKillDeathListener extends SpeedrunListenerBase {

    StandardSpeedrun game;
    StandardKillDeathListener(SpeedrunStats plugin, StandardSpeedrun game) {
        super(plugin, "StandardKillDeathListener");
        this.game = game;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        SpeedRunner runner = game.getRunnerByName(player.getName());
        if(runner == null)
        {
            plugin.getLogger().warning("Runner " + player.getName() + " not found in game.");
            return;
        }
        runner.stats.addDeath();

        Entity killer = player.getKiller();
        if( !(killer instanceof Player) ) { // if killer is a mob
            return;
        }

        Player playerKiller = (Player) killer;
        SpeedRunner killerRunner = game.getRunnerByName(playerKiller.getName());
        if(killerRunner == null)
        {
            plugin.getLogger().warning("Runner " + playerKiller.getName() + " not found in game.");
            return;
        }

        if(runner.teamID < 0 || runner.teamID == killerRunner.teamID)
        {
            return; // kills added for teamkills
        }

        killerRunner.stats.addKill();
    }


}
