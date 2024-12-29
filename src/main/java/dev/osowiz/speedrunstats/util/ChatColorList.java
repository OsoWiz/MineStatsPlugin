package dev.osowiz.speedrunstats.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatColorList {

    public static final ChatColor[] colors = {
            ChatColor.WHITE,
            ChatColor.BLACK,
            ChatColor.RED,
            ChatColor.GREEN,
            ChatColor.BLUE,
            ChatColor.YELLOW,
            ChatColor.AQUA,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GRAY,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_PURPLE,
            ChatColor.DARK_RED,
            ChatColor.GOLD,
            ChatColor.GRAY,
            ChatColor.LIGHT_PURPLE
    };

    /**
     * Returns color based on index.
     * @param index
     * @return
     */
    public static ChatColor getColor(int index) {
        if(index < 0 || colors.length <= index)
            return ChatColor.RESET;

        return colors[index];
    }

    public static List<ChatColor> getColors() {
        return Arrays.asList(colors);
    }

    public static List<ChatColor> getShuffledColors() {
        ArrayList<ChatColor> colors = new ArrayList<>(getColors());
        Collections.shuffle(colors);
        return colors;
    }

    public static int getColorIndex(ChatColor color) {
        for(int i = 0; i < colors.length; i++) {
            if(colors[i] == color) {
                return i;
            }
        }
        return -1;
    }

    public static int maxColorIndex() {
        return colors.length - 1;
    }

    public static int numColors() {
        return colors.length;
    }

}
