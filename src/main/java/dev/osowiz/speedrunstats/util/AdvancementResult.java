package dev.osowiz.speedrunstats.util;

public final class AdvancementResult
{
    private final int points;
    private final int coreAdvancementLevel;

    public AdvancementResult(int points, int coreAdvancementLevel)
    {
        this.points = points;
        this.coreAdvancementLevel = coreAdvancementLevel;
    }

    public int getPoints()
    {
        return points;
    }

    public int getAdvancementLevel()
    {
        return coreAdvancementLevel;
    }
}
