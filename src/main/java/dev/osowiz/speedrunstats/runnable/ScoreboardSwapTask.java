package dev.osowiz.speedrunstats.runnable;


import dev.osowiz.speedrunstats.util.SpeedrunScoreBoardManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ScoreboardSwapTask extends BukkitRunnable {

    private final List<String> boards;
    int currentIndex = 0;
    private final SpeedrunScoreBoardManager manager;
    public ScoreboardSwapTask(List<String> scoreBoardNames, SpeedrunScoreBoardManager manager)
    {
        this.boards = scoreBoardNames;
        this.manager = manager;
    }

    @Override
    public void run() {
        if(boards.isEmpty())
        {
            return;
        }
        currentIndex = (currentIndex + 1) % boards.size();
        String currentBoard = boards.get(currentIndex);
        // set the current board to be displayed
        manager.setBoardToAllPlayers(currentBoard);
    }

}
