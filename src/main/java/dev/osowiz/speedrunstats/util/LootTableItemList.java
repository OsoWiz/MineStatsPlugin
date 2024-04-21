package dev.osowiz.speedrunstats.util;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LootTableItemList {

    private Collection<LootTableEntry> entries;
    private int weightSum;

    LootTableItemList(Collection<LootTableEntry> entries)
    {
        if(entries.isEmpty())
        {
            throw new IllegalArgumentException("Entry list cannot be empty");
        }
        this.entries = entries;
        for(LootTableEntry entry : entries)
        {
            weightSum += entry.getWeight();
        }
        if(weightSum == 0)
        {
            throw new IllegalArgumentException("List contains malformed entries with zero weights.");
        }

    }

    /**
     * Returns a random entry from the list based on the weight of each entry
     * @param random to generate random number.
     * @return Entry on the list
     */
    public LootTableEntry getRandomEntry(Random random)
    {
        int rand = random.nextInt(weightSum);
        int sum = 0;
        for(LootTableEntry entry : entries)
        {
            sum += entry.getWeight();
            if(rand < sum)
            {
                return entry;
            }
        }
        throw new IllegalStateException("Entry list is empty somehow..");
    }

}
