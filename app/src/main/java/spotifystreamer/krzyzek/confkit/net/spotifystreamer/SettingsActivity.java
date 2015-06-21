package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by £ukasz on 2015-06-20.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}