package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;


public class ArtistDetailedActivity extends ActionBarActivity implements ArtistDetailedActivityFragment.OnSongsListSelectedListener {
    static String EXTRA_SONG_DETAILS = "songs_list";
    static String EXTRA_SONG_CLICKED = "song_current";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detailed);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnSongsListSelected(ArtistLocal artistLocal, ArrayList songList, int position) {
        MainActivityFragment artistList = (MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.artist_fragment);

        if (artistList == null) {
            Intent intent = new Intent(this, SimplePlayerActivity.class);
            intent.putParcelableArrayListExtra(ArtistDetailedActivity.EXTRA_SONG_DETAILS, songList);
            intent.putExtra(MainActivityFragment.EXTRA_ARTIST_DETAILS, artistLocal);
            intent.putExtra(ArtistDetailedActivity.EXTRA_SONG_CLICKED, position);
            startActivity(intent);
        } else {
            //  artistDetailed.updateContent(position);
            //  artistDetailed
        }
    }
}
