package dev.osowiz.speedrunstats.documents;

import dev.osowiz.speedrunstats.util.Rank;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

public class PlayerDocument {

    @BsonId
    @BsonProperty("_id")
    private UUID id;
    @BsonProperty("name")
    private String name;
    @BsonProperty("rank_code")
    private Integer rankCode;
    @BsonProperty("kills")
    private Integer allKills;
    @BsonProperty("deaths")
    private Integer allDeaths;
    @BsonProperty("games_played")
    private Integer gamesPlayed;
    @BsonProperty("highest_score")
    private Integer highestScore;
    @BsonProperty("fastest_time")
    private Double fastestTimeInSeconds;

    public PlayerDocument(UUID uid, String name, int rankCode, int allKills, int allDeaths, int gamesPlayed, int highestScore, double fastestTimeInSeconds) {
        this.id = uid;
        this.name = name;
        this.rankCode = rankCode;
        this.allKills = allKills;
        this.allDeaths = allDeaths;
        this.gamesPlayed = gamesPlayed;
        this.highestScore = highestScore;
        this.fastestTimeInSeconds = fastestTimeInSeconds;
    }

    public PlayerDocument() {
        // default constructor for mongo
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRankCode() {
        return rankCode;
    }

    public Rank getRank() {
        return Rank.fromCode(rankCode);
    }

    public void setRankCode(Integer rankCode) {
        this.rankCode = rankCode;
    }

    public Integer getAllKills() {
        return allKills;
    }

    public void setAllKills(Integer allKills) {
        this.allKills = allKills;
    }

    public Integer getAllDeaths() {
        return allDeaths;
    }

    public void setAllDeaths(Integer allDeaths) {
        this.allDeaths = allDeaths;
    }

    public Integer getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Double getFastestTimeInSeconds() {
        return fastestTimeInSeconds;
    }

    public void setFastestTimeInSeconds(Double fastestTimeInSeconds) {
        this.fastestTimeInSeconds = fastestTimeInSeconds;
    }

}
