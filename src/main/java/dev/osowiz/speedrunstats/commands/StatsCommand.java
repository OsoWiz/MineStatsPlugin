package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.enums.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends SpeedrunCommandBase {

    public static final String name = "stats";
    public StatsCommand(SpeedrunStats plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(! (sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        float tickRate = player.getServer().getServerTickManager().getTickRate();
        sender.sendMessage("You have dealt: " + Statistic.DAMAGE_DEALT.getFormattedValue(player));
        sender.sendMessage("You have jumped a total of: " + Statistic.TIMES_JUMPED.getFormattedValue(player));
        sender.sendMessage( "You have walked: " + Statistic.WALK_DISTANCE.getFormattedValue(player));
        sender.sendMessage("You have tanked : " + Statistic.DAMAGE_TAKEN.getFormattedValue(player));
        sender.sendMessage("You have sneaked for: " +Statistic.SNEAK_TIME.getFormattedValue(player));
        return true;
    }

}
