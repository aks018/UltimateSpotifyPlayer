package aviee.develop.music.ultimatespotifyplayer.constant;

import java.util.Random;

public class Constants {

    public static int randomNumber(int low, int high) {
        Random random = new Random();
        int number = random.nextInt(high - low) + low;
        return number;
    }
}
