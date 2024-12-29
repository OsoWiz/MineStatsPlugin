package dev.osowiz.speedrunstats.listeners;

import dev.osowiz.speedrunstats.SpeedrunStats;
import dev.osowiz.speedrunstats.gametypes.StandardSpeedrun;
import dev.osowiz.speedrunstats.util.Helpers;
import dev.osowiz.speedrunstats.util.SpeedRunner;
import dev.osowiz.speedrunstats.util.SpeedrunTeam;
import dev.osowiz.speedrunstats.util.StandardSpeedrunScoring;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
            plugin.getLogger().warning("Runner " + player.getName() + " not found in game.");
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
            if (team != null) {
                // get partner with least deaths
                Optional<SpeedRunner> bestPartner = team.getRunners().stream().filter(r -> r.uid != runner.uid).min(Comparator.comparingInt(SpeedRunner::getDeathsThisGame));
                if (bestPartner.isPresent() && bestPartner.get().spigotPlayer.isOnline()) {
                    SpeedRunner partner = bestPartner.get();
                    int deathsNow = partner.getDeathsThisGame();
                    Double currentCooldown = coolDowns.getOrDefault(runner.uid, STARTING_TP_COOLDOWN);
                    Timer timer = new Timer();
                    player.sendMessage("Teleporting to your team partner in " + currentCooldown + " seconds if they stay alive.");
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (partner.spigotPlayer.isOnline() && !partner.spigotPlayer.isDead()
                                    && deathsNow == partner.getDeathsThisGame()) {
                                Location partnerLocation = partner.spigotPlayer.getLocation();
                                Bukkit.getScheduler().runTask(plugin, () -> player.teleport(partnerLocation));
                                player.sendMessage("You have been teleported to your team partner.");
                                coolDowns.compute(runner.uid, (k, v) -> {
                                    if(v != null) return v + 10.d;
                                    return currentCooldown;
                                } );
                            }
                        }
                    },  currentCooldown.longValue() * 1000 ); // 30 seconds
                }
            } else {
                plugin.getLogger().warning("Team " + runner.teamID + " not found in game despite this game being a team game.");
            }
        }

    }




}
