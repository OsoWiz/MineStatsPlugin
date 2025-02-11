package dev.osowiz.speedrunstats.util;

import org.bukkit.ChatColor;

public enum Rank {

    UNRANKED(0, "Unranked", ChatColor.WHITE),
    ASS(1, "Ass", ChatColor.BLACK),
    WOOD(2, "Wood", ChatColor.YELLOW),
    SILVER(3, "Silver", ChatColor.GRAY),
    LAPIS(4, "Lapis", ChatColor.BLUE),
    GOLD(5, "Gold", ChatColor.GOLD),
    EMERALD(6, "Emerald", ChatColor.GREEN),
    DIAMOND(7, "Diamond", ChatColor.AQUA),
    NETHERITE( 8, "Netherite", ChatColor.DARK_GRAY),
    ENCHANTED(9, "Enchanted", ChatColor.DARK_PURPLE);

    public static class Formula { // this forces classloader to initialize these before enums.
        public static double base = 1.01;
        public static double exponentScale = 1.15;
        public static double linearCoefficient = 0.00045;
    }
    private final int code;
    private final String name;
    private final ChatColor color;
    private final int requiredScore;
    private Rank(int code, String name, ChatColor color)
    {
        this.code = code;
        this.name = name;
        this.color = color;
        this.requiredScore = calculateRequiredScore(code);
    }

    public int getCode() {
        return code;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String toString()
    {
        return color + name + ChatColor.RESET;
    }

    public int getRequiredScore()
    {
        return requiredScore;
    }

    public static Rank fromCode(int code)
    {
        for(Rank rank : Rank.values())
        {
            if(rank.getCode() == code)
            {
                return rank;
            }
        }
        return Rank.UNRANKED;
    }

    public static int maxRank()
    {
        return Rank.values().length - 1;
    }

    /**
     * Calculates the rank given the player score. Does not assume minimum threshold of played games.
     * @param score is usually the average of all the scores of the player.
     * @return
     */
    static public Rank calculateRank(int score) {
        if(score < 0)
        {
            return UNRANKED;
        }
        double drank = rankFunction((double) score);
        int rankCode = (int) Math.min(drank, maxRank());
        return fromCode(rankCode);
    }

    static public int calculateRequiredScore(Rank rank)
    {
        return calculateRequiredScore(rank.code);
    }

    static public int calculateRequiredScore(int rankCode)
    {
        // The rank function is transcendental and cannot be inverted. So the correct answer has to be searched for.
        if(rankCode < 2)
        { // for unranked and worst rank you don't need any score.
            return 0;
        }
        double target = rankCode;
        double guess = 100.0;
        double diff = 1e6f;
        for(int i = 0; i < 10; i++) {
            double fx = rankFunction(guess);
            double dx = rankDerivative(guess);
            double step = ((target - fx) / dx);
            guess = guess + step;
            diff = target - fx;
        } // while(Math.abs(diff) > 0.1f || target - (int) guess != 0);
        if(rankFunction(Math.floor(guess)) < target)
        { // there may be a possibility that the optimization gets stuck below the target.
            guess += 1;
        }

        return (int) Math.floor(guess);
    }

    private static double rankFunction(double x){
       return (float) (Math.pow(Formula.base, x * Formula.exponentScale) + Formula.linearCoefficient * x);
    }

    private static double rankDerivative(double x) {
        return (float) (Math.log(Formula.base) * Formula.exponentScale * Math.pow(Formula.base, x) + Formula.linearCoefficient);
    }

}
