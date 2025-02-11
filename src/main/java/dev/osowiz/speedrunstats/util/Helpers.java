package dev.osowiz.speedrunstats.util;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bukkit.ChatColor;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class Helpers {

     public static final float walkSpeed = 0.2f;
     public static final float sneakSpeed = 0.1f;
     public static final float flySpeed = 0.1f;
     private static final double nanoFactor = 1e-9;

    public static String timeToString(double timeInSeconds){

        if(timeInSeconds < 0.d) {
            return "no time set";
        }
        if(1e6 <= timeInSeconds) {
            return "∞";
        }

        double remaining = timeInSeconds;
        int hours = (int) remaining / 3600;
        remaining -= hours * 3600;
        int minutes = (int) remaining / 60;
        remaining -= minutes * 60;
        DecimalFormat df = new DecimalFormat("0.00");
        return hours + "h " + minutes + "m " + df.format(remaining) + "s";
    }

    public static boolean equalDoubles(double a, double b) {
        return Math.abs(a - b) < 1e-6;
    }

    public static double nanoToSeconds(long nanoseconds) {
        return nanoseconds * nanoFactor;
    }


    public static void sendResultsToPlayers(List<SpeedrunTeam> teams) {
        teams.sort(Comparator.comparingInt(SpeedrunTeam::getCurrentObjectiveID).reversed());
        int previousBest = Integer.MAX_VALUE;
        int i = 1;
        for(SpeedrunTeam team : teams)
        {
            for(SpeedRunner runner : team.getRunners())
            {
                String subTitle = "Your time was: " + Helpers.timeToString(runner.time);
                if(1e6 - 10.f < runner.time)
                    subTitle = "Better luck next time!";

                runner.spigotPlayer.sendTitle(ChatColor.BOLD + "You finished in " + Place.getPlace(i).formattedToString() + " place!", subTitle, 10, 200, 20);
            }
            i++;
        }
    }

    public static void tellPlayersTheirTeam(List<SpeedrunTeam> teams) {
        for(SpeedrunTeam team : teams)
        {
            for(SpeedRunner runner : team.getRunners())
            {
                runner.spigotPlayer.sendTitle(ChatColor.BOLD + "Your team is: ", team.getTeamAsString(), 10, 120, 20);
            }
        }
    }

    public static Binary uuidToBinary(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return new Binary(BsonBinarySubType.UUID_STANDARD, bb.array());
    }

    public static UUID binaryToUUID(Binary binary) {
        ByteBuffer bb = ByteBuffer.wrap(binary.getData());
        return new UUID(bb.getLong(), bb.getLong());
    }

}
