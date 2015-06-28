package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.SongLocal;

public class SimplePlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private static final String TAG = SimplePlayerService.class.getSimpleName();
    public static String ON_COMPLETION_BROADCAST = "spotifystreamer.krzyzek.confkit.net.spotifystreamer.on_completion";
    public static String ON_PREPARED_BROADCAST = "spotifystreamer.krzyzek.confkit.net.spotifystreamer.on_prepared";
    private final IBinder mBinder = new LocalBinder();
    MediaPlayer mMediaPlayer = null;
    private boolean isInitialized = false;
    private boolean isStopDuringInitilizing = false;
    private ArrayList<SongLocal> mSongsList;
    private int mCurrentSong = -1;
    private ArtistLocal mIdArtist = new ArtistLocal("", "", "");

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "Error: " + what + " extra: " + extra);
        return false;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand Service no");

        /*if (intent.getAction().equals(ACTION_PLAY)) {
            Log.d(TAG, "onStartCommand Service");
            String url = "https://p.scdn.co/mp3-preview/0333accb974b80b31f36bf6a09eb0c4c9f2a3f26";
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.prepareAsync();
            }
        }*/
        //   setupHandler();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            ArtistLocal artistLocal = bundle.getParcelable(MainActivityFragment.EXTRA_ARTIST_DETAILS);

            if (!(mIdArtist.getId().equals(artistLocal.getId()))) {
                mCurrentSong = bundle.getInt(ArtistDetailedActivity.EXTRA_SONG_CLICKED);
                mSongsList = bundle.getParcelableArrayList(ArtistDetailedActivity.EXTRA_SONG_DETAILS);
                mIdArtist = artistLocal;
            }
        }
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /**
     * Called when MediaPlayer is ready
     */
    public void onPrepared(MediaPlayer player) {
        isInitialized = true;
        mMediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy Service");

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        sendOnCompletionBroadcast();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public void sendOnCompletionBroadcast() {
        Intent intent = new Intent(ON_COMPLETION_BROADCAST);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void nextMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        isInitialized = false;
        if (++mCurrentSong >= mSongsList.size()) {
            mCurrentSong = 0;
        }
    }

    public void stopMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.reset();
            isInitialized = false;
            //     isStopDuringInitilizing = true;
        }
    }

    public void prevMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        isInitialized = false;
        if (--mCurrentSong < 0) {
            mCurrentSong = mSongsList.size() - 1;
        }
    }

    public void playMedia() {
        if (isInitialized) {
            mMediaPlayer.start();
        } else {
            mMediaPlayer.reset();
            //  String url = "https://p.scdn.co/mp3-preview/0333accb974b80b31f36bf6a09eb0c4c9f2a3f26";
            SongLocal songLocal = mSongsList.get(mCurrentSong);
            String url = songLocal.getmUrl();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.prepareAsync();
            }
        }
    }

    public SongLocal getCurrentSong() {
        return mSongsList.get(mCurrentSong);
    }

    public ArtistLocal getCurrentArtist() {
        return mIdArtist;
    }

    public int getSongPosition() {
        if (isInitialized) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getSongDuration() {
        if (isInitialized) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public boolean isCompleted() {
        return ((mMediaPlayer.getDuration() == mMediaPlayer.getCurrentPosition()) && isInitialized);
    }

    public void seekTo(int progress) {
        if (isInitialized || mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(progress);
        }
    }

    public class LocalBinder extends Binder {
        SimplePlayerService getService() {
            return SimplePlayerService.this;
        }
    }
}