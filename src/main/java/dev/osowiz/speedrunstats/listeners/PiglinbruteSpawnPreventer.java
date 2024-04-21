package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class PiglinbruteSpawnPreventer extends SpeedrunListenerBase {

    public static final String name = "PiglinbruteSpawnPreventer";
    public PiglinbruteSpawnPreventer(SpeedrunStats plugin) {
        super(plugin, name);
    }

    @EventHandler
    public void onPiglinBruteSpawn(CreatureSpawnEvent event) {
        if(event.getEntityType() == EntityType.PIGLIN_BRUTE) {
            event.setCancelled(true);
        }
    }


}
