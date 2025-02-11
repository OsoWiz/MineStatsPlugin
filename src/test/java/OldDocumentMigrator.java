import com.mongodb.client.MongoCollection;
import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.documents.GameDocument;
import dev.osowiz.speedrunstats.documents.PlayerDocument;
import dev.osowiz.speedrunstats.documents.RunDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class OldDocumentMigrator extends RealDatabaseTest {

@Test
public void convert()
{
    if(true)
    {
        System.out.println("This test is disabled.");
        Assert.assertTrue(true);
        return;
    }
    // This is not really a test and hence very sussy but I do not care
    // converts old documents to new ones.
    try {
        // first players
        MongoCollection<Document> playerCollection = database.getDb().getCollection("players");
        int convertedPlayers = 0;
        int allPlayers = (int) playerCollection.countDocuments();
        for(Document document : playerCollection.find())
        {
            if(document.get("_id") instanceof ObjectId) // not converted
            {
                String uidAsString = document.get("uid").toString();
                UUID playerUUID = UUID.fromString(uidAsString);
                PlayerDocument existingDoc = database.findPlayer(playerUUID);
                if(existingDoc != null) // already exists
                {
                    playerCollection.deleteOne(document);
                    continue;
                }
                PlayerDocument player = new PlayerDocument();
                player.setId(playerUUID);
                player.setName(document.getString("name"));
                player.setAllKills(document.getInteger("kills"));
                player.setAllDeaths(document.getInteger("deaths"));
                player.setGamesPlayed(document.getInteger("games"));
                player.setHighestScore(0);
                player.setFastestTimeInSeconds(Double.POSITIVE_INFINITY);
                database.insertPlayer(player);
                convertedPlayers++;
                playerCollection.deleteOne(document);
            }
        }
        System.out.println("Converted " + convertedPlayers + " players out of " + allPlayers + " players.");
        Assert.assertTrue(true);

        // runs next. They are now split to runs per game. So a new Game document must also be created in a smart way.
        MongoCollection<Document> runCollection = database.getDb().getCollection("runs");
        MongoCollection<GameDocument> gameCollection = database.getDb().getCollection("games", GameDocument.class);

        int convertedRuns = 0;
        int allRuns = (int) runCollection.countDocuments();
        for(Document document : runCollection.find())
        {
            if(document.get("_id") instanceof ObjectId) // not converted
            {
                String playerIDAsString = document.get("player_id").toString();
                UUID playerUUID = UUID.fromString(playerIDAsString);
                String category = document.getString("category");
                String playerName = document.getString("player_name");
                int score = document.getInteger("score");
                int kills = document.getInteger("kills");
                int deaths = document.getInteger("deaths");
                Date date = document.getDate("date");
                int teamID = document.getInteger("team_id");
                Double time = document.getDouble("time");
                UUID gameId = UUID.randomUUID();
                // find if an existing game already exists in the collection by the category and time between +- 10 seconds. Should be the same game.
                GameDocument game = gameCollection.find(new Document("category", category).append("date", new Document("$gte", new Date(date.getTime() - 10000)).append("$lte", new Date(date.getTime() + 10000)))).first();
                if(game == null) // game does not exist, lets create a new one
                {
                    GameDocument newGame = new GameDocument();
                    newGame.setCategory(category);
                    newGame.setDate(date);
                    newGame.setGameID(gameId);
                    database.insertGame(newGame);
                } else
                {
                    gameId = game.getGameID();
                }

                RunDocument run = new RunDocument(playerUUID, gameId, playerName, category, teamID, time, kills, deaths, score, date);
                database.insertRun(run);
                convertedRuns++;
                runCollection.deleteOne(document);
            }
        }
        System.out.println("Converted " + convertedRuns + " runs out of " + allRuns + " runs.");
        Assert.assertTrue(true);
        System.out.println("Now updating the game documents with data.");


    } catch (Exception e) {
        System.out.println("Failed to convert old documents to new ones.");
        e.printStackTrace();
        Assert.fail();
    }

}



@Test
public void convertTimeToInfinity()
{
    if(true)
    {
        System.out.println("This test is disabled.");
        Assert.assertTrue(true);
        return;
    }

    try {
        for (RunDocument doc : database.getRuns().find())
        {
            if (doc.getTime() > 1e5 && doc.getTime() < 1e10) {
                doc.setTime(Double.POSITIVE_INFINITY);
                database.updateRun(doc);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        Assert.fail();
    }

}

private void updateAllPlayers()
{
    int playersUpdated = 0;
    for(PlayerDocument player : database.getPlayers().find())
    {
        List<RunDocument> runsOfThePlayer = database.findRunsByPlayer(player.getId());
        player.setGamesPlayed(runsOfThePlayer.size());
        player.setAllKills(runsOfThePlayer.stream().mapToInt(RunDocument::getKills).sum());
        player.setAllDeaths(runsOfThePlayer.stream().mapToInt(RunDocument::getDeaths).sum());
        player.setHighestScore(runsOfThePlayer.stream().mapToInt(RunDocument::getScore).max().orElse(0));
        player.setFastestTimeInSeconds(runsOfThePlayer.stream().mapToDouble(RunDocument::getTime).min().orElse(Double.POSITIVE_INFINITY));
        database.updatePlayer(player);
    }
    System.out.println("Updated " + playersUpdated + " player documents.");
}


private void updateOldRunsWithRankInfo()
{
    for(RunDocument run : database.getRuns().find())
    {
        run.setPlayerRank(SpeedrunStats.calculateRank(run.getPlayerID(), run.getCategory(), run.getDate(), database).getCode());
        database.updateRun(run);
    }
}

private void updateRanks()
{
    for(PlayerDocument player : database.getPlayers().find())
    {
        // player.setRank(SpeedrunStats.calculateRank(player.getId(), "standard", new Date(), database).getCode());
        database.updatePlayer(player);
    }
}

private void updateGamesWithInfo()
{ // this method assumes all the relevant runs already have been updated with the game info
    int updatedGames = 0;
    for(GameDocument game : database.getGames().find())
    {
        List<RunDocument> runsOfTheGame = database.getRuns().find(new Document("game_id", game.getGameID())).into(new ArrayList<>());
        if(runsOfTheGame.isEmpty()) // no runners means erroneous game
        {
            database.deleteGame(game.getGameID());
            continue;
        }
        game.setNumRunners(runsOfTheGame.size());
        game.setHighestScore(runsOfTheGame.stream().max(Comparator.comparingInt(RunDocument::getScore)).get().getScore());
        game.setAvgRank((float) runsOfTheGame.stream().mapToInt(RunDocument::getPlayerRank).average().orElse(0));
        game.setAvgScore((int) runsOfTheGame.stream().mapToInt(RunDocument::getScore).average().orElse(0));
        List<Integer> teamIDs = runsOfTheGame.stream().map(RunDocument::getTeamID).distinct().toList();
        game.setNumTeams(teamIDs.size());
        List<RunDocument> winnerDocs = runsOfTheGame.stream().filter(run -> run.getTime() < 1e5).toList();
        if(game.getWinnerTeamID() != null && !winnerDocs.isEmpty()) // no winner has been set and there are  winners
        {
            game.setWinnerTeamID(winnerDocs.getFirst().getTeamID()); // assume all have the same team
            game.setWinnerNames(winnerDocs.stream().map(RunDocument::getPlayerName).toList());
            game.setCompletionTime(winnerDocs.getFirst().getTime());
        } else if(winnerDocs.isEmpty())
        {
            System.out.println("WARNING: Game " + game.getGameID() + " has no winners.");
        }
        updatedGames++;
        database.updateGame(game);
    }
    System.out.println("Updated " + updatedGames +" game documents.");
}

private void updateOldGameDocuments()
{
    // just update old documents that have object id. Only change the objectid to random uuid
    int updated = 0;
    for(Document game : database.getDb().getCollection("games").find())
    {
        if(game.get("_id") instanceof ObjectId)
        {
            UUID gameId = UUID.randomUUID();
            Float avgRank =  game.getDouble("avg_rank").floatValue();
            GameDocument newGame = new GameDocument(gameId, game.getString("category"), game.getInteger("num_runners"), game.getInteger("num_teams"), avgRank , game.getInteger("avg_score"), game.getInteger("winner_team_id"), game.getInteger("highest_score"), game.getDouble("completion_time"), game.getList("winner_names", String.class), game.getDate("date"));
            database.insertGame(newGame);
            database.getDb().getCollection("games").deleteOne(game);
            updated++;
        }
    }
    System.out.println("Updated " + updated + " game documents.");
}

private void convertRunObjectIdToUid()
{
    // runs next. They are now split to runs per game. So a new Game document must also be created in a smart way.
    MongoCollection<Document> runCollection = database.getDb().getCollection("runs");

    int convertedRuns = 0;
    int allRuns = (int) runCollection.countDocuments();
    for(Document document : runCollection.find())
    {
        if(document.get("_id") instanceof ObjectId) // not converted
        {
            RunDocument replacingRun = new RunDocument();
            replacingRun.setGameID(UUID.randomUUID());
            replacingRun.setCategory(document.getString("category"));
            replacingRun.setDate(document.getDate("date"));
            replacingRun.setKills(document.getInteger("kills"));
            replacingRun.setDeaths(document.getInteger("deaths"));
            replacingRun.setGameID(document.get("game_id", UUID.class));
            replacingRun.setPlayerID(document.get("player_id", UUID.class));
            replacingRun.setPlayerName(document.getString("player_name"));
            replacingRun.setPlayerRank(document.getInteger("player_rank"));
            replacingRun.setScore(document.getInteger("score"));
            replacingRun.setTeamID(document.getInteger("team_id"));
            replacingRun.setTime(document.getDouble("time"));
            database.insertRun(replacingRun);
            runCollection.deleteOne(document);
            convertedRuns++;
        }
    }
    System.out.println("Converted " + convertedRuns + " runs out of " + allRuns + " runs.");
}

}
