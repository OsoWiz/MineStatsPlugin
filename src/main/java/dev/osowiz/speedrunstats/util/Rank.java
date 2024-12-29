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

    private final int code;
    private final String name;
    private final ChatColor color;
    private Rank(int code, String name, ChatColor color)
    {
        this.code = code;
        this.name = name;
        this.color = color;
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

    public static Rank getRank(int code)
    {
        for(Rank rank : Rank.values())
        {
            if(rank.getCode() == code)
            {
                return rank;
            }
        }
        return null;
    }

    public static int maxRank()
    {
        return Rank.values().length - 1;
    }

    /**
     * Calculates the rank given the player score. (Usually weighted sum of past player scores.)
     * @param score is usually the average of all the scores of the player.
     * @return
     */
    static public Rank calculateRank(int score) {
        if(score < 0)
        {
            return UNRANKED;
        }
        double dScore = (double) score;
        double base = dScore / 15000 + 1;
        double dRank = Math.pow(base, dScore * 0.85) + dScore / 40 - 1;
        int rankCode = (int) Math.min(dRank, maxRank());
        return getRank(rankCode);
    }

}
