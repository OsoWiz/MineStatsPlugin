package dev.osowiz.speedrunstats.util;

import jdk.internal.net.http.common.Pair;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class StandardPiglinLootTable implements LootTable {

    private final Pair<Material, Integer>[] standardItemList = new Pair[]{
        Pair.pair(Material.ENCHANTED_BOOK., 1)
    };

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext context) {
        ArrayList<ItemStack> items = new ArrayList<>();
        int min = 1; int max = 2;
        Material itemMaterial = Material.OAK_LOG;


        if(rand < 0.018)
        {
            
        } else if (false) {
            
        }

        items.add(new ItemStack(Material.GOLD_NUGGET, 1));
        return items;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext context) {
        return;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey("speedrunstats", "standard_piglin_loot_table");
    }

}
