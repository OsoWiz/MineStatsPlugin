package dev.osowiz.speedrunstats.prompts;

import dev.osowiz.speedrunstats.util.Game;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

public class SizeOrCountPrompt extends NumericPrompt {

    private Game game;
    private String question;
    public SizeOrCountPrompt(Game game, String sizeOrCountQuestion)
    {
        super();
        this.game = game;
        this.question = sizeOrCountQuestion;
    }
    @Override
    public Prompt acceptValidatedInput(ConversationContext context, Number input)
    {
        game.teamBuilder.setSizeOrCount(input.intValue());
        // send end of file
        return new PolicyPrompt(game);
    }

    @Override
    public String getPromptText(ConversationContext context)
    {
        return question;
    }
}
