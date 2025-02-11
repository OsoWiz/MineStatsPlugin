package dev.osowiz.speedrunstats.commands;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.listeners.ConfigurationAbandonListener;
import dev.osowiz.speedrunstats.prompts.StrategyPrompt;
import dev.osowiz.speedrunstats.games.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class ConfigureCommand extends SpeedrunCommandBase {

    public static final String name = "config";
    private Game game;
    private final ConversationFactory factory;
    public ConfigureCommand(SpeedrunStats plugin, Game game) {
        super(plugin);
        this.game = game;
        this.factory = new ConversationFactory(plugin)
                .withFirstPrompt(new StrategyPrompt(game))
                .withEscapeSequence("cancel")
                .withTimeout(60)
                .thatExcludesNonPlayersWithMessage("You must be a player to configure the game")
                .addConversationAbandonedListener(new ConfigurationAbandonListener());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if( !(sender instanceof Player) ) // if sender is not a player
        {
            return false;
        }
        Player player = (Player) sender;
        // start conversation with the player. Ask whether they want teams based on size or count
        // if size, ask for team size
        // if count, ask for team count
        Conversation convo = factory.buildConversation(player);
        convo.begin();
        return true;
    }


    private static Prompt getConfigPrompt() {
        // FixedSetPrompt strategyPrompt = new FixedSetPrompt("", "");
        return Prompt.END_OF_CONVERSATION; // todo make
    }
}
