package dev.osowiz.speedrunstats;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import dev.osowiz.speedrunstats.commands.PointsCommand;
import dev.osowiz.speedrunstats.commands.StartCommand;
import dev.osowiz.speedrunstats.gametypes.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.*;
import org.bson.Document;
// mongodb
// bukkit
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
// java
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.Date;
import java.util.List;


/**
 * SpeedrunStats plugin main class.
 * This class is mainly concerned with database accessess and game setup.
 */
public final class SpeedrunStats extends JavaPlugin {

    // private variables
    private FileConfiguration config;
    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> players;
    private MongoCollection<Document> games;

    private Game game;

    public Boolean hasStarted = false;
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("SpeedrunStats plugin enabled, loading configuration..");
        // Always check for config.yml file first and override the default values
        File conf = new File(getDataFolder(), "config.yml");
        try{
            this.config.load(conf);
            if(SpeedrunConfig.isValid(this.config))
            {
                getLogger().info("Config file loaded successfully");
            }
            else
            {
                getLogger().info("Loaded config file is invalid, using default values");
                this.config = this.getConfig();
            }
        }
        catch ( Exception e)
        {
            getLogger().info("Error loading config.yml file, using default values");
            this.config = this.getConfig();
        }

        // connect to the database
        String connectionString = config.getString("db_connectionstring");
        if(connectionString == null)
        {
            getLogger().info("Database connection string not found, exiting..");
            return;
        }

        try {
            MongoClient newClient = MongoClients.create(connectionString);

            this.client = newClient;
            this.database = client.getDatabase("speedrundb");
            this.players = database.getCollection("players");
            this.games = database.getCollection("games");
        } catch (Exception e) {
            getLogger().info("Error connecting to the database");
            e.printStackTrace();
        }

        // check whether world folder exists and delete it
        if(this.config.getBoolean("delete_worlds_on_startup")) {
            Path worldPath = Paths.get("world");
            Path netherPath = Paths.get("world_nether");
            Path endPath = Paths.get("world_the_end");
            deleteWorldFolder(worldPath);
            deleteWorldFolder(netherPath);
            deleteWorldFolder(endPath);
        }

        // set up the game
        switch(this.config.getString("gamemode"))
        {
            case "standard":
                game = new StandardSpeedrun(this, new SpeedrunConfig(this.config));
                break;
            default:
                getLogger().info("Gamemode not found, using standard speedrun");
                game = new StandardSpeedrun(this, new SpeedrunConfig(this.config));
                break;
        }

        // set up commands
        this.getCommand("start").setExecutor(new StartCommand(this));
        this.getCommand("points").setExecutor(new PointsCommand(this, game));

