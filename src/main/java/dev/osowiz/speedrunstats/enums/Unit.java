package dev.osowiz.speedrunstats.enums;

public enum Unit { // todo perhaps set up certain pool of possible symbols? So that distance based metrics would always have the same unit, etc.

    COUNT("Count", "", 1), // count has no symbol
    METER("Meter", "m", 1),
    CENTIMETER("Centimeter", "m", 0.01f),
    TICKS("Ticks", "s", 0.05f), // assume standard tickrate of 20 ticks per second
    HEARTS("Hearts", "❤", 0.05f), // apparently damage based statistics are recorded in 10ths of healf-hearts. (so 0.5 * 1 / 10 * 20 = 1 )
    SECONDS("Seconds", "s", 1);

    private final String name;
    private final String symbol;
    private final float conversionRate; // conversion rate is the conversion to a usable standard unit, such as seconds or meters.
    private Unit(String name, String symbol, float conversionRate)
    {
        this.name = name;
        this.symbol = symbol;
        this.conversionRate = conversionRate;
    }

    public String toString()
    {
        return name + " (" + symbol + ")";
    }

    /** Converts a value from this unit to the standard unit.
     * @param value
     * @return
     */
    public String valueToThisUnit(int value)
    {
        return value * conversionRate + " " + symbol;
    }


}
