package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class SimplePlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    static final String ACTION_PLAY = "spotifystreamer.krzyzek.confkit.net.spotifystreamer.SimplePlayerService.action.PLAY";
    private static final String TAG = SimplePlayerService.class.getSimpleName();
    MediaPlayer mMediaPlayer = null;

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "Error: " + what + " extra: " + extra);
        return false;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand Service no");

        if (intent.getAction().equals(ACTION_PLAY)) {
            Log.d(TAG, "onStartCommand Service");
            //  mMediaPlayer = new MediaPlayer();
            String url = "https://p.scdn.co/mp3-preview/0333accb974b80b31f36bf6a09eb0c4c9f2a3f26";
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.prepareAsync();

        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when MediaPlayer is ready
     */
    public void onPrepared(MediaPlayer player) {
        Log.d(TAG, "onPreapredMediaPLayer");
        player.start();
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
    }
}