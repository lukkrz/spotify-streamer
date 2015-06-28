package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.SimplePlayerActivityFragment.OnFragmentInteractionListener;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.SongLocal;


public class SimplePlayerActivity extends ActionBarActivity implements OnFragmentInteractionListener {
    private static String TAG = SimplePlayerActivity.class.getName();
    SimplePlayerService mService;
    boolean mBound = false;
    Intent mIntent;
    int mCurrentSong;
    ArrayList<SongLocal> mArrayListSong;
    ArtistLocal mArtistLocal;
    Handler musicMethodsHandler;
    SimplePlayerActivityFragment mSmplePlayerActivityFragment;
    OnCompletionBroadcastReceiver mOnCompletionBroadcastReceiver;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
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
        setContentView(R.layout.activity_simple_player);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        mCurrentSong = bundle.getInt(ArtistDetailedActivity.EXTRA_SONG_CLICKED);
        mArrayListSong = bundle.getParcelableArrayList(ArtistDetailedActivity.EXTRA_SONG_DETAILS);
        mArtistLocal = bundle.getParcelable(MainActivityFragment.EXTRA_ARTIST_DETAILS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        mIntent = new Intent(this, SimplePlayerService.class);
        mIntent.putParcelableArrayListExtra(ArtistDetailedActivity.EXTRA_SONG_DETAILS, mArrayListSong);
        mIntent.putExtra(MainActivityFragment.EXTRA_ARTIST_DETAILS, mArtistLocal);
        mIntent.putExtra(ArtistDetailedActivity.EXTRA_SONG_CLICKED, mCurrentSong);

        bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
        startService(mIntent);

        mSmplePlayerActivityFragment = (SimplePlayerActivityFragment)
                getFragmentManager().findFragmentById(R.id.fragment_simple_player);

        mOnCompletionBroadcastReceiver = new OnCompletionBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnCompletionBroadcastReceiver, new IntentFilter(SimplePlayerService.ON_COMPLETION_BROADCAST));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnCompletionBroadcastReceiver);
        super.onPause();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopService(mIntent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple_player, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void updateSongView() {
        /*SimplePlayerActivityFragment simplePlayerActivityFragment = (SimplePlayerActivityFragment)
                getFragmentManager().findFragmentById(R.id.fragment_simple_player);*/
        SongLocal songLocal = mService.getCurrentSong();
        ArtistLocal artistLocal = mService.getCurrentArtist();
        mSmplePlayerActivityFragment.updateUI(artistLocal, songLocal);
        /*if (mService.isCompleted())
            mSmplePlayerActivityFragment.stopPlay();
*/
    }

    private void updateSeekBarPosition() {
        mSmplePlayerActivityFragment = (SimplePlayerActivityFragment)
                getFragmentManager().findFragmentById(R.id.fragment_simple_player);

        musicMethodsHandler = new Handler();
        Runnable musicRun = new Runnable() {

            @Override
            public void run() {
                if (mBound == true) {
                    int musicMaxTime = mService.getSongDuration();
                    int musicCurTime = mService.getSongPosition();

                    Log.d(TAG, "current: " + musicCurTime + " max: " + musicMaxTime);
                    mSmplePlayerActivityFragment.updateSeekBarPostion(musicMaxTime, musicCurTime);
                }
                musicMethodsHandler.postDelayed(this, 500);
            }
        };
        musicMethodsHandler.postDelayed(musicRun, 500);



/*

        String counter = intent.getStringExtra("counter");
        String mediamax = intent.getStringExtra("mediamax");

        int seekProgress = Integer.parseInt(counter);
        int seekMax = Integer.parseInt(mediamax);

        simplePlayerActivityFragment.updateSeekBarPostion(seekMax, seekProgress);
*/

    }

    private class OnCompletionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSmplePlayerActivityFragment.stopPlay();
        }
    }

}


