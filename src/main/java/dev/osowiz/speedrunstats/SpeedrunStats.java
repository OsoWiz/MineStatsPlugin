package dev.osowiz.speedrunstats;

import com.mongodb.client.model.Filters;
import dev.osowiz.speedrunstats.commands.*;
import dev.osowiz.speedrunstats.documents.GameDocument;
import dev.osowiz.speedrunstats.documents.PlayerDocument;
import dev.osowiz.speedrunstats.documents.RunDocument;
import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.games.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.*;
// mongodb
// bukkit
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
// java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * SpeedrunStats plugin main class.
 * This class is mainly concerned with database accessess and game setup.
 */
public final class SpeedrunStats extends JavaPlugin {

    // private variables
    private FileConfiguration config;
    private SpeedrunDB database;
    public static long rankWindow = 150L * 24L * 3600L * 1000L; // 150 days (in milliseconds)

    private Game game;
    public boolean hasStarted = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("SpeedrunStats plugin enabled, loading configuration..");
        // Always check for config.yml file first and override the default values
        File conf = new File(getDataFolder(), "config.yml");
        try {
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
        // load a file called credentials and read the connection string from there

        String connectionString = readFromFile(".credentials");
        if(connectionString == null)
        {
            getLogger().info("Database connection string not found, exiting..");
            return;
        }

        try {
            this.database = new SpeedrunDB(connectionString);
        } catch (Exception e) {
            getLogger().info("Error connecting to the database");
            e.printStackTrace();
            return;
        }
        getLogger().info("Connected to the database, currently there are " + database.getPlayers().countDocuments() + " players and " + database.getGames().countDocuments() + " games played in the database");

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
        this.getCommand(StartCommand.name).setExecutor(new StartCommand(this));
        this.getCommand(PointsCommand.name).setExecutor(new PointsCommand(this, game));
        this.getCommand(StatsCommand.name).setExecutor(new StatsCommand(this));
        this.getCommand(ConfigureCommand.name).setExecutor(new ConfigureCommand(this, game));
        this.getCommand(RankCommand.name).setExecutor(new RankCommand(this, game));
        this.getCommand(ListRanksCommand.name).setExecutor(new ListRanksCommand(this, game));
        this.getCommand(StatLeaderCommand.name).setExecutor(new StatLeaderCommand(this, game));

        this.saveConfig();
    }

