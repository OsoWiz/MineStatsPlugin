package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EndCrystalDestroyer extends SpeedrunListenerBase {

    public EndCrystalDestroyer(SpeedrunStats plugin) {
        super(plugin, EndCrystalDestroyer.class.getName());
    }

    @EventHandler
    public void onEndCrystalDestroy(PlayerChangedWorldEvent event) {

        World playersWorld = event.getPlayer().getWorld();
        if (playersWorld.getEnvironment() == World.Environment.THE_END) {
            this.plugin.getLogger().info("Destroying end crystals..");
            List<Entity> endEntities = playersWorld.getEntities();
            for(Entity entity : endEntities) {
                if(entity instanceof EnderCrystal) {
                    entity.remove();
                }
            }
        // crystals are destroyed, this can be unregistered
        this.unregister();
        } // end of if
    } // end of onEndCrystalDestroy
}
