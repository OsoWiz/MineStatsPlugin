package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.util.Helpers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.function.Function;

/**
 * PlayerJoin listener listens for connecting players.
 */
public class PlayerJoinListener extends SpeedrunListenerBase {

    Function<Player, Void> playerAddFunction;
    private Game game;

    public PlayerJoinListener(SpeedrunStats plugin, Game game) {
        super(plugin, PlayerJoinListener.class.getName());
        this.game = game;
        playerAddFunction = (player) -> {
            plugin.addPlayerToGame(player);
            return null;
        };
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if(game.isOn && !game.containsPlayer(event.getPlayer())) { // game is on and player is not in the game -> kick
            event.getPlayer().kickPlayer("You cannot join the server at this time.");
            return;
        } else if(game.isOn) { // game is on and player is already in the game -> do nothing
            return;
        }

        event.getPlayer().sendMessage("Welcome to the server!");
        Player player = event.getPlayer();
        playerAddFunction.apply(player);
    }

    @Override
    public void unregister() {
        super.unregister();
    }

}
