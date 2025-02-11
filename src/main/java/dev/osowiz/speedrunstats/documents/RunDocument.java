package dev.osowiz.speedrunstats.documents;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a run document, eg a speedrun of a player.
 */
public class RunDocument {

    @BsonId
    @BsonProperty("_id")
    private UUID id;

    @BsonProperty("player_id")
    UUID playerID;
    @BsonProperty("game_id")
    UUID gameID;
    @BsonProperty("player_name")
    String playerName;
    @BsonProperty("player_rank")
    Integer playerRank;
    @BsonProperty("category")
    String category;
    @BsonProperty("team_id")
    Integer teamID;
    @BsonProperty("time")
    Double time;
    @BsonProperty("kills")
    Integer kills;
    @BsonProperty("deaths")
    Integer deaths;
    @BsonProperty("score")
    Integer score;
    @BsonProperty("date")
    Date date;

    public RunDocument(UUID playerID, UUID gameID, String playerName, String category, int teamID, double time, int kills, int deaths, int score, Date date) {
        this.id = UUID.randomUUID();
        this.playerID = playerID;
        this.gameID = gameID;
        this.playerName = playerName;
        this.category = category;
        this.teamID = teamID;
        this.time = time;
        this.kills = kills;
        this.deaths = deaths;
        this.score = score;
        this.date = date;
    }

    public RunDocument() {
        // default constructor for mongo
        this.id = UUID.randomUUID();
    }

    public boolean isWinner()
    {
        return !time.isInfinite();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public void setPlayerID(UUID playerID) {
        this.playerID = playerID;
    }

    public UUID getGameID() {
        return gameID;
    }

    public void setGameID(UUID gameID) {
        this.gameID = gameID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public void setTeamID(Integer teamID) {
        this.teamID = teamID;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Integer getPlayerRank() {
        return playerRank;
    }

    public void setPlayerRank(Integer playerRank) {
        this.playerRank = playerRank;
    }

    public Integer getKills() {
        return kills;
    }

    public void setKills(Integer kills) {
        this.kills = kills;
    }

    public Integer getDeaths() {
        return deaths;
    }

    public void setDeaths(Integer deaths) {
        this.deaths = deaths;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
