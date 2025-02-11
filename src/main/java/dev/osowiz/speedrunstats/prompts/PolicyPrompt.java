package dev.osowiz.speedrunstats.prompts;

import dev.osowiz.speedrunstats.games.Game;
import dev.osowiz.speedrunstats.util.RafflePolicy;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

import java.util.Arrays;

public class PolicyPrompt extends FixedSetPrompt {

    private Game game;
    public PolicyPrompt(Game game) {
        super(Arrays.stream(RafflePolicy.values()).map(RafflePolicy::toString).toArray(String[]::new));
        this.game = game;
    }

    @Override
    public Prompt acceptValidatedInput(ConversationContext context, String input) {
        switch (input){
            case "RANDOM":
                game.teamBuilder.setPolicy(RafflePolicy.RANDOM);
                break;
            case "MINIMIZE_RANK_DISPARITY":
                game.teamBuilder.setPolicy(RafflePolicy.MINIMIZE_RANK_DISPARITY);
                break;
            case "PLAYER_CHOICE":
                game.teamBuilder.setPolicy(RafflePolicy.PLAYER_CHOICE);
                game.askPlayerChoices();
                break;
            default:
                return END_OF_CONVERSATION;
        }
        // send end of file
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return "What policy would you like to use? Options are: " + fixedSet.stream().reduce((a, b) -> a + ", " + b).orElse("");
    }
}
