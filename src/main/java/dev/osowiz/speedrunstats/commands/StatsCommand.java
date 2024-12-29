package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends SpeedrunCommandBase {

    public StatsCommand(SpeedrunStats plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(! (sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        sender.sendMessage("You have dealt: " + player.getStatistic(Statistic.DAMAGE_DEALT));
        sender.sendMessage("You have jumped a total of: " + player.getStatistic(Statistic.JUMP));
        sender.sendMessage( "You have walked: " + player.getStatistic(Statistic.WALK_ONE_CM) / 100 + " meters");
        return true;
    }

}
