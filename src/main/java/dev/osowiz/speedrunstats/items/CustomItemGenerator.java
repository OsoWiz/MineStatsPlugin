package dev.osowiz.speedrunstats.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.ArrayList;
import java.util.List;

// This class holds functions for creating variants of already existing in-game items such as compasses and swords that may have special abilities.
public class CustomItemGenerator {

    public static ItemStack getPlayerTrackingCompass(Player target)
    {
        ItemStack compass = new ItemStack(Material.COMPASS, 1);
        if( compass.getItemMeta() instanceof CompassMeta compassMeta)
        {
            compassMeta.setDisplayName("Dreamtracker");
            List<String> lore = new ArrayList<String>();
            lore.add("This compass points to the player " + target.getName());
            compassMeta.setLore(lore);
            compassMeta.setLodestone(target.getLocation());
            compassMeta.setLodestoneTracked(false);
        }
        return compass;
    }

}
