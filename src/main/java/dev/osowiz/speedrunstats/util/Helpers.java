package dev.osowiz.speedrunstats.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public final class Helpers {

     public static final float walkSpeed = 0.2f;
     public static final float sneakSpeed = 0.1f;
     public static final float flySpeed = 0.1f;
     private static final double nanoFactor = 1e-9;

    public static String timeToString(double timeInSeconds){

        if(timeInSeconds < 0.d) {
            return "no time set";
        }
        if(1e6 <= timeInSeconds) {
            return "∞";
        }

        double remaining = timeInSeconds;
        int hours = (int) remaining / 3600;
        remaining -= hours * 3600;
        int minutes = (int) remaining / 60;
        remaining -= minutes * 60;
        DecimalFormat df = new DecimalFormat("0.00");
        return hours + "h " + minutes + "m " + df.format(remaining) + "s";
    }

    public static double nanoToSeconds(long nanoseconds) {
        return nanoseconds * nanoFactor;
    }

    /**
     * Returns random teams of given size.
     * @param runners
     * @param teamSize
     * @param policy
     * @return
     */
    public static ArrayList<SpeedrunTeam> raffleTeamsBySize(ArrayList<SpeedRunner> runners, int teamSize, RafflePolicy policy) {
        if(runners.size() < teamSize || teamSize <= 1) {
            teamSize = 1;
        }
        int overFlow = runners.size() % teamSize;
        int teamCount = runners.size() / teamSize + (0 < overFlow ? 1 : 0);
        int nRunners = runners.size();
        ArrayList<SpeedrunTeam> teams = new ArrayList<SpeedrunTeam>(teamCount);
        for(int i = 0; i < teamCount; i++) {
            teams.add(new SpeedrunTeam(new ArrayList<SpeedRunner>())); // initialize teams
        }
        System.out.println("size of team array: " + teams.size() + " teamSize: " + teamSize + " runners: " + nRunners);
        switch(policy) {
            case RANDOM:
            {
                Collections.shuffle(runners);
                int teamIndex = 0;
                for(SpeedRunner runner : runners) {
                    teams.get(teamIndex).addRunner(runner);
                    teamIndex++;
                    teamIndex %= teamCount;
                }
                break;
            }
            case MINIMIZE_RANK_DISPARITY: // currently pretty bad at minimizing rank disparity
            {
                Collections.sort(runners, (a, b) -> a.rank - b.rank);
                int teamIndex = 0;
                for (SpeedRunner runner : runners) {
                    teams.get(teamIndex).addRunner(runner);
                    teamIndex++;
                    teamIndex %= teamCount;
                }
                break;
            }
            default:
            { // use the order in which the runners joined the server.
                int teamIndex = 0;
                for(SpeedRunner runner : runners) {
                    teams.get(teamIndex).addRunner(runner);
                    teamIndex++;
                    teamIndex %= teamCount;
                }
                break;
            }
        }

        return teams;
    }

    public static ArrayList<SpeedrunTeam> raffleTeamsByCount(ArrayList<SpeedRunner> runners, int teamCount, RafflePolicy policy) {
        if(teamCount <= 1) {
            teamCount = 1;
        }
        int overFlow = runners.size() % teamCount;
        int teamSize = runners.size() / teamCount + ((0 < overFlow) ? 1 : 0);
        return raffleTeamsBySize(runners, teamSize, policy);
    }

    public static ArrayList<SpeedrunTeam> raffleTeamsBySize(ArrayList<SpeedRunner> runners, int teamSize){
        return raffleTeamsBySize(runners, teamSize, RafflePolicy.RANDOM);
    }

}
