package dev.osowiz.speedrunstats.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class StandardSpeedrunScoring {

    static public final int MaxRank = 7;

    static public final int finalMission = 7;

    static public final int[] corePoints = new int[] {10, 10, 20, 20, 15, 25, 25, 25};

    static public final ItemStack[] catchupStack = {
            new ItemStack(Material.OAK_LOG, 4),
            new ItemStack(Material.COBBLESTONE, 4),
            new ItemStack(Material.STONE_AXE, 1),
            new ItemStack(Material.STONE_PICKAXE, 1),
            new ItemStack(Material.BREAD, 3)
    };
    static public final String[] coreObjectives = new String[]{
            "Find iron",
            "Get a bucket of lava",
            "Enter the Nether",
            "Find a nether fortress",
            "Obtain some blaze rods",
            "Find the stronghold",
            "Enter the End dimension",
            "Kill the dragon",
    };

    static public AdvancementResult getScoreForAdvancement(String advancementKey) {

        int score = 0;
        int level = -1; // -1 signifies that the advancement is not a core advancement

        switch (advancementKey) {
            // core advancements
            case "story/smelt_iron": // core advancement
            {
                level = 0;
                score = corePoints[level];
                break;
            }
            case "story/lava_bucket": // core advancement
            {
                level = 1;
                score = corePoints[level];
                break;
            }
            case "story/enter_the_nether": // core advancement
            {
                level = 2;
                score = corePoints[level];
                break;
            }
            case "nether/find_fortress": // core advancement
            {
                level = 3;
                score = corePoints[level];
                break;
            }
            case "nether/obtain_blaze_rod": // core advancement
            {
                level = 4;
                score = corePoints[level];
                break;
            }
            case "story/follow_ender_eye": // core advancement
            {
                level = 5;
                score = corePoints[level];
                break;
            }
            case "story/enter_the_end": // core advancement
            {
                level = 6;
                score = corePoints[level];
                break;
            }
            case "end/kill_dragon": // core advancement
            {
                level = 7;
                score = corePoints[level];
                break;
            }
            // other advancements
            case "adventure/sleep_in_bed": {
                score = 1;
                break;
            }
            case "story/mine_stone":
            case "story/upgrade_tools":
            case "story/deflect_arrow":
            case "nether/return_to_sender":
            case "nether/distract_piglin":
            case "adventure/kill_a_mob":
            case "adventure/shoot_arrow": {
                score = 2;
                break;
            }
            case "story/obtain_armor":
            case "story/iron_tools":
            case "husbandry/fishy_business":
            case "adventure/ol_betsy":
            case "adventure/sniper_duel": {
                score = 3;
                break;
            }
            case "story/form_obsidian":
            case "story/obtain_beehive":
            case "nether/charge_respawn_anchor":
            case "adventure/voluntary_exile":
            case "adventure/trade":
            case "husbandry/tame_an_animal": {
                score = 4;
                break;
            }
            case "adventure/throw_trident":
            case "adventure/totem_of_undying":
            case "adventure/summon_iron_golem": {
                score = 5;
                break;
            }
            case "story/mine_diamond": {
                score = 6;
                break;
            }
            case "story/cure_zombie_villager":
            case "nether/explore_nether":
            case "nether/find_bastion":
            case "nether/loot_bastion": {
                score = 8;
                break;
            }
            case "story/shiny_gear": {
                score = 10;
                break;
            }
            case "story/enchant_item": {
                score = 12;
                break;
            }
            case "adventure/hero_of_the_village": {
                score = 15;
                break;
            }
            case "end/find_end_city": {
                score = 20;
                break;
            }
            case "end/elytra": {
                score = 30;
                break;
            }
            case "nether/summon_wither": // wtf
            {
                score = 33;
                break;
            }
            case "adventure/adventuring_time": {
                score = 40;
                break;
            }
            case "nether/netherite_armor": {
                score = 60;
                break;
            }
            case "nether/create_full_beacon": {
                score = 100;
                break;
            }
            default:
                score = 0;
        }
        return new AdvancementResult(score, level);
    }

    /**
     * Calculates the rank (can be used as an index to rank names)
     * @param score is usually the average of all the scores of the player.
     * @return
     */
    static public int calculateRank(int score) {
        if(score < 0)
        {
            return 0;
        }
        double dScore = (double) score;
        double base = dScore / 15000 + 1;
        double dRank = Math.pow(base, dScore) + dScore / 100 - 1;
        return (int) Math.min(dRank, MaxRank);
    }

}