        // test
        this.saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("SpeedrunStats plugin disabled, closing database connection..");
        this.client.close();
    }

    public void startGame(String[] args) {
        if(hasStarted)
        {
            getLogger().info("Game has already started");
            return;
        }
        if(args.length == 1) {
            try{
                int teamSize = Integer.parseInt(args[0]);
                this.config.set("standard_team_size", teamSize);
                game.setConfig(new SpeedrunConfig(this.config));
            } catch (NumberFormatException e) {
                getLogger().info("Invalid team size, using default value");

            }
        }

        hasStarted = true;
        this.game.startGame();
    }

    public void addPlayerToGame(Player player)
    {
        String uid = player.getUniqueId().toString();
        String operatorUID = config.getString("operator_uid");
        if(uid.equals(operatorUID))
        {
            player.setOp(true);
        }
        // find the player document from the database
        Document playerDoc = findByID(uid);
        if(playerDoc == null)
        {
            getLogger().info("Player not found in the database, creating a new document..");
            playerDoc = new Document();
            playerDoc.append("uid", uid);
            playerDoc.append("name", player.getName());
            playerDoc.append("kills", 0);
            playerDoc.append("deaths", 0);
            playerDoc.append("games", 0);
            players.insertOne(playerDoc);
        }

        FindIterable docIter = this.games.find(
                Filters.and(
                Filters.eq("uid", uid),
                        Filters.eq("gamemode", this.config.getString("gamemode"))
                ));
        int playerAllKills = playerDoc.getInteger("kills");
        int playerAllDeaths = playerDoc.getInteger("deaths");
        // calculate stats like kills, deaths, etc.
        int allKills = 0;
        int allDeaths = 0;
        int avgScore = 0;
        int bestScore = 0;
        double bestTime = 1e6;
        int numGames = 0;
        for(Object doc : docIter)
        {
            Document gameDoc = (Document) doc;
            allKills += gameDoc.getInteger("kills");
            allDeaths += gameDoc.getInteger("deaths");
            int score = gameDoc.getInteger("score");
            double time = gameDoc.getDouble("time");
            if(time < bestTime)
            {
                bestTime = time;
            }
            if(bestScore < score)
            {
                bestScore = score;
            }
            numGames++;
        }

        if(0 < numGames)
        {
            avgScore /= numGames;
        }

        int rank = calculateRank(uid, this.game.getCategory());
        getServer().broadcastMessage("Player " + player.getName()
                + " with a rank " + rank + " and pb of " + Helpers.timeToString(bestTime) + " has joined the game!");

        SpeedRunner runner = new SpeedRunner(player, allKills, allDeaths, bestScore, rank, bestTime);
        this.game.addRunner(runner);
    }

    public void uploadGameData(List<Document> gameData){
        this.games.insertMany(gameData);
    }

    public void uploadPlayerData(List<Document> playerData){
        playerData.forEach(doc -> {
            String uid = doc.getString("uid"); // match by uid
            players.replaceOne(Filters.eq("uid", uid), doc, new com.mongodb.client.model.ReplaceOptions().upsert(true));
        });
    }

    public int calculateRank(String uid, String category)
    {
        // games are considered for the past three months, weighted by time
        Date now = new Date();
        Date threeMonthsAgo = new Date(now.getTime() - 3 * 30 * 24 * 60 * 60 * 1000);

        FindIterable<Document> docIter = this.games.find(
                Filters.and(
                        Filters.eq("uid", uid),
                        Filters.eq("category", category),
                        Filters.gte("date", threeMonthsAgo)
                ));
        int totalScore = 0;
        float weightSum = 0.f;
        for(Document doc : docIter)
        {
            int score = doc.getInteger("score");
            long time = doc.getLong("time");
            float weight = 1.f - (float) (now.getTime() - doc.getDate("date").getTime()) / (now.getTime() - threeMonthsAgo.getTime());
            totalScore += score * weight;
            weightSum += weight;
        }
        if(weightSum < 1e-6)
        {
            return 0;
        }
        int normalizedScore = (int) (totalScore / weightSum);
        if(category == "standard")
            return StandardSpeedrunScoring.calculateRank(normalizedScore);
        else
            return 0;
    }

    private Document findByName(String name) {
        try {
            return players.find(new Document("name", name)).first();
        } catch (Exception e) {
            getLogger().info("Error finding player by ID");
        }
        return null;
    }

    private Document findByID(String uid) {
        try {
            return players.find(new Document("uid", uid)).first();
        } catch (Exception e) {
            getLogger().info("Error finding player by ID");
        }
        return null;
    }

    private void deleteWorldFolder(Path worldPath) {
        if (Files.exists(worldPath)) {
            try {
                // delete the whole directory
                Files.walk(worldPath)
                        .map(Path::toFile)
                        .forEach(File::delete);
                // print a message to the console
                getLogger().info("Deleted world folder");
            } catch (java.io.IOException e) {
                getLogger().info("Error deleting a world folder");
                e.printStackTrace();
            }
        } else {
            // print a message to the console
            getLogger().info("World folder does not exist..");
        }
    }

}
