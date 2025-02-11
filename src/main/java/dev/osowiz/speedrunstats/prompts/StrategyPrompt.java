package dev.osowiz.speedrunstats.prompts;

import dev.osowiz.speedrunstats.util.FormationStrategy;
import dev.osowiz.speedrunstats.games.Game;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;

import java.util.Arrays;

public class StrategyPrompt extends FixedSetPrompt {

    private Game game;

    public StrategyPrompt(Game game) {
        super(Arrays.stream(FormationStrategy.values()).map(FormationStrategy::toString).toArray(String[]::new));
        this.game = game;
    }

    @Override
    public String getPromptText(ConversationContext ctx) {
        return "How do you want to form Teams? Options are: " + fixedSet.stream().reduce((a, b) -> a + ", " + b).orElse("none") + ".";
    }

    @Override
    public Prompt acceptValidatedInput(ConversationContext context, String input) {
        switch (input) {
            case "SIZE":
                game.teamBuilder.setStrategy(FormationStrategy.SIZE);
                return new SizeOrCountPrompt(game, "How many players per team? Answer with a number.");
            case "COUNT":
                game.teamBuilder.setStrategy(FormationStrategy.COUNT);
                return new SizeOrCountPrompt(game, "How many teams? Answer with a number.");
            case "LOOSE":
                game.teamBuilder.setStrategy(FormationStrategy.LOOSE);
                return new PolicyPrompt(game);
            default:
                return END_OF_CONVERSATION;
        }
    }
}
