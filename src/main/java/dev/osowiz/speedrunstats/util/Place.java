package dev.osowiz.speedrunstats.util;

import org.bukkit.ChatColor;

public enum Place {

    Unknown(0, "Unknown", ChatColor.DARK_GRAY),
    First(1, "First", ChatColor.GOLD),
    Second(2, "Second", ChatColor.GRAY),
    Third(3, "Third", ChatColor.DARK_RED),
    Fourth(4, "Fourth", ChatColor.WHITE),
    Fifth(5, "Fifth", ChatColor.WHITE),
    Sixth(6, "Sixth", ChatColor.WHITE),
    Seventh(7, "Seventh", ChatColor.WHITE),
    Eighth(8, "Eighth", ChatColor.WHITE),
    Ninth(9, "Ninth", ChatColor.WHITE),
    Tenth(10, "Tenth", ChatColor.WHITE);

    private final int number;
    private final String name;
    private final ChatColor color;

    private Place(int number, String name, ChatColor color)
    {
        this.number = number;
        this.name = name;
        this.color = color;
    }

    public String toString()
    {
        return this.name;
    }

    public int getNumber()
    {
        return this.number;
    }

    public ChatColor getColor()
    {
        return this.color;
    }

    public String formattedToString()
    {
        return this.color + this.name + ChatColor.RESET;
    }

    public static Place getPlace(int number)
    {
        if(number < 1 || Place.values().length < number)
            return null;

        for(Place place : Place.values())
        {
            if(place.getNumber() == number)
            {
                return place;
            }
        }
        return null;
    }

}
