package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.SongLocal;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.OnArtistListSelectedListener, ArtistDetailedActivityFragment.OnSongsListSelectedListener, SimplePlayerActivityFragment.OnFragmentInteractionListener {

    private static String TAG = "MainActivity";

    SimplePlayerService mService;
    boolean mBound = false;
    int mCurrentSong;
    ArrayList<SongLocal> mArrayListSong;
    ArtistLocal mArtistLocal;
    SimplePlayerActivityFragment mSimplePlayerActivityFragment;
    OnCompletionBroadcastReceiver mOnCompletionBroadcastReceiver;
    Intent mIntent;

    Handler musicMethodsHandler;


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SimplePlayerService.LocalBinder binder = (SimplePlayerService.LocalBinder) service;
            mService = binder.getService();

           /* if (mService.getmIdArtist() != mArtistLocal.getId()) {
                mService.setArtistLocal(mArtistLocal.getId());
                mService.setSongsList(mArrayListSong);
                mService.setCurrentSong(mCurrentSong);
                mService.changeArtist();
            } else if (mService.getmCurrentSong() != mCurrentSong) {
                mService.setCurrentSong(mCurrentSong);
            }*/
            mBound = true;
            updateSongView();
            updateSeekBarPosition();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnArtistListSelected(ArtistLocal artistLocal, int position) {
        ArtistDetailedActivityFragment artistDetailed = (ArtistDetailedActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_artist_detailed);

        if (artistDetailed == null) {
            Intent intent = new Intent(this, ArtistDetailedActivity.class);
            intent.putExtra(MainActivityFragment.EXTRA_ARTIST_DETAILS, artistLocal);
            startActivity(intent);
        } else {
            artistDetailed.displayTopSongs(artistLocal);
        }
    }

    @Override
    public void OnSongsListSelected(ArtistLocal artistLocal, ArrayList songList, int position) {
        android.app.FragmentManager fm = getFragmentManager();
        mArrayListSong = songList;
        mArtistLocal = artistLocal;
        mCurrentSong = position;

        mSimplePlayerActivityFragment = new SimplePlayerActivityFragment();
        mSimplePlayerActivityFragment.show(fm, "fragment_simple_player");

        // Bind to LocalService
        mIntent = new Intent(this, SimplePlayerService.class);
        mIntent.putParcelableArrayListExtra(ArtistDetailedActivity.EXTRA_SONG_DETAILS, mArrayListSong);
        mIntent.putExtra(MainActivityFragment.EXTRA_ARTIST_DETAILS, mArtistLocal);
        mIntent.putExtra(ArtistDetailedActivity.EXTRA_SONG_CLICKED, mCurrentSong);

        bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
        startService(mIntent);


        mOnCompletionBroadcastReceiver = new OnCompletionBroadcastReceiver();

    }


    private void updateSongView() {
        /*SimplePlayerActivityFragment simplePlayerActivityFragment = (SimplePlayerActivityFragment)
                getFragmentManager().findFragmentById(R.id.fragment_simple_player);*/
        SongLocal songLocal = mService.getCurrentSong();
        ArtistLocal artistLocal = mService.getCurrentArtist();
        mSimplePlayerActivityFragment.updateUI(artistLocal, songLocal);
        /*if (mService.isCompleted())
            mSimplePlayerActivityFragment.stopPlay();
*/
    }

    private void updateSeekBarPosition() {
        musicMethodsHandler = new Handler();
        Runnable musicRun = new Runnable() {

            @Override
            public void run() {
                if (mBound == true) {
                    int musicMaxTime = mService.getSongDuration();
                    int musicCurTime = mService.getSongPosition();

                    Log.d(TAG, "current: " + musicCurTime + " max: " + musicMaxTime);
                    if (musicMaxTime != 0)
                        mSimplePlayerActivityFragment.updateSeekBarPostion(musicMaxTime, musicCurTime);
                }
                musicMethodsHandler.postDelayed(this, 500);
            }
        };
        musicMethodsHandler.postDelayed(musicRun, 500);
    }

    @Override
    public void onButtonClick(SimplePlayerActivityFragment.ButtonType button) {
        if (mBound) {
            if (button == SimplePlayerActivityFragment.ButtonType.PLAY) {
                mService.playMedia();
            } else if (button == SimplePlayerActivityFragment.ButtonType.PREV) {
                mService.prevMedia();
                updateSongView();
            } else if (button == SimplePlayerActivityFragment.ButtonType.NEXT) {
                mService.nextMedia();
                updateSongView();
            } else {
                mService.stopMedia();
            }
            Toast.makeText(this, "ButtonClicked: " + button.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSeekBarClick(int progress) {
        if (mBound) {
            mService.seekTo(progress);
        }
    }

    @Override
    public void onDismiss() {
        if (mBound) {
            //  stopService(mIntent);
            mBound = false;
        }
        Log.d(TAG, "onDismiss");

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private class OnCompletionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSimplePlayerActivityFragment.stopPlay();
        }
    }
}
