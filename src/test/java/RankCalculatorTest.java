import dev.osowiz.speedrunstats.util.Rank;
import org.junit.Test;

public class RankCalculatorTest {

    @Test
    public void rankScoreCalculationTest()
    {
        for(Rank rank : Rank.values())
        {
            int requiredScore = rank.getRequiredScore();
            System.out.println(rank.getName() + " requires " + requiredScore + " points");
            if(rank.getCode() < 2)
            {
                assert requiredScore == 0;
            }
            else
            {
                assert requiredScore > 0;
            }
        }
    }
}
