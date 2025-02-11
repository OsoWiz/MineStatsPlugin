package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.games.StandardSpeedrun;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * WorldListener is used to initialize members available only after the world has loaded.
 */
public class WorldListener extends SpeedrunListenerBase {

    private final Game game;

    public WorldListener(SpeedrunStats plugin, Game game) {
        super(plugin, WorldListener.class.getName());
        this.game = game;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        game.registerScoreBoardManager();
        this.unregister();
    }

}
