package dev.osowiz.speedrunstats;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import dev.osowiz.speedrunstats.documents.GameDocument;
import dev.osowiz.speedrunstats.documents.PlayerDocument;
import dev.osowiz.speedrunstats.documents.RunDocument;
import dev.osowiz.speedrunstats.util.Helpers;
import dev.osowiz.speedrunstats.util.Rank;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.*;

public class SpeedrunDB implements AutoCloseable {

    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<PlayerDocument> players;
    private MongoCollection<GameDocument> games;
    private MongoCollection<RunDocument> runs;

    public SpeedrunDB(String connectionString)
    {
        ConnectionString connection = new ConnectionString(connectionString);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        // CodecRegistry uuidRegistry = fromCodecs(new UuidCodec(UuidRepresentation.STANDARD));
        CodecRegistry combinedRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connection)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(combinedRegistry)
                .build();
        // Use the connection string for testing
        client = MongoClients.create(clientSettings);
        db = client.getDatabase("speedrundb");
        players = db.getCollection("players", PlayerDocument.class);
        games = db.getCollection("games", GameDocument.class);
        runs = db.getCollection("runs", RunDocument.class);
    }

    public void close()
    {
        client.close();
    }

    public MongoDatabase getDb()
    {
        return db;
    }

    public void insertPlayer(PlayerDocument player)
    {
        players.insertOne(player);
    }

    public void insertGame(GameDocument game)
    {
        games.insertOne(game);
    }

    public void insertRun(RunDocument run)
    {
        runs.insertOne(run);
    }

    public MongoCollection<PlayerDocument> getPlayers()
    {
        return players;
    }

    public MongoCollection<GameDocument> getGames()
    {
        return games;
    }

    public MongoCollection<RunDocument> getRuns()
    {
        return runs;
    }

    public boolean updatePlayerStats(UUID playerID, Rank newRank)
    {
        PlayerDocument player = this.findPlayer(playerID);
        if(player == null)
        {
            return false;
        }
        List<RunDocument> runsOfThePlayer = this.findRunsByPlayer(playerID);
        player.setGamesPlayed(runsOfThePlayer.size());
        player.setAllKills(runsOfThePlayer.stream().mapToInt(RunDocument::getKills).sum());
        player.setAllDeaths(runsOfThePlayer.stream().mapToInt(RunDocument::getDeaths).sum());
        player.setHighestScore(runsOfThePlayer.stream().mapToInt(RunDocument::getScore).max().orElse(0));
        player.setFastestTimeInSeconds(runsOfThePlayer.stream().mapToDouble(RunDocument::getTime).min().orElse(Double.POSITIVE_INFINITY));
        player.setRankCode(newRank.getCode());
        this.updatePlayer(player);
        return true;
    }

    /**
     * Get a player by their UUID.
     * @return
     */
    public PlayerDocument findPlayer(UUID playerID)
    {
        return players.find(eq("_id", playerID)).first();
    }

    /**
     * Get a game by its UUID.
     * @return
     */
    public GameDocument findGame(UUID gameID)
    {
        return games.find(eq("_id", gameID)).first();
    }

    public boolean updateGame(GameDocument game)
    {
        return games.replaceOne(eq("_id", game.getGameID()), game).wasAcknowledged();
    }

    public List<GameDocument> getFastestNGames(int n)
    {
        return games.find().sort(Sorts.ascending("completion_time")).limit(n).into(new ArrayList<>());
    }

    public List<PlayerDocument> getTopNRankedPlayers(int n)
    {
        return players.find().sort(Sorts.descending("rank_code")).limit(n).into(new ArrayList<>());
    }

    public List<PlayerDocument> getTopNPlayersByScore(int n)
    {
        return players.find().sort(Sorts.descending("highest_score")).limit(n).into(new ArrayList<>());
    }

    public List<PlayerDocument> getTopNPlayersByKills(int n)
    {
        return players.find().sort(Sorts.descending("kills")).limit(n).into(new ArrayList<>());
    }

    public List<PlayerDocument> getTopNPlayersByDeaths(int n)
    {
        return players.find().sort(Sorts.descending("deaths")).limit(n).into(new ArrayList<>());
    }

    public List<RunDocument> findRunsByPlayer(UUID playerID)
    {
        return runs.find(eq("player_id", playerID)).into(new ArrayList<>());
    }

    public boolean updatePlayer(PlayerDocument player)
    {
        return players.replaceOne(eq("_id", player.getId()), player).wasAcknowledged();
    }

    public boolean updateRun(RunDocument run)
    {
        return runs.replaceOne(eq("_id", run.getId()), run).wasAcknowledged();
    }

    public void insertRuns(List<RunDocument> runs)
    {
        this.runs.insertMany(runs);
    }

    public List<RunDocument> findRunsByGame(UUID gameID)
    {
        return runs.find(eq("game_id", gameID)).into(new ArrayList<>());
    }

    public RunDocument findRunByPlayerAndGame(UUID playerID, UUID gameID)
    {
        return runs.find(eq("player_id", playerID)).filter(eq("game_id", gameID)).first();
    }

    public List<RunDocument> findRunsByCategoryAndPlayer(String category, UUID playerID)
    {
        return runs.find(eq("player_id", playerID)).filter(eq("category", category)).into(new ArrayList<>());
    }

    public List<RunDocument> findRunsByFilter(Bson filter) {
        return runs.find(filter).into(new ArrayList<>());
    };

    public List<RunDocument> getTopNFastestRuns(int n)
    {
        return runs.find().sort(Sorts.ascending("time")).limit(n).into(new ArrayList<>());
    }

    public boolean deleteGame(UUID gameID)
    {
        return games.deleteOne(eq("_id", gameID)).wasAcknowledged();
    }

    public boolean deletePlayer(UUID playerID)
    {
        return players.deleteOne(eq("_id", playerID)).wasAcknowledged();
    }

    public boolean deleteRun(UUID runID)
    {
        return runs.deleteOne(eq("_id", runID)).wasAcknowledged();
    }

    public boolean deleteRunsByPlayer(UUID playerID)
    {
        return runs.deleteMany(eq("player_id", playerID)).wasAcknowledged();
    }

    public boolean deleteRunsByGame(UUID gameID)
    {
        return runs.deleteMany(eq("game_id", gameID)).wasAcknowledged();
    }

}
