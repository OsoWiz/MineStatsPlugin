package dev.osowiz.speedrunstats.prompts;

import dev.osowiz.speedrunstats.games.StandardSpeedrun;
import dev.osowiz.speedrunstats.runnable.RunnableTeleport;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import dev.osowiz.speedrunstats.util.SpeedrunTeam;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class TeleportPrompt extends NumericPrompt {

    private final List<SpeedRunner> restOfTeam;
    private final Double cooldown;
    private final StandardSpeedrun game; // this seems excessive. Is there a better way to schedule tasks?
    public TeleportPrompt(StandardSpeedrun game, Player player, SpeedrunTeam team, Double cooldown) {
        restOfTeam = team.getRunners().stream().filter(r -> !r.spigotPlayer.getUniqueId().equals(player.getUniqueId())).toList();
        this.cooldown = cooldown;
        this.game = game;

    }

    @Nullable
    @Override
    protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull Number number) {

        if(conversationContext.getForWhom() instanceof Player player && number instanceof Integer)
        {
            if(number.intValue() > SpeedrunTeam.NONE && number.intValue() < restOfTeam.size()) {
                SpeedRunner target = restOfTeam.get(number.intValue());
                Runnable runnable = new RunnableTeleport(player, target.spigotPlayer);
                game.scheduleTask(runnable, cooldown.floatValue());
                return END_OF_CONVERSATION;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext conversationContext) {
        if(conversationContext.getForWhom() instanceof Player player)
        {
            return "Who would you like to teleport to? " + ChatColor.BOLD + "Answer with a number. " + ChatColor.RESET + " Options are: "
                    + IntStream.range(0, restOfTeam.size()).mapToObj( i -> restOfTeam.get(i).getName() + " : " + i).reduce("", (a, acc) -> acc + ", " + a, String::concat);
        }
        return "";
    }
}
