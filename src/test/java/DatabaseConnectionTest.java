
import dev.osowiz.speedrunstats.documents.GameDocument;
import dev.osowiz.speedrunstats.documents.PlayerDocument;
import dev.osowiz.speedrunstats.documents.RunDocument;
import pojos.TestDocument;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DatabaseConnectionTest extends RealDatabaseTest {

    @Test
    public void testGameSerialization() {

        try {
            UUID testGameUUID = UUID.randomUUID();
            Date testDate = new Date();
            List<String> winnerNames = List.of("gamer1, gamer2");
            GameDocument testGame = new GameDocument(testGameUUID, "standard", 1, 1, 0.5f, 1, 1, 100, 100.0, winnerNames, testDate);
            database.insertGame(testGame);
            GameDocument foundGame = database.findGame(testGameUUID);
            Assert.assertEquals("standard", foundGame.getCategory());
            Assert.assertEquals(testGameUUID, foundGame.getGameID());
            Assert.assertTrue(database.deleteGame(testGameUUID));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testPlayerSerialization()
    {
        try {
            UUID testPlayerUUID = UUID.randomUUID();
            PlayerDocument testPlayer = new PlayerDocument(testPlayerUUID, "testplayer", 2, 5, 5, 5, 100, 4000.0);
            database.insertPlayer(testPlayer);
            PlayerDocument foundPlayer = database.findPlayer(testPlayerUUID);
            Assert.assertEquals("testplayer", foundPlayer.getName());
            Assert.assertEquals(testPlayerUUID, foundPlayer.getId());
            Assert.assertTrue(database.deletePlayer(testPlayerUUID));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testRunSerialization()
    {
        try {
            UUID testPlayerUUID = UUID.randomUUID();
            UUID testGameUUID = UUID.randomUUID();
            RunDocument testRun = new RunDocument(testPlayerUUID, testGameUUID, "", "", 0, 0, 0, 0, 0, new Date());
            UUID testRunUUID = testRun.getId();
            database.insertRun(testRun);
            RunDocument foundRun = database.findRunByPlayerAndGame(testPlayerUUID, testGameUUID);
            Assert.assertEquals(testRunUUID, foundRun.getId());
            Assert.assertEquals(testPlayerUUID, foundRun.getPlayerID());
            Assert.assertEquals(testGameUUID, foundRun.getGameID());
            Assert.assertTrue(database.deleteRun(testRunUUID));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testDb()
    {
        UUID testUid = UUID.randomUUID();
        TestDocument testDocument = new TestDocument(testUid, "test");
        testDocument.setName("test");
        database.getDb().getCollection("test", TestDocument.class).insertOne(testDocument);
        Assert.assertEquals("test", database.getDb().getCollection("test", TestDocument.class).find().first().getName());
        database.getDb().getCollection("test", TestDocument.class).deleteOne(new Document("name", "test"));
    }

}
