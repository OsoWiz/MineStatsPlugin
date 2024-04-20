package dev.osowiz.speedrunstats.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Random;

public enum LootTableEntry {

    StandardEnchantedBook(Material.ENCHANTED_BOOK, 5, 1, 1, Enchantment.SOUL_SPEED),
    StandardIronBoots(Material.IRON_BOOTS, 8, 1, 1, Enchantment.SOUL_SPEED),
    StandardIronNugget(Material.IRON_NUGGET, 10, 9, 36),
    StandardSplashPotion(Material.SPLASH_POTION, 10, 1 ,1, );

    private Material item;
    private int weight;
    private int minDrop;
    private int maxDrop;
    private Enchantment enchantment;
    private ItemMeta metaData;

    public Material getItem() {
        return item;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMinDrop() {
        return minDrop;
    }

    public void setMinDrop(int minDrop) {
        this.minDrop = minDrop;
    }

    public int getMaxDrop() {
        return maxDrop;
    }

    public void setMaxDrop(int maxDrop) {
        this.maxDrop = maxDrop;
    }

    LootTableEntry(Material item, int weight, int minDrop, int maxDrop){
        this.item = item;
        this.weight = weight;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
        this.enchantment = null;
        this.metaData = null;
    }

    LootTableEntry(Material item, int weight, int minDrop, int maxDrop, ItemMeta metaData){
        this.item = item;
        this.weight = weight;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
        this.enchantment = null;
        this.metaData = metaData;
    }

    LootTableEntry(Material item, int weight, int minDrop, int maxDrop, Enchantment enchantment){
        this.item = item;
        this.weight = weight;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
        this.enchantment = enchantment;
    }

    /**
     * Returns itemStack with the given enchantment.
     * @param rand
     * @param enchantment
     * @return
     */
    public ItemStack getItemStack(Random rand, Enchantment enchantment)
    {
        ItemStack stack = new ItemStack(item, rand.nextInt(maxDrop - minDrop) + minDrop);
        int maxLevel = enchantment.getMaxLevel();
        int minLevel = enchantment.getStartLevel();
        stack.addEnchantment(enchantment, rand.nextInt(maxLevel) + minLevel );
        return stack;
    }

    /**
     * Returns itemStack without enchantment, or with the items own enchantment.
     * @param rand
     * @return
     */
    public ItemStack getItemStack(Random rand)
    {
        ItemStack stack = new ItemStack(item, rand.nextInt(maxDrop - minDrop) + minDrop);
        if(this.enchantment != null)
        {
            int maxLevel = enchantment.getMaxLevel();
            int minLevel = enchantment.getStartLevel();
            stack.addEnchantment(enchantment, rand.nextInt(maxLevel) + minLevel );
        }
        return stack;
    }

}
