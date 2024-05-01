package dev.osowiz.speedrunstats.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.loot.LootTable;

public class SpeedrunConfig {

    FileConfiguration fileconfig;

    public final int standardTeamSize;
    public final String gameMode;
    public final boolean preventMovement;
    public final double catchupCooldown;
    public final LootTable lootTable;
    public SpeedrunConfig(FileConfiguration fileconfig) {
        this.fileconfig = fileconfig;
        standardTeamSize = fileconfig.getInt("standard_team_size");
        gameMode = fileconfig.getString("gamemode");
        preventMovement = fileconfig.getBoolean("prevent_movement_at_start");
        catchupCooldown = fileconfig.getDouble("catchup_cooldown");
        String lootTableString = fileconfig.getString("loot_table");
        switch (lootTableString) {
            default:
                lootTable = new StandardPiglinLootTable();
        }

    }

    public static boolean isValid(FileConfiguration fileconfig) {
        return fileconfig.contains("standard_team_size")
                && fileconfig.contains("gamemode")
                && fileconfig.contains("prevent_movement_at_start")
                && fileconfig.contains("db_connectionstring")
                && fileconfig.contains("delete_worlds_on_startup");
    }

}