    private String readFromFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName))).trim();
        } catch (IOException e) {
            getLogger().severe("Error reading connection string from file: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("SpeedrunStats plugin disabled, closing database connection..");
        this.database.close();
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
        UUID uid = player.getUniqueId();
        if(this.game.containsPlayer(player)) // prevent player from joining the game twice
        {
            getLogger().info("Player " + player.getName() + " is already in the game");
            return;
        }

        String operatorUID = config.getString("operator_uid");
        if(uid.toString().equals(operatorUID))
        {
            player.setOp(true);
        }
        // find the player document from the database
        PlayerDocument playerDoc = database.findPlayer(uid);
        if(playerDoc == null)
        {
            getLogger().info("Player not found in the database, creating a new document..");
            playerDoc = new PlayerDocument(uid, player.getName(), 0, 0, 0, 0, 0, Double.POSITIVE_INFINITY);
            database.insertPlayer(playerDoc);
        }

        getServer().broadcastMessage("Player " + player.getName()
                + " with a rank " + playerDoc.getRank() + " and pb of " + Helpers.timeToString(playerDoc.getFastestTimeInSeconds()) + " has joined the game!");
        player.setDisplayName(playerDoc.getRank().getColor() + player.getName() + ChatColor.RESET);
        player.setPlayerListName(playerDoc.getRank().getColor() + player.getName() + ChatColor.RESET);
        SpeedRunner runner = new SpeedRunner(player, playerDoc.getAllKills(), playerDoc.getAllDeaths(),
                playerDoc.getHighestScore(), playerDoc.getRank(), playerDoc.getFastestTimeInSeconds());
        this.game.addRunner(runner);
    }

    public void uploadGameData(GameDocument gameData){
        database.insertGame(gameData);
    }

    public void uploadFinishedGameData() {
        GameDocument gameData = new GameDocument();
        ArrayList<RunDocument> runData = new ArrayList<RunDocument>();
        List<String> winnerNames = new ArrayList<>();
        double fastestTime = Double.POSITIVE_INFINITY;
        int winnerTeamID = -1;
        float averageRank = game.getAverageRank();

        for (SpeedRunner runner : game.getRunners()) // one gamedoc per runner
        {
            RunDocument runDoc = new RunDocument();
            runDoc.setPlayerID(runner.spigotPlayer.getUniqueId());
            runDoc.setGameID(gameData.getGameID());
            runDoc.setPlayerName(runner.getName());
            runDoc.setCategory(game.getCategory());
            runDoc.setTeamID(runner.teamID);
            runDoc.setTime(runner.time);
            runDoc.setScore(game.calculateFinalScore(runner, averageRank));
            runDoc.setKills(runner.stats.getKills());
            runDoc.setDeaths(runner.stats.getDeaths());
            runDoc.setDate(game.getGameDate());
            runData.add(runDoc);
            if(runDoc.isWinner())
            {
                winnerNames.add(runner.getName());
                fastestTime = Math.min(fastestTime, runDoc.getTime()); // should be the same but just in case
                winnerTeamID = runner.teamID;
            }
        }
        // write game data
        gameData.setNumRunners(game.getRunners().size());
        gameData.setNumTeams(game.getTeamCount());
        gameData.setCategory(game.getCategory());
        gameData.setAvgRank(averageRank);
        gameData.setAvgScore(game.getRunners().stream().map(runner -> runner.stats.getPoints()).reduce(0, Integer::sum) / gameData.getNumRunners());
        gameData.setHighestScore(game.getRunners().stream().map(runner -> runner.stats.getPoints()).max(Integer::compare).orElse(0));
        gameData.setCompletionTime(fastestTime);
        gameData.setWinnerNames(winnerNames);
        gameData.setDate(game.getGameDate());
        gameData.setWinnerTeamID(winnerTeamID);

        uploadRunData(runData);
        uploadGameData(gameData);
        for(SpeedRunner runner : game.getRunners())
        { // playerdocuments are mostly just calculated facts from runs and games and therefore are updated later.
            Rank newRank = calculateRank(runner.spigotPlayer.getUniqueId(), game.getCategory(), game.getGameDate(), database);
            database.updatePlayerStats(runner.spigotPlayer.getUniqueId(), newRank);
        }
    }

    /**
     * Static method for calculating the rank of a player in a particular category by linearly weighting their games.
     * @param uid of the player
     * @param category of games
     * @param dateOfCalculation the date of the calculation
     * @param database to search from
     * @return the rank of the player
     */
    public static Rank calculateRank(UUID uid, String category, Date dateOfCalculation, SpeedrunDB database)
    {
        // games are considered for the past 150 days, weighted by time
        Date tresholdDate = new Date(dateOfCalculation.getTime() - rankWindow);
        List<RunDocument> runs = getConsideredRunsForPlayerWithDate(uid, category, dateOfCalculation, database);
        return calculateRankFromRuns(runs, dateOfCalculation, tresholdDate);
    }

    public Rank calculateRank(UUID uid)
    {
        return calculateRank(uid, game.getCategory(), game.getGameDate(), database);
    }

    /**
     * Static method for calculating the rank of a player in a particular category by linearly weighting their games. Does NOT check that the given games are valid and within the time frame.
     * @param runs
     * @param dateOfCalculation
     * @param tresholdDate
     * @return
     */
    public static Rank calculateRankFromRuns(List<RunDocument> runs, Date dateOfCalculation, Date tresholdDate)
    {
        if(runs.size() < 2)
        { // player needs at least 2 runs to be ranked
            return Rank.UNRANKED;
        }
        int normalizedScore = getWeightedScore(runs, dateOfCalculation, tresholdDate);
        return Rank.calculateRank(normalizedScore);
    }

    public static int getWeightedScore(List<RunDocument> runs, Date dateOfCalculation, Date tresholdDate)
    {
        float totalScore = 0.f;
        float weightSum = 0.f;
        for(RunDocument doc : runs)
        {
            float weight = 1.f -  ( (float) dateOfCalculation.getTime() - doc.getDate().getTime()) / ( (float) dateOfCalculation.getTime() - tresholdDate.getTime());
            totalScore += ((float) doc.getScore()) * weight;
            weightSum += weight;
        }
        if(weightSum < 1.f)
        {
            weightSum = 1.f; // no boost to old scores
        }

        return  (int) (totalScore / weightSum);
    }

    public int getWeightedScore(List<RunDocument> runs)
    {
        return getWeightedScore(runs, game.getGameDate(), new Date(game.getGameDate().getTime() - rankWindow));
    }

    /**
     * Returns the runs of a player in a category that are considered for ranking calculations.
     * @param uid
     * @return
     */
    public List<RunDocument> getConsideredRunsForPlayer(UUID uid)
    {
        return getConsideredRunsForPlayerWithDate(uid, game.getCategory(), game.getGameDate(), database);
    }

    public static List<RunDocument> getConsideredRunsForPlayerWithDate(UUID uid, String category, Date searchDate, SpeedrunDB database)
    {
        Date tresholdDate = new Date(searchDate.getTime() - rankWindow);
        return database.findRunsByFilter(Filters.and(
                Filters.eq("player_id", uid),
                Filters.eq("category", category),
                Filters.gte("date", tresholdDate),
                Filters.lte("date", searchDate)));
    }

    public SpeedrunDB getDatabase() {
        return this.database;
    }

    /**
     * Schedules a task to be run after a delay.
     * @param task to run
     * @param delayInSeconds delay in seconds
     */
    public void scheduleTask(Runnable task, float delayInSeconds)
    {
        long delayInTicks = (long) (delayInSeconds * getServer().getServerTickManager().getTickRate());
        getServer().getScheduler().scheduleSyncDelayedTask(this, task, delayInTicks);
    }

    public int scheduleRecurrentTask(Runnable task, float delayInSeconds, float periodInSeconds)
    {
        long delayInTicks = (long) (delayInSeconds * getServer().getServerTickManager().getTickRate());
        long periodInTicks = (long) (periodInSeconds * getServer().getServerTickManager().getTickRate());
        return getServer().getScheduler().scheduleSyncRepeatingTask(this, task, delayInTicks, periodInTicks);
    }

    public void cancelTask(int taskId)
    {
        getServer().getScheduler().cancelTask(taskId);
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

    public void uploadRunData(ArrayList<RunDocument> runData) {
        this.database.insertRuns(runData);
    }
}
