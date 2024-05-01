package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerQuitListener extends SpeedrunListenerBase {

    public static final String name = "PlayerQuitListener";
    SpeedrunStats plugin;
    public PlayerQuitListener(SpeedrunStats plugin) {
        super(plugin, name);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) { // deal damage to player on quit, to prevent cheating
        if(!this.plugin.hasStarted) // don't punish if the game has not started
            return;

        Player player = event.getPlayer();
        player.damage(player.getHealth() * 0.5d);
        // and make player drop all items in their inventory
        Inventory pInventory = player.getInventory();
        ItemStack[] items = pInventory.getContents();
        pInventory.clear();
        for(ItemStack item : items) {
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }
}
