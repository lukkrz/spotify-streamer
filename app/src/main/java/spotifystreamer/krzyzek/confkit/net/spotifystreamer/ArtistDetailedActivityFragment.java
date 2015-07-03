package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.SongLocal;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistDetailedActivityFragment extends Fragment {
    private static String TAG = ArtistDetailedActivityFragment.class.getName();
    private static String SONGS_ARRAY_KEY = "songsArray";
    private static String COUNTRY_SPOTIFY_API_KEY = "country";

    private static int LARGE_PICTURE_DIMENS = 640;
    private static int SMALL_PICTURE_DIMENS = 200;

    OnSongsListSelectedListener mOnSongsListSelectedListener;

    private SpotifySongsAdapter mSongsAdapter;
    private ArrayList<SongLocal> mSongsList;
    private Toast mToastText;
    private SpotifyApi mSpotifyApi;
    private ArtistLocal mArtistLocal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpotifyApi = new SpotifyApi();

        mToastText = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        if (savedInstanceState != null) {
            mSongsList = savedInstanceState.getParcelableArrayList(SONGS_ARRAY_KEY);
            mSongsAdapter = new SpotifySongsAdapter(
                    getActivity(),
                    mSongsList
            );
        } else {
            mSongsAdapter = new SpotifySongsAdapter(
                    getActivity(),
                    new ArrayList<SongLocal>()
            );
        }
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelableArrayList(SONGS_ARRAY_KEY, mSongsList);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnSongsListSelectedListener = (OnSongsListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSongsListSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artist_detailed, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();

        if (bundle != null)
            mArtistLocal = bundle.getParcelable(MainActivityFragment.EXTRA_ARTIST_DETAILS);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_artist_detailed);
            //actionBar.setSubtitle(mArtistLocal.getmName());
        }

        ListView listView = (ListView) rootView.findViewById(R.id.artist_detailed_list_view);
        listView.setAdapter(mSongsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // SongLocal songLocal = (SongLocal) parent.getAdapter().getItem(position);
               /* Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                toast.cancel();
                toast = Toast.makeText(getActivity(), "Clicking on: " + songLocal.getmName(), Toast.LENGTH_SHORT);
                toast.show();*/
                /*Intent intent = new Intent(getActivity(), SimplePlayerActivity.class);
                intent.putParcelableArrayListExtra(ArtistDetailedActivity.EXTRA_SONG_DETAILS, mSongsList);
                intent.putExtra(MainActivityFragment.EXTRA_ARTIST_DETAILS, mArtistLocal);
                intent.putExtra(ArtistDetailedActivity.EXTRA_SONG_CLICKED, position);
                startActivity(intent);*/
                mOnSongsListSelectedListener.OnSongsListSelected(mArtistLocal, mSongsList, position);
            }
        });

        TextView emptyText = (TextView) rootView.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        if (savedInstanceState == null && mArtistLocal != null) {
            displayTopSongs(mArtistLocal);
        }

        return rootView;
    }


    public void displayTopSongs(ArtistLocal artistLocal) {
        mSongsAdapter.clear();

        mArtistLocal = artistLocal;

        String artistID = artistLocal.getId();

        SpotifyService spotify = mSpotifyApi.getService();

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        String countryPref = sharedPrefs.getString(getString(R.string.pref_country_info_key), Locale.getDefault().getCountry());

        Map<String, Object> map = new HashMap<>();
        map.put(COUNTRY_SPOTIFY_API_KEY, countryPref);

        spotify.getArtistTopTrack(artistID, map, new Callback<Tracks>() {

            @Override
            public void success(final Tracks tracks, Response response) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<kaaes.spotify.webapi.android.models.Track> trackList = (ArrayList) tracks.tracks;
                        if (trackList.isEmpty()) {
                            displayText(getResources().getText(R.string.empty_list).toString());
                        } else {
                            mSongsList = getSongLocalArray(trackList);
                            mSongsAdapter.addAll(mSongsList);
                        }
                    }
                };
                getActivity().runOnUiThread(runnable);
            }

            @Override
            public void failure(RetrofitError error) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        displayText(getResources().getText(R.string.empty_list).toString());
                    }
                };
                getActivity().runOnUiThread(runnable);
            }
        });
    }

    private void displayText(final String message) {
        mToastText.cancel();
        mToastText.setText(message);
        mToastText.show();
    }

    private ArrayList<SongLocal> getSongLocalArray(ArrayList<Track> tracksArrayList) {
        ArrayList<SongLocal> songLocalArray = new ArrayList<SongLocal>();
        for (Track track : tracksArrayList) {
            ArrayList<Image> imageArrayList = (ArrayList<Image>) track.album.images;
            String urlOfImageSmall, urlOfImageBig;
            if (imageArrayList.size() != 0) {
                urlOfImageSmall = imageArrayList.get(0).url;
                urlOfImageBig = imageArrayList.get(0).url;
                for (Image image : imageArrayList) {
                    if (image.height == LARGE_PICTURE_DIMENS && image.width == LARGE_PICTURE_DIMENS) {
                        urlOfImageBig = image.url;
                    } else if (image.height == SMALL_PICTURE_DIMENS && image.width == SMALL_PICTURE_DIMENS) {
                        urlOfImageSmall = image.url;
                    }
                }
            } else {
                urlOfImageSmall = getActivity().getResources().getString(R.string.blank_image);
                urlOfImageBig = getActivity().getResources().getString(R.string.blank_image);
            }
            songLocalArray.add(new SongLocal(track.id, track.name, track.album.name, track.preview_url, urlOfImageSmall, urlOfImageBig));
        }
        return songLocalArray;
    }

    public interface OnSongsListSelectedListener {
        void OnSongsListSelected(ArtistLocal artist, ArrayList songList, int position);
    }
}