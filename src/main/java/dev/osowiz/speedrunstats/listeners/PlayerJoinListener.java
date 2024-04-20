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

    public PlayerJoinListener(SpeedrunStats plugin, boolean preventMovement) {
        super(plugin, "PlayerJoinListener");
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
        event.getPlayer().sendMessage("Your walk speed is: " + player.getWalkSpeed());
        if(preventMovement)
        {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(0.f);
            player.setWalkSpeed(0.f); // prevent movement
        }

        playerAddFunction.apply(player);
    }

}