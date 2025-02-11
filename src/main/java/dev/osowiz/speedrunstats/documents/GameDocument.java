package dev.osowiz.speedrunstats.documents;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Represents a game that players / runners took part in.
 */
public class GameDocument {

    @BsonId
    @BsonProperty("_id")
    private UUID gameID;
    @BsonProperty("category")
    private String category;
    @BsonProperty("num_runners")
    private Integer numRunners;
    @BsonProperty("num_teams")
    private Integer numTeams;
    @BsonProperty("avg_rank")
    private Float avgRank;
    @BsonProperty("avg_score")
    private Integer avgScore;
    @BsonProperty("winner_team_id")
    private Integer winnerTeamID;
    @BsonProperty("highest_score")
    private Integer highestScore;
    @BsonProperty("completion_time")
    private Double completionTime;
    @BsonProperty("winner_names")
    private List<String> winnerNames;
    @BsonProperty("date")
    private Date date;


    public GameDocument(UUID gameID, String category, int numRunners, int numTeams, float avgRank, int avgScore, int winnerTeamID, int highestScore, double completionTime, List<String> winnerNames, Date date)
    {
        this.gameID = gameID;
        this.category = category;
        this.numRunners = numRunners;
        this.numTeams = numTeams;
        this.avgRank = avgRank;
        this.avgScore = avgScore;
        this.winnerTeamID = winnerTeamID;
        this.highestScore = highestScore;
        this.completionTime = completionTime;
        this.winnerNames = winnerNames;
        this.date = date;
    }

    public GameDocument() {
        this.gameID = UUID.randomUUID();
        winnerNames = new ArrayList<>();
        // default constructor
    }

    public UUID getGameID() {
        return this.gameID;
    }

    public void setGameID(UUID uuid)
    {
        this.gameID = uuid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getNumRunners() {
        return numRunners;
    }

    public void setNumRunners(Integer numRunners) {
        this.numRunners = numRunners;
    }

    public Integer getNumTeams() {
        return numTeams;
    }

    public void setNumTeams(Integer numTeams) {
        this.numTeams = numTeams;
    }

    public Integer getWinnerTeamID() {
        return winnerTeamID;
    }

    public void setWinnerTeamID(Integer winnerTeamID) {
        this.winnerTeamID = winnerTeamID;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Double getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Double completionTime) {
        this.completionTime = completionTime;
    }

    public List<String> getWinnerNames() {
        return winnerNames;
    }

    public void setWinnerNames(List<String> winnerNames) {
        this.winnerNames = winnerNames;
    }

    public void addWinnerName(String winnerName) {
        this.winnerNames.add(winnerName);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getAvgRank() {
        return avgRank;
    }

    public void setAvgRank(Float avgRank) {
        this.avgRank = avgRank;
    }

    public Integer getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(Integer avgScore) {
        this.avgScore = avgScore;
    }

}
