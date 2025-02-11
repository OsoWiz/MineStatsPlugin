package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.util.Helpers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static dev.osowiz.speedrunstats.util.Helpers.equalDoubles;

public class MovementPreventer extends SpeedrunListenerBase {

    public static final String name = "MovementPreventer";
    public MovementPreventer(SpeedrunStats plugin) {
        super(plugin, name);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if(to == null)
            return;

        if(!equalDoubles(from.getX(), to.getX())
                ||!equalDoubles(from.getX(), to.getX())
                || !equalDoubles(from.getZ() , to.getZ()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.f);
        player.setWalkSpeed(0.f);
    }

    @Override
    public void unregister() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFlySpeed(Helpers.flySpeed);
            player.setWalkSpeed(Helpers.walkSpeed);
        });
        super.unregister();
    }
}
