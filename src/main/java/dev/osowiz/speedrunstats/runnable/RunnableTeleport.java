package dev.osowiz.speedrunstats.runnable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RunnableTeleport extends BukkitRunnable {

    private final Player runner;
    private final Player target;

    public RunnableTeleport(Player runner, Player target) {
        this.runner = runner;
        this.target = target;
    }


    @Override
    public void run() {
        if (target.isOnline() && !target.isDead()) {
            Location partnerLocation = target.getLocation();
            runner.teleport(partnerLocation);
            runner.sendMessage("You have been teleported to your team partner.");
        } else {
            runner.sendMessage("Your team partner is not online or is dead. Unluigi moment.");
        }
    }
}
