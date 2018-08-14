package aviee.develop.music.ultimatespotifyplayer.constant;

import java.util.Random;

public class Constants {

    public static int randomNumber(int low, int high) {
        Random random = new Random();
        int number = random.nextInt(high - low) + low;
        return number;
    }

    public static String secondsToString(int pTime) {
        final int min = pTime / 60;
        final int sec = pTime - (min * 60);

        final String strMin = placeZeroIfNeeded(min);
        final String strSec = placeZeroIfNeeded(sec);
        return String.format("%s:%s", strMin, strSec);
    }

    private static String placeZeroIfNeeded(int number) {
        return (number >= 10) ? Integer.toString(number) : String.format("0%s", Integer.toString(number));
    }
}
