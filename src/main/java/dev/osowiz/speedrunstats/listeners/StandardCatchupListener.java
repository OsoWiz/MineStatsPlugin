package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.gametypes.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.Helpers;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import dev.osowiz.speedrunstats.util.StandardSpeedrunScoring;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class StandardCatchupListener extends SpeedrunListenerBase {

    StandardSpeedrun game;
    public StandardCatchupListener(SpeedrunStats plugin, StandardSpeedrun game) {
        super(plugin, "StandardCatchupListener");
        this.game = game;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        SpeedRunner runner = game.getRunnerByName(player.getName());
        if(runner == null)
        {
            plugin.getLogger().warning("Runner " + player.getName() + " not found in game.");
            return;
        }

        if(game.getCooldownTime() < Helpers.nanoToSeconds(System.nanoTime() - runner.lastCooldown))
        {
            for(ItemStack stack : StandardSpeedrunScoring.catchupStack)
            {
                player.getInventory().addItem(stack);
            }
            runner.setCooldown();
        }
    }




}
