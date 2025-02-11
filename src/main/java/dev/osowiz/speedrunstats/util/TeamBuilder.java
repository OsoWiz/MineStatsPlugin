package dev.osowiz.speedrunstats.util;

import org.bukkit.ChatColor;

import java.util.*;

public class TeamBuilder {

    private FormationStrategy strategy; // is the strategy used to determine how many teams are created
    private RafflePolicy policy; // policy used to choose the teams
    private int teamCountSize; // team count or team size depending on the strategy.
    private HashMap<SpeedRunner, Integer> playerChoices = new HashMap<SpeedRunner, Integer>();
    Random rand = new Random();

    /**
     * Default constructor. Teams of size two.
     */
    public TeamBuilder() {
        teamCountSize = 2;
        strategy = FormationStrategy.SIZE;
        policy = RafflePolicy.RANDOM;
    }

    public TeamBuilder(int teamCountSize) {
        teamCountSize = teamCountSize;
        policy = RafflePolicy.RANDOM;
        strategy = FormationStrategy.COUNT;
    }

    public TeamBuilder(int teamCountSize, RafflePolicy policy) {
        teamCountSize = teamCountSize;
        policy = policy;
        strategy = FormationStrategy.SIZE;
    }

    public TeamBuilder(int teamCountSize, RafflePolicy policy, FormationStrategy strategy) {
        teamCountSize = teamCountSize;
        policy = policy;
        strategy = strategy;
    }

    /**
     * Returns teams according to the strategy.
     * @param runners
     * @return
     */
    public List<SpeedrunTeam> build(List<SpeedRunner> runners)
    {
        List<SpeedrunTeam> teams = new ArrayList<>();
        return switch (policy) {
            case RANDOM -> shuffleRandomly(runners);
            case MINIMIZE_RANK_DISPARITY -> // currently pretty bad at minimizing rank disparity
                    minimizeRankDisparity(runners);
            case PLAYER_CHOICE -> shuffleByChoice(runners);
            default -> // use the order in which the runners joined the server.
                    groupByJoinOrder(runners);
        };
    }

    public void setPlayerChoice(SpeedRunner player, int teamID) {
        playerChoices.put(player, teamID);
    }

    public int getTeamCount(int numRunners) {
        switch(this.strategy) {
            case COUNT:
                return teamCountSize <= numRunners ? teamCountSize : numRunners; // there can't be more teams than runners
            case SIZE:
                int overFlow = numRunners % teamCountSize;
                return numRunners / teamCountSize + (0 < overFlow ? 1 : 0);
            case LOOSE:
                return ChatColorList.numColors();
            default:
                return numRunners;
        }
    }


    private List<SpeedrunTeam> getEmptyTeams(int numRunners) {
        int trueTeamCount = getTeamCount(numRunners);

        ArrayList<SpeedrunTeam> teams = new ArrayList<SpeedrunTeam>(trueTeamCount);
        // get randomized colors until team count
        List<ChatColor> colors = ChatColorList.getShuffledColors().subList(0, trueTeamCount);
        for(int i = 0; i < trueTeamCount; i++) {
            SpeedrunTeam team = new SpeedrunTeam();
            team.teamColor = colors.get(i);
            team.teamID = i;
            teams.add(team); // initialize teams
        }
        return teams;
    }

    private List<SpeedrunTeam> shuffleRandomly(List<SpeedRunner> runnersToShuffle) {
        List<List<SpeedRunner>> teamedPlayers = new ArrayList<>();
        switch (strategy)
        {
            case COUNT:
                teamedPlayers = Shuffle.randomForCount(runnersToShuffle, this.teamCountSize);
                break;
            case SIZE:
                teamedPlayers = Shuffle.randomForSize(runnersToShuffle, this.teamCountSize);
                break;
            case LOOSE:
                this.teamCountSize = getTeamCountOnLooseStrategy(runnersToShuffle.size());
                teamedPlayers = Shuffle.randomForCount(runnersToShuffle, this.teamCountSize);
                break;
            default:
                teamedPlayers = Shuffle.randomForCount(runnersToShuffle, runnersToShuffle.size());
        }
        return makeTeams(teamedPlayers);
    }

