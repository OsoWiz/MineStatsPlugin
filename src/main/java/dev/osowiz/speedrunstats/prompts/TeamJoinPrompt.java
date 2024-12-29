package dev.osowiz.speedrunstats.prompts;

import dev.osowiz.speedrunstats.util.Game;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

public class TeamJoinPrompt extends ValidatingPrompt {

    private Game game;
    private String question;
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
        game.broadcastMessage("Player " + ((Player) context.getForWhom()).getDisplayName() + " has joined a team " + input + "!");
        try {
            int choice = Integer.parseInt(input);
            if(-1 < choice && choice < numTeams && context.getForWhom() instanceof Player)
            {
                Player player = (Player) context.getForWhom();
                player.sendMessage("You have joined team " + choice + "!");
                // add player to team
                game.teamBuilder.setPlayerChoice(player.getUniqueId(), choice);
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
