package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.SongLocal;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SimplePlayerActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SimplePlayerActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimplePlayerActivityFragment extends DialogFragment {
    private static final String TAG = SimplePlayerActivityFragment.class.getSimpleName();

    private static String MUSING_PLAYING_KEY = "music_key";
    private static String SEEKBAR_LAST_POSITION = "seekbar_pos_key";
    private static String SEEKBAR_MAX_POSITION = "seekbar_max_key";

    private OnFragmentInteractionListener mListener;
    private SeekBar.OnSeekBarChangeListener mSeekBarListener;
    private ImageView mCenterButton, mLeftButton, mRightButton;
    private boolean mIsMusicPlaying;

    private TextView mArtistName;
    private TextView mSongName;
    private TextView mAlbumName;
    private TextView mProgress;

    private ImageView mSongImage;
    private SeekBar mSeekBar;

    public SimplePlayerActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public void updateUI(ArtistLocal artistLocal, SongLocal songLocal) {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_player_activity);
            actionBar.setSubtitle(songLocal.getmName());
        }

        mSongName.setText(songLocal.getmName());
        mArtistName.setText(artistLocal.getmName());
        mAlbumName.setText(songLocal.getmAlbum());

        Picasso.with(getActivity()).load(songLocal.getmImageBig()).into(mSongImage);
    }

    public void updateSeekBarPostion(int seekMax, int seekProgress) {
        mSeekBar.setMax(seekMax);
        mSeekBar.setProgress(seekProgress);
        mProgress.setText(getDurationBreakdown(seekProgress) + " / " + getDurationBreakdown(seekMax));
    }

    public String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);

        sb.append(minutes);
        sb.append(":");
        sb.append(seconds);

        return (sb.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mIsMusicPlaying = savedInstanceState.getBoolean(MUSING_PLAYING_KEY);
        } else {
            mIsMusicPlaying = false;
        }

    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putBoolean(MUSING_PLAYING_KEY, mIsMusicPlaying);
        savedState.putInt(SEEKBAR_LAST_POSITION, mSeekBar.getProgress());
        savedState.putInt(SEEKBAR_MAX_POSITION, mSeekBar.getMax());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_simple_player_activity, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();

        // TODO: add playing!!
//        bundle.setClassLoader(SongLocal.class.getClassLoader());
        // SongLocal songLocal = bundle.getParcelable(ArtistDetailedActivity.EXTRA_SONG_DETAILS);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_player_activity);
            //   actionBar.setSubtitle(songLocal.getmName());
        }

        mArtistName = (TextView) rootView.findViewById(R.id.artist_name);
        mAlbumName = (TextView) rootView.findViewById(R.id.album_name);
        mSongName = (TextView) rootView.findViewById(R.id.song_name);
        mSongImage = (ImageView) rootView.findViewById(R.id.song_image);

        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);

        if (savedInstanceState != null) {
            mSeekBar.setMax(savedInstanceState.getInt(SEEKBAR_MAX_POSITION));
            mSeekBar.setProgress(savedInstanceState.getInt(SEEKBAR_LAST_POSITION));
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mListener.onSeekBarClick(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mProgress = (TextView) rootView.findViewById(R.id.progress_text);

        mLeftButton = (ImageView) rootView.findViewById(R.id.button_left);
        mRightButton = (ImageView) rootView.findViewById(R.id.button_right);

        mCenterButton = (ImageView) rootView.findViewById(R.id.button_center);

        if (mIsMusicPlaying) {
            mCenterButton.setImageResource(android.R.drawable.ic_media_pause);
        }

        mCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCLick Button Center");
                buttonStopStartPlay();
            }
        });

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(ButtonType.PREV);
                stopPlay();
            }
        });
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClick(ButtonType.NEXT);
                stopPlay();
            }
        });
        return rootView;
    }

    private void buttonStopStartPlay() {
        if (mIsMusicPlaying) {
            stopPlay();
            mListener.onButtonClick(ButtonType.STOP);
        } else {
            mIsMusicPlaying = true;
            mCenterButton.setImageResource(android.R.drawable.ic_media_pause);
            mListener.onButtonClick(ButtonType.PLAY);
        }
    }

    public void stopPlay() {
        mIsMusicPlaying = false;
        mCenterButton.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public enum ButtonType {PLAY, STOP, NEXT, PREV}

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onButtonClick(ButtonType button);
        public void onSeekBarClick(int progress);

        public void onDismiss();
    }


}
