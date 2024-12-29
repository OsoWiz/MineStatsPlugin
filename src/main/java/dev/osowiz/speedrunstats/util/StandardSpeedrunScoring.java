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
            case "adventure/sleep_in_bed":
            case "nether/obtain_crying_obsidian":{
                score = 1;
                break;
            }
            case "story/mine_stone":
            case "story/upgrade_tools":
            case "story/deflect_arrow":
            case "nether/return_to_sender":
            case "nether/distract_piglin":
            case "adventure/kill_a_mob":
            case "adventure/shoot_arrow":
            case "adventure/spyglass_at_ghast": {
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
            case "nether/ride_strider":
            case "adventure/voluntary_exile":
            case "adventure/trade":
            case "husbandry/tame_an_animal": {
                score = 4;
                break;
            }
            case "adventure/throw_trident":
            case "adventure/totem_of_undying":
            case "adventure/summon_iron_golem":
            case "nether/charge_respawn_anchor": {
                score = 5;
                break;
            }
            case "story/mine_diamond":
            case "story/shiny_gear":
            case "nether/obtain_ancient_debris":
            case "nether/fast_travel": {
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
            case "adventure/spyglass_at_dragon": {
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
     * This method calculates the score based on eyes of ender, or their components collected.
     * @param runner to calculate for
     * @return
     */
    static public int getEyeOfEnderScore(SpeedRunner runner) {
        if(finalMission <= runner.stats.currentObjectiveID)
        {
            return 0;
        }

        int numEyes = 0;
        int numRods = 0;
        int numBlazePowder = 0;
        int numPearls = 0;
        for(ItemStack stack : runner.spigotPlayer.getInventory().getContents())
        {
            if(stack == null)
            {
                continue;
            }

            switch(stack.getType())
            {
                case ENDER_EYE:
                    numEyes += stack.getAmount();
                    break;
                case BLAZE_ROD:
                    numRods += stack.getAmount();
                    break;
                case BLAZE_POWDER:
                    numBlazePowder += stack.getAmount();
                    break;
                case ENDER_PEARL:
                    numPearls += stack.getAmount();
                    break;
                default:
                    break;
            }
        }
        return calculateEyeScore(numEyes, numRods, numBlazePowder, numPearls);
    }

    /**
     * This method calculates the score based on eyes of ender, or their components collected.
     * @param team to calculate for
     * @return
     */
    static public int getEyeOfEnderScore(SpeedrunTeam team) {
        if(finalMission <= team.getCurrentObjectiveID()) // if player has not entered the end
        {
            return 0;
        }

        int numEyes = 0;
        int numRods = 0;
        int numBlazePowder = 0;
        int numPearls = 0;
        for(SpeedRunner runner : team.getRunners()) {
            for (ItemStack stack : runner.spigotPlayer.getInventory().getContents()) {
                if (stack == null) {
                    continue;
                }

                switch (stack.getType()) {
                    case ENDER_EYE:
                        numEyes += stack.getAmount();
                        break;
                    case BLAZE_ROD:
                        numRods += stack.getAmount();
                        break;
                    case BLAZE_POWDER:
                        numBlazePowder += stack.getAmount();
                        break;
                    case ENDER_PEARL:
                        numPearls += stack.getAmount();
                        break;
                    default:
                        break;
                }
            }
        }
        return calculateEyeScore(numEyes, numRods, numBlazePowder, numPearls);
    }

    private static int calculateEyeScore(int numEyes, int numRods, int numPowder, int numPearls) {
        int score = 2 * numEyes + Math.min(12, numPearls) / 2
                + Math.min(12, numPowder + 2 * numRods) / 2;
        return Math.min(score, 24);
    }

    public static int calculateDeathScore(int deaths) {
        return Math.max(0, 10 - 2 * deaths);
    }

    /**
     * Calculates the elo boost for a a player or a team based on their rank and the average rank of the game.
     * @param avgRank of the game at hand
     * @param boostRank to calculate boost for.
     * @return
     */
    public static float calculateEloBoost(float avgRank, float boostRank)
    {
        return (float) Math.max(1.f, Math.pow(1.1f, avgRank - boostRank));
    }

}