    // TODO fix
    private List<SpeedrunTeam> minimizeRankDisparity(List<SpeedRunner> runnersToShuffle) {
        runnersToShuffle.sort(Comparator.comparingInt(a -> a.rank.getCode())); // dummy implementation for now

        List<List<SpeedRunner>> teamedPlayers = new ArrayList<>();
        int numTeams = getTeamCount(runnersToShuffle.size());
        teamedPlayers = Shuffle.randomForCount(runnersToShuffle, numTeams);

        return makeTeams(teamedPlayers);
    }

    private List<SpeedrunTeam> groupByJoinOrder(List<SpeedRunner> runnersToShuffle) {
        return makeTeams(Shuffle.rollDivide(runnersToShuffle, getTeamCount(runnersToShuffle.size())));
    }

    private List<SpeedrunTeam> shuffleByChoice(List<SpeedRunner> runnersToShuffle)
    {
        int teamCount = getTeamCount(runnersToShuffle.size());
        List<List<SpeedRunner>> runnerInTeams = Shuffle.byChoice(runnersToShuffle, teamCount, playerChoices);
        return makeTeams(runnerInTeams);
    }

    private int getTeamCountOnLooseStrategy(int numRunners) {
        switch(policy)
        {
            case RANDOM:
                if(1 < numRunners) {
                    return 2 + rand.nextInt(numRunners - 1);
                }
            break;
            case MINIMIZE_RANK_DISPARITY: // todo
                break;
            case PLAYER_CHOICE:
                int numTeams = 1;
                for(Map.Entry<SpeedRunner, Integer> entry : playerChoices.entrySet()) {
                    int suggestedTeamCount = entry.getValue() + 1;
                    if(numTeams < suggestedTeamCount) {
                        numTeams = suggestedTeamCount;
                    }
                }
                return Math.min(numTeams, numRunners);
        }
        return numRunners;
    }

    private int getTeamIDForNoChoice(List<SpeedrunTeam> teams) {
        int teamID = rand.nextInt(teams.size());
        switch(strategy)
        {
            case SIZE: // in case of size, we choose randomly among any team that has space in it. broken. TODO fix
                List<SpeedrunTeam> teamsWithSpace = teams.stream().filter(team -> team.getRunners().size() < teamCountSize).toList();
                int spaceTeamIndex = rand.nextInt(teamsWithSpace.size());
                return teamsWithSpace.get(spaceTeamIndex).teamID;
            case COUNT: // in case of count, we choose the team with the least amount of players.
                int minPlayers = Integer.MAX_VALUE;
                for(SpeedrunTeam team : teams) {
                    if(team.getRunners().size() < minPlayers) {
                        minPlayers = team.getRunners().size();
                        teamID = team.teamID;
                    }
                }
                return teamID;
            default:
                return teamID;
        }
    }

    private void setTeamColors(List<SpeedrunTeam> teams) {
        List<ChatColor> colors = ChatColorList.getShuffledColors();
        for(int i = 0; i < teams.size(); i++) {
            teams.get(i).teamColor = colors.get(i);
        }
    }

    private List<SpeedrunTeam> makeTeams(List<List<SpeedRunner>> groupedPlayers)
    {
        List<SpeedrunTeam> teams = new ArrayList<>();
        List<ChatColor> colors = ChatColorList.getShuffledColors();
        for(int i = 0; i < groupedPlayers.size(); i++)
        {
            SpeedrunTeam team = new SpeedrunTeam();
            team.teamID = i;
            team.setRunners(groupedPlayers.get(i));
            team.teamColor = colors.get(i);
            teams.add(team);
        }
        return teams;
    }


    public FormationStrategy getStrategy() {
        return strategy;
    }

    public TeamBuilder setStrategy(FormationStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public RafflePolicy getPolicy() {
        return policy;
    }

    public TeamBuilder setPolicy(RafflePolicy policy) {
        this.policy = policy;
        return this;
    }

    public TeamBuilder setSizeOrCount(int sizeOrCount) {
        this.teamCountSize = sizeOrCount;
        return this;
    }

    /**
     * Sets the preferred team count and changes the strategy to COUNT.
     * @param teamCount
     * @return
     */
    public TeamBuilder setTeamCount(int teamCount) {
        this.teamCountSize = teamCount;
        this.strategy = FormationStrategy.COUNT;
        return this;
    }

    /**
     * Sets the preferred team size and changes the strategy to SIZE.
     * @param teamSize
     * @return
     */
    public TeamBuilder setTeamSize(int teamSize) {
        this.teamCountSize = teamSize;
        this.strategy = FormationStrategy.SIZE;
        return this;
    }

}
