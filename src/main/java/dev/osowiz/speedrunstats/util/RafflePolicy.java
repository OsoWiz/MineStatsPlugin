package dev.osowiz.speedrunstats.util;

public enum RafflePolicy {
    /**
     * Todo possible raffle enum policies.
     * These could be used to determine the specifics of how teams are selected.
     */
    RANDOM, // random
    MINIMIZE_RANK_DISPARITY, // minimize the difference in average rank between teams
    PLAYER_CHOICE // starts a conversation with the players to choose their teams
}
