package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.EntitiesLoadEvent;

public class PiglinbruteSpawnPreventer extends SpeedrunListenerBase {

    public static final String name = "PiglinbruteSpawnPreventer";
    public PiglinbruteSpawnPreventer(SpeedrunStats plugin) {
        super(plugin, name);
    }

    @EventHandler
    public void onPiglinBruteLoad(EntitiesLoadEvent loadEvent)
    {
        for(Entity entity : loadEvent.getEntities())
        {
            if(entity.getType() == EntityType.PIGLIN_BRUTE)
            {
                entity.remove();
            }
        }
    }


}
