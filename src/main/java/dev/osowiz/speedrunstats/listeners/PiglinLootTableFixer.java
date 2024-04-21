package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PiglinLootTableFixer extends SpeedrunListenerBase{

    public static final String name = "PiglinLootTableFixer";
    public PiglinLootTableFixer(SpeedrunStats plugin) {
        super(plugin, name);
    }

    @EventHandler
    public void onPiglinLootGeneration(LootGenerateEvent event) {
        if(event.getEntity() instanceof Piglin) {
            Piglin spawnedPiglin = (Piglin) event.getEntity();
            List<ItemStack> items = event.getLoot();

            plugin.getLogger().info("Piglin spawned with loot: ");
            for(ItemStack item : items) {
                plugin.getLogger().info(item.getData().toString());
            }

        }
    }
}
