package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakPreventer extends SpeedrunListenerBase {

    public static final String name = "BlockBreakPreventer";
    public BlockBreakPreventer(SpeedrunStats plugin) {
        super(plugin, name);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }
}
