package dev.osowiz.speedrunstats.util;

import org.bukkit.Sound;

import java.util.Random;

public class Soundbank {

    public static Sound[] soundList = {};
    private static Random random = new Random();


    public static Sound getRandomGoatHorn()
    {
        Sound[] horns = { Sound.ITEM_GOAT_HORN_SOUND_0, Sound.ITEM_GOAT_HORN_SOUND_1, Sound.ITEM_GOAT_HORN_SOUND_2, Sound.ITEM_GOAT_HORN_SOUND_3, Sound.ITEM_GOAT_HORN_SOUND_4, Sound.ITEM_GOAT_HORN_SOUND_5};
        return horns[random.nextInt(horns.length)];
    }

}
