package dev.osowiz.speedrunstats.prompts;

import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

public class TeamJoinPrompt extends ValidatingPrompt {

    private final Game game;
    private final String question;
    int numTeams;
    public TeamJoinPrompt(Game game, int numTeams)
    {
        super();
        this.game = game;
        this.question = "Join a team by entering a number between "
                + ChatColor.BOLD + " 0 " + ChatColor.RESET + " and " + ChatColor.BOLD +  (numTeams - 1) + ChatColor.RESET
                + ". Exit by sending any other character.";
        this.numTeams = numTeams;
    }

    @Override
    public Prompt acceptValidatedInput(ConversationContext context, String input)
    {
        try {
            int choice = Integer.parseInt(input);
            if(-1 < choice && choice < numTeams && context.getForWhom() instanceof Player player)
            {
                game.broadcastMessage("Player " + ((Player) context.getForWhom()).getDisplayName() + " has joined a team " + input + "!");
                player.sendMessage("You have joined team " + choice + "!");
                // add player to team
                SpeedRunner runner = game.getRunnerByID(player.getUniqueId());
                if(runner != null)
                {
                    game.teamBuilder.setPlayerChoice(runner, choice);
                }
            }
        } catch (NumberFormatException e)
        {
            return END_OF_CONVERSATION;
        }
        // send end of file
        return END_OF_CONVERSATION;
    }

    @Override
    public boolean isInputValid(ConversationContext context, String input)
    {
        return true;
    }

    @Override
    public String getPromptText(ConversationContext context)
    {
        return question;
    }

}
