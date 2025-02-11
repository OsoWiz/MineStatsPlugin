package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.games.StandardSpeedrun;
import dev.osowiz.speedrunstats.prompts.TeleportPrompt;
import dev.osowiz.speedrunstats.util.Helpers;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import dev.osowiz.speedrunstats.util.SpeedrunTeam;
import dev.osowiz.speedrunstats.util.StandardSpeedrunScoring;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class StandardCatchupListener extends SpeedrunListenerBase {

    StandardSpeedrun game;
    public static final String name = "StandardCatchupListener";
    private static final double STARTING_TP_COOLDOWN = 20.d;

    private Map<UUID, Double> coolDowns = new HashMap<UUID, Double>();

    public StandardCatchupListener(SpeedrunStats plugin, StandardSpeedrun game) {
        super(plugin, name);
        this.game = game;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if(Helpers.nanoToSeconds(System.nanoTime() - game.getStartTime() ) < game.getConfig().catchupCooldown)
            return; // if the game hasn't been going for 5 minutes, don't give the player items.

        Player player = event.getPlayer();
        SpeedRunner runner = game.getRunnerByID(player.getUniqueId());
        if(runner == null)
        {
            plugin.getLogger().warning("Runner " + player.getName() + " not found in game on respawn listener.");
            return;
        }

        if(game.getCooldownTime() < Helpers.nanoToSeconds(System.nanoTime() - runner.lastCooldown))
        {
            for(ItemStack stack : StandardSpeedrunScoring.catchupStack)
            {
                player.getInventory().addItem(stack);
            }
            runner.setCooldown();
            coolDowns.putIfAbsent(runner.uid, STARTING_TP_COOLDOWN); // starting cooldown
        } else {
            player.sendMessage("You still have " + Helpers.nanoToSeconds(System.nanoTime() - runner.lastCooldown) + " seconds of cooldown." );
        }

        // Check if it's a team game and start the teleport timer
        if (game.isTeamGame() && runner.teamID != -1) {
            SpeedrunTeam team = game.getTeamByID(runner.teamID);
            if (team != null && team.size() > 1) {
                Double currentCooldown = coolDowns.getOrDefault(runner.uid, STARTING_TP_COOLDOWN);
                // start a new conversation with the player
                Conversation conversation = new ConversationFactory(plugin)
                        .withFirstPrompt(new TeleportPrompt(this.game, player, team, currentCooldown))
                        .withEscapeSequence("cancel")
                        .withTimeout(currentCooldown.intValue())
                        .buildConversation(player);
                conversation.begin();
                coolDowns.put(runner.uid, currentCooldown + 10); // increase cooldown by 10 seconds
            }
        }

    }




}
