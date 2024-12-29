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

    boolean preventMovement;
    Function<Player, Void> playerAddFunction;
    private Game game;
    public PlayerJoinListener(SpeedrunStats plugin, Game game, boolean preventMovement) {
        super(plugin, PlayerJoinListener.class.getName());
        this.game = game;
        playerAddFunction = (player) -> {
            plugin.addPlayerToGame(player);
            return null;
        };
        this.preventMovement = preventMovement;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if(game.isOn && !game.containsPlayer(event.getPlayer())) { // game is on and player is not in the game -> kick
            event.getPlayer().kickPlayer("You cannot join the server at this time.");
            return;
        }else if(game.isOn) { // game is on and player is already in the game -> do nothing
            return;
        }

        event.getPlayer().sendMessage("Welcome to the server!");
        Player player = event.getPlayer();
        if(preventMovement)
        {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(0.f);
            player.setWalkSpeed(0.f);
        }

        playerAddFunction.apply(player);
    }

}
