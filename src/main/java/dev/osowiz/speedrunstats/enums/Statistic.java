package dev.osowiz.speedrunstats.enums;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Optional;

public enum Statistic {

    ANIMALS_BRED(org.bukkit.Statistic.ANIMALS_BRED, Unit.COUNT),
    NETHERRACK_MINED(org.bukkit.Statistic.MINE_BLOCK, Unit.COUNT, Material.NETHERRACK),
    COBBLESTONE_MINED(org.bukkit.Statistic.MINE_BLOCK, Unit.COUNT, Material.COBBLESTONE),
    TIMES_JUMPED(org.bukkit.Statistic.JUMP, Unit.COUNT),
    PLAYER_KILLS(org.bukkit.Statistic.PLAYER_KILLS, Unit.COUNT),
    CHESTS_OPENED(org.bukkit.Statistic.CHEST_OPENED, Unit.COUNT),
    CRAFTING_TABLE_INTERACTION(org.bukkit.Statistic.CRAFTING_TABLE_INTERACTION, Unit.COUNT),
    SNEAK_TIME(org.bukkit.Statistic.SNEAK_TIME, Unit.TICKS),
    WALK_DISTANCE(org.bukkit.Statistic.WALK_ONE_CM, Unit.CENTIMETER),
    SPRINT_DISTANCE(org.bukkit.Statistic.SPRINT_ONE_CM, Unit.CENTIMETER),
    SWIM_DISTANCE(org.bukkit.Statistic.SWIM_ONE_CM, Unit.CENTIMETER),
    FALL_DISTANCE(org.bukkit.Statistic.FALL_ONE_CM, Unit.CENTIMETER),
    FLY_DISTANCE(org.bukkit.Statistic.FLY_ONE_CM, Unit.CENTIMETER),
    DISTANCE_BY_BOAT(org.bukkit.Statistic.BOAT_ONE_CM, Unit.CENTIMETER),
    DISTANCE_BY_PIG(org.bukkit.Statistic.PIG_ONE_CM, Unit.CENTIMETER),
    DISTANCE_BY_HORSE(org.bukkit.Statistic.HORSE_ONE_CM, Unit.CENTIMETER),
    FISH_CAUGHT(org.bukkit.Statistic.FISH_CAUGHT, Unit.COUNT),
    TARGET_HIT(org.bukkit.Statistic.TARGET_HIT, Unit.COUNT),
    PLAY_TIME(org.bukkit.Statistic.PLAY_ONE_MINUTE, Unit.TICKS),
    DAMAGE_DEALT(org.bukkit.Statistic.DAMAGE_DEALT, Unit.HEARTS),
    DAMAGE_TAKEN(org.bukkit.Statistic.DAMAGE_TAKEN, Unit.HEARTS),
    DEATHS(org.bukkit.Statistic.DEATHS, Unit.COUNT),
    DAMAGE_BLOCKED_BY_SHIELD(org.bukkit.Statistic.DAMAGE_BLOCKED_BY_SHIELD, Unit.HEARTS),
    TRADED_WITH_VILLAGER(org.bukkit.Statistic.TRADED_WITH_VILLAGER, Unit.COUNT); // todo add more


    private final org.bukkit.Statistic statistic;
    private final Unit unit;
    private final Optional<EntityType> entityType;
    private final Optional<Material> material;
    // constructor for general statistics
    private Statistic(org.bukkit.Statistic stat, Unit unit)
    {
        this.statistic = stat;
        this.unit = unit;
        this.entityType = Optional.empty();
        this.material = Optional.empty();
    }
    // constructor for entity type statistics
    private Statistic(org.bukkit.Statistic stat, Unit unit, EntityType entityType)
    {
        this.statistic = stat;
        this.unit = unit;
        this.entityType = Optional.of(entityType);
        this.material = Optional.empty();
    }
    // constructor for material statistics
    private Statistic(org.bukkit.Statistic stat, Unit unit, Material material)
    {
        this.statistic = stat;
        this.unit = unit;
        this.entityType = Optional.empty();
        this.material = Optional.of(material);
    }

    // returns the String value of the statistic for the player
    public String getFormattedValue(Player player)
    {
        return this.unit.valueToThisUnit(getValueForPlayer(player));
    }

    public int getValueForPlayer(Player player)
    {
        if(entityType.isPresent())
        {
            return player.getStatistic(statistic, entityType.get());
        }
        else if(material.isPresent())
        {
            return player.getStatistic(statistic, material.get());
        }
        else
        {
            return player.getStatistic(statistic);
        }
    }

    public String getName()
    {
        return this.name();
    }

    public Unit getUnit()
    {
        return unit;
    }
}
