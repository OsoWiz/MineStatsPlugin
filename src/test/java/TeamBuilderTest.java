import dev.osowiz.speedrunstats.util.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class TeamBuilderTest {

    @Test
    public void testShuffling()
    { // shuffling is not viable for testing, so here are some prints
        System.out.println("Now testing shuffling.");
        List<String> sixTestNames = getTestNames().subList(0, 6);

        System.out.println("Names:");
        System.out.println(sixTestNames);
        System.out.println("Shuffled:");

        Shuffle.randomForSize(sixTestNames, 2).forEach(System.out::println);
        System.out.println("Second shuffle:");
        Shuffle.randomForSize(sixTestNames, 2).forEach(System.out::println);
        System.out.println("Third shuffle:");
        Shuffle.randomForSize(sixTestNames, 2).forEach(System.out::println);
    }

    @Test
    public void testChoices()
    {
        System.out.println("Now testing choices.");
        List<String> eightNames = getTestNames().subList(0, 8);
        System.out.println("Names:");
        System.out.println(eightNames);
        TreeMap<String, Integer> choices = new TreeMap<>();
        String first = eightNames.get(0);
        String second = eightNames.get(1);
        String third = eightNames.get(2);
        choices.put(first, 0);
        choices.put(second, 0);
        choices.put(third, 1);
        List<List<String>> teams = Shuffle.byChoice(eightNames, 3, choices);
        System.out.println("Teams:");
        teams.forEach(System.out::println);
        List<String> firstTeam = teams.get(0);
        System.out.println("Team 1:");
        System.out.println(firstTeam);
        Assert.assertTrue(firstTeam.size() >= 2);
        Assert.assertTrue(firstTeam.contains(first));
        Assert.assertTrue(firstTeam.contains(second));
        List<String> secondTeam = teams.get(1);
        System.out.println("Team 2:");
        System.out.println(secondTeam);
        Assert.assertTrue(secondTeam.size() >= 1);
        Assert.assertTrue(secondTeam.contains(third));

    }

    @Test
    public void testTeamCount()
    {
        TeamBuilder builder = new TeamBuilder(3);
        builder.setTeamSize(3);
        Assert.assertEquals(3, builder.getTeamCount(9));
        builder.setTeamSize(4);
        Assert.assertEquals(3, builder.getTeamCount(9));
        builder.setTeamSize(3);
        Assert.assertEquals(4, builder.getTeamCount(10));
        builder.setTeamCount(5);
        Assert.assertEquals(5, builder.getTeamCount(11));
        builder.setTeamCount(6);
        Assert.assertEquals(5, builder.getTeamCount(5));
    }


    private List<String> getTestNames()
    {
        ArrayList names = new ArrayList<String>();
        names.addAll(List.of("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hannah", "Ivan", "Jack", "Katie", "Liam", "Mia", "Nathan", "Olivia", "Peter", "Quinn", "Rachel", "Steve", "Tina", "Ursula", "Victor", "Wendy", "Xavier", "Yvonne", "Zach"));
        return names;
    }

}
