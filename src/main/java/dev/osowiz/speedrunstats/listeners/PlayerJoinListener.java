package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.util.Game;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.function.Function;

/**
 * PlayerJoin listener listens for connecting players.
 */
public class PlayerJoinListener extends SpeedrunListenerBase {

    boolean canJoin = true;
    boolean preventMovement;
    Function<Player, Void> playerAddFunction;
    public static final String name = "PlayerJoinListener";
    public PlayerJoinListener(SpeedrunStats plugin, boolean preventMovement) {
        super(plugin, name);
        playerAddFunction = (player) -> {
            plugin.addPlayerToGame(player);
            return null;
        };
        this.preventMovement = preventMovement;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!canJoin) {
            event.getPlayer().kickPlayer("You cannot join the server at this time.");
            return;
        }
        event.getPlayer().sendMessage("Welcome to the server!");
        Player player = event.getPlayer();
        if(preventMovement)
        {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(0.f);
        }

        playerAddFunction.apply(player);
    }

}
