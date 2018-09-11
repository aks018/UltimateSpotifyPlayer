package aviee.develop.music.myultimatemusicexperienceforspotify;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.mortbay.jetty.Main;

import static aviee.develop.music.myultimatemusicexperienceforspotify.MainActivity.REDIRECT_URI;
import static aviee.develop.music.myultimatemusicexperienceforspotify.MainActivity.REQUEST_CODE;
import static aviee.develop.music.myultimatemusicexperienceforspotify.MainActivity.authenticate;
import static aviee.develop.music.myultimatemusicexperienceforspotify.MainActivity.mPlayer;

public class MyPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);

            Preference myPref = (Preference) findPreference("signOut");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    mPlayer.logout();
                    authenticate = false;
                    return true;
                }
            });
        }

    }

}
