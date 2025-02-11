package dev.osowiz.speedrunstats.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Collection;

public class SpawnerFinder extends BukkitRunnable {

    private final Collection<Chunk> chunks;
    private final Plugin plugin;
    public SpawnerFinder(Plugin plugin, Collection<Chunk> chunks)
    {
        this.plugin = plugin;
        this.chunks = chunks;
    }

    @Override
    public void run() {
        BlockData spawnerData = Bukkit.createBlockData(Material.SPAWNER);
        int spawnersToBeFound = 2;
        for(Chunk chunk : this.chunks)
        {
            if(spawnersToBeFound == 0)
            {
                return;
            }
            if(chunk.contains(spawnerData))
            {
                plugin.getLogger().info("Spawner found in chunk " + chunk.getX() + " " + chunk.getZ());
                Block spawnerBlock = getSpawnerBlock(chunk);
                if(spawnerBlock != null)
                {
                    plugin.getLogger().info("Spawner found at " + spawnerBlock.getLocation());
                    try {
                        Field strength = Block.class.getDeclaredField("strength");
                        strength.setAccessible(true);
                        strength.set(spawnerBlock, 100000f);
                    } catch (NoSuchFieldException | IllegalAccessException e)
                    {
                        plugin.getLogger().info("Could not find strength field in Block class");
                    }
                    spawnersToBeFound--;
                }
            }
        }
    }

    public static Block getSpawnerBlock(Chunk chunk)
    {
        for(int x = 0; x < 16; x++)
        {
            for(int z = 0; z < 16; z++)
            {
                for(int y = 0; y < 256; y++)
                {
                    Block block = chunk.getBlock(x, y, z);
                    if(block.getType() == Material.SPAWNER)
                    {
                        return block;
                    }
                }
            }
        }
        return null;
    }

}
