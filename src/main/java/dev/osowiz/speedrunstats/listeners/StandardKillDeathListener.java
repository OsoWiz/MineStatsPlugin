package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.games.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StandardKillDeathListener extends SpeedrunListenerBase {

    StandardSpeedrun game;
    public static final String name = "StandardKillDeathListener";
    public StandardKillDeathListener(SpeedrunStats plugin, StandardSpeedrun game) {
        super(plugin, name);
        this.game = game;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        SpeedRunner runner = game.getRunnerByID(player.getUniqueId());
        if(runner == null)
        {
            plugin.getLogger().warning("Runner " + player.getName() + " not found in the game for death event.");
            return;
        }
        runner.stats.addDeath();

        Entity killer = player.getKiller();
        if( !(killer instanceof Player playerKiller) ) { // if killer is a mob
            return;
        }

        SpeedRunner killerRunner = game.getRunnerByID(playerKiller.getUniqueId());
        if(killerRunner == null)
        {
            plugin.getLogger().warning("The killing player " + playerKiller.getName() + " not found in game.");
            return;
        }

        if(runner.teamID < 0 || runner.teamID == killerRunner.teamID)
        {
            return; // kills added for teamkills
        }

        killerRunner.stats.addKill();
    }


}
