package dev.osowiz.speedrunstats.util;

import jdk.internal.net.http.common.Pair;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class StandardPiglinLootTable implements LootTable {


    private final LootTableItemList itemList;
    private final LootTableEntry[] entries = {
            LootTableEntry.StandardEnchantedBook,
            LootTableEntry.StandardIronBoots,
            LootTableEntry.StandardIronNugget,
            LootTableEntry.StandardFireResSplashPotion,
            LootTableEntry.StandardFireResPotion,
            LootTableEntry.StandardQuartz,
            LootTableEntry.StandardGlowStone,
            LootTableEntry.StandardMagmaCream,
            LootTableEntry.StandardEnderPearl,
            LootTableEntry.StandardString,
            LootTableEntry.StandardFireCharge,
            LootTableEntry.StandardGravel,
            LootTableEntry.StandardLeather,
            LootTableEntry.StandardNetherBrick,
            LootTableEntry.StandardObsidian,
            LootTableEntry.StandardCryingObsidian,
            LootTableEntry.StandardSoulSand
    };

    public StandardPiglinLootTable(){
        itemList = new LootTableItemList(Arrays.asList(entries));
    }

    @Override
    public @NotNull Collection<ItemStack> populateLoot(Random random, @NotNull LootContext context) {
        ArrayList<ItemStack> items = new ArrayList<>();
        LootTableEntry entry = this.itemList.getRandomEntry(random);
        items.add(entry.getItemStack(random));
        return items;
    }

    @Override
    public void fillInventory(@NotNull Inventory inventory, Random random, @NotNull LootContext context) {
        return;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey("speedrunstats", "standard_piglin_loot_table");
    }

}
