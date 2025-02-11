package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.runnable.SpawnerFinder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.AsyncStructureSpawnEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.List;

public class SpawnerFortifier extends SpeedrunListenerBase {

    public SpawnerFortifier(SpeedrunStats plugin)
    {
        super(plugin, SpawnerFortifier.class.getName());
    }

    @EventHandler
    public void onFortressSpawn(AsyncStructureSpawnEvent e)
    {
        if(e.getStructure().equals(Structure.FORTRESS))
        {
            BoundingBox bb = e.getBoundingBox();
            plugin.getLogger().info("Fortress spawned at " + bb.getCenter());
            Collection<Chunk> chunks = e.getWorld().getIntersectingChunks(bb);
            new SpawnerFinder(plugin, chunks).runTask(plugin);
        }

    }

}
