package dev.osowiz.speedrunstats.util;

import java.util.*;

public class Shuffle {

    public static Random rand = new Random();

    public static <T> List<List<T>> randomForSize(List<T> objectsToShuffle, int bucketSize)
    {
        Collections.shuffle(objectsToShuffle);
        return rollDivide(objectsToShuffle, getBucketCountForSize(objectsToShuffle.size(), bucketSize));
    }

    public static <T> List<List<T>> randomForCount(List<T> objectsToShuffle, int bucketCount)
    {
        Collections.shuffle(objectsToShuffle);
        return rollDivide(objectsToShuffle, bucketCount);
    }

    public static <T> List<List<T>> byChoice(List<T> objectsToShuffle, int bucketCount, Map<T, Integer> choices)
    {
        if(bucketCount < 1 || bucketCount < choices.size() )
        {
            throw new IllegalArgumentException("bucketCount must be greater than 0 and greater than the amount of choices.");
        }

        List<List<T>> buckets = initiateBuckets(bucketCount);
        for(Map.Entry<T, Integer> choice : choices.entrySet())
        {
            if(choice.getKey() != null && choice.getValue() >= 0 && choice.getValue() < bucketCount)
            {
                buckets.get(choice.getValue()).add(choice.getKey());
                objectsToShuffle.remove(choice.getKey());
            }
        }
        // now fill the rest randomly from the bucket with the least members
        for(T t : objectsToShuffle)
        {
            int bucketIndex = buckets.stream()
                    .min(Comparator.comparingInt(List::size))
                    .map(buckets::indexOf).orElse(rand.nextInt(bucketCount));
            buckets.get(bucketIndex).add(t);
        }
        return buckets;
    }


    /**
     * Evenly divides the objects into buckets.
     * @param objectsToShuffle
     * @param bucketCount
     * @return
     * @param <T>
     */
    public static <T> List<List<T>> rollDivide(List<T> objectsToShuffle, int bucketCount)
    {
        if(bucketCount <= 0)
        {
            throw new IllegalArgumentException("Bucket count must be greater than 0.");
        }

        List<List<T>> buckets = initiateBuckets(bucketCount);
        int cursor = 0;
        for(T t : objectsToShuffle)
        {
            buckets.get(cursor).add(t);
            cursor = (cursor + 1) % bucketCount;
        }
        return buckets;
    }

    /**
     * Fills the buckets one by one.
     * @param objectsToShuffle
     * @param maxFill
     * @return
     * @param <T>
     */
    private static  <T> List<List<T>> fillDivide(List<T> objectsToShuffle, int maxFill)
    {
        if(maxFill <= 0)
        {
            throw new IllegalArgumentException("Max fill must be greater than 0.");
        }
        int bucketCount = (int) Math.ceil((double) objectsToShuffle.size() / maxFill);

        List<List<T>> buckets = initiateBuckets(bucketCount);
        int limiter = 0;
        int cursor = 0;
        for(T t : objectsToShuffle)
        {
            if(limiter >= maxFill)
            {
                limiter = 0;
                cursor++;
            }
            buckets.get(cursor).add(t);
            limiter++;
        }
        return buckets;
    }

    /**
     * Initializes a list of empty lists, so elements can just be added.
     * @param bucketCount
     * @return
     * @param <T>
     */
    private static <T> List<List<T>> initiateBuckets(int bucketCount)
    {
        List<List<T>> buckets = new ArrayList<>();
        for(int i = 0; i < bucketCount; i++)
        {
            buckets.add(new ArrayList<T>());
        }
        return buckets;
    }

    private static int getBucketCountForSize(int size, int bucketSize)
    {
        int overFlow = size % bucketSize;
        return size / bucketSize + (0 < overFlow ? 1 : 0);
    }

}
