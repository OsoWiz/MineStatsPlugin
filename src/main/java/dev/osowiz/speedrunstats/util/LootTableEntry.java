package dev.osowiz.speedrunstats.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Random;

public enum LootTableEntry {
    // standard barter loot
    StandardEnchantedBook(Material.ENCHANTED_BOOK, 5, 1, 1, Enchantment.SOUL_SPEED),
    StandardIronBoots(Material.IRON_BOOTS, 8, 1, 1, Enchantment.SOUL_SPEED),
    StandardIronNugget(Material.IRON_NUGGET, 10, 9, 36),
    StandardFireResSplashPotion(Material.SPLASH_POTION, 10, 1 ,1, PotionType.FIRE_RESISTANCE),
    StandardFireResPotion(Material.POTION, 10, 1 ,1, PotionType.FIRE_RESISTANCE),
    StandardQuartz(Material.QUARTZ, 20, 8, 16),
    StandardGlowStone(Material.GLOWSTONE_DUST, 20, 5, 12),
    StandardMagmaCream(Material.MAGMA_CREAM, 20, 2, 6),
    StandardEnderPearl(Material.ENDER_PEARL, 20, 4, 8),
    StandardString(Material.STRING, 20, 8, 24),
    StandardFireCharge(Material.FIRE_CHARGE, 40, 1, 5),
    StandardGravel(Material.GRAVEL, 40, 8, 16),
    StandardLeather(Material.LEATHER, 40, 4, 10),
    StandardNetherBrick(Material.NETHER_BRICK, 40, 4, 16),
    StandardObsidian(Material.OBSIDIAN, 40, 1, 1),
    StandardCryingObsidian(Material.CRYING_OBSIDIAN, 40, 1, 3),
    StandardSoulSand(Material.SOUL_SAND, 40, 4, 16);
    // custom barter loot


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
        setItemStackData(item, weight, minDrop, maxDrop);
    }

    LootTableEntry(Material item, int weight, int minDrop, int maxDrop, PotionType potionType){
        setItemStackData(item, weight, minDrop, maxDrop);
        this.enchantment = null;
        Server server = Bukkit.getServer();
        ItemFactory factory = server.getItemFactory();
        PotionMeta potionMeta = (PotionMeta) factory.getItemMeta(item);
        if(potionMeta == null)
        {
            throw new IllegalArgumentException("Item is not a potion");
        }
        potionMeta.setBasePotionType(potionType);
        this.metaData = potionMeta;
    }

    LootTableEntry(Material item, int weight, int minDrop, int maxDrop, Enchantment enchantment){
        setItemStackData(item, weight, minDrop, maxDrop);
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
        ItemStack stack = new ItemStack(item, getRandomAmount(rand));
        addEnchantment(stack, enchantment, rand);
        return stack;
    }

    /**
     * Returns itemStack without enchantment, or with the items own enchantment.
     * @param rand
     * @return
     */
    public ItemStack getItemStack(Random rand)
    {
        ItemStack stack = new ItemStack(item, getRandomAmount(rand));
        if(this.enchantment != null)
        {
            addEnchantment(stack, enchantment, rand);
        }
        if(this.metaData != null)
        {
            stack.setItemMeta(metaData);
        }

        return stack;
    }

    // private methods

    /**
     * Sets the itemStack data.
     * @param item
     * @param weight
     * @param minDrop
     * @param maxDrop
     */
    private void setItemStackData(Material item, int weight, int minDrop, int maxDrop){
        this.item = item;
        this.weight = weight;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
    }

    private int getRandomAmount(Random rand)
    {
        return rand.nextInt(maxDrop - minDrop + 1) + minDrop;
    }

    private void addEnchantment(ItemStack stack, Enchantment enchantment, Random rand)
    {

        int maxLevel = enchantment.getMaxLevel();
        int minLevel = enchantment.getStartLevel();
        int applicableLevel = rand.nextInt(maxLevel) + minLevel;
        try {
            stack.addUnsafeEnchantment(enchantment, applicableLevel); // todo figure out to set enchantmentstoragemeta
        } catch (IllegalArgumentException e) {
            // log to console
            System.out.println("Enchantment " + enchantment.toString() + " of level " +  applicableLevel + " not applicable to item: " + e.getMessage());
        }
    }

}
