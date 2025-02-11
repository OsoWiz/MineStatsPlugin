package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.Material;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.List;
import java.util.Random;

public class PiglinLootTableFixer extends SpeedrunListenerBase{

    private final Random random = new Random();
    private final LootTable lootTable;
    public static final String name = "PiglinLootTableFixer";

    public PiglinLootTableFixer(SpeedrunStats plugin, LootTable table) {
        super(plugin, name);
        this.lootTable = table;
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

    @EventHandler
    public void onPiglinBarterEvent(PiglinBarterEvent barterEvent)
    {
        List<ItemStack> outCome = barterEvent.getOutcome();
        outCome.clear();
        LootContext ctx = new LootContext.Builder(barterEvent.getEntity().getLocation()).build();
        outCome.addAll(lootTable.populateLoot(random, ctx));
    }

}
