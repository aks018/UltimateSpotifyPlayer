package aviee.develop.music.ultimatespotifyplayer;

import com.example.avi.ultimatespotifyplayer.constant.Constants;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void randomNumberGenerator() {
        assertTrue(Constants.randomNumber(0, 100) > 0 && Constants.randomNumber(0,100) <= 100);
    }
}