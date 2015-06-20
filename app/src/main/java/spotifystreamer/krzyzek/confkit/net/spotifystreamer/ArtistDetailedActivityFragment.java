package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    private static String TAG = "ArtistDetailedActivityFragment";

    SpotifySongsAdapter mSongsAdapter;

    public void displayTopSongs(String artistID) {
        mSongsAdapter.clear();

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        Map<String, Object> map = new HashMap<>();
        map.put("country", Locale.getDefault().getCountry());

        spotify.getArtistTopTrack(artistID, map, new Callback<Tracks>() {

            @Override
            public void success(final Tracks tracks, Response response) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<kaaes.spotify.webapi.android.models.Track> trackList = (ArrayList) tracks.tracks;
                        if (trackList.isEmpty()) {
                            displayNoTracksMessage();
                        } else {
                            addTracksToAdapter(trackList);
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
                        displayNoTracksMessage();
                    }
                };
                getActivity().runOnUiThread(runnable);
            }
        });
    }

    private void displayNoTracksMessage() {
        Log.d(TAG, "Nothing was found");
        Toast.makeText(getActivity(), "No songs found for the artist.", Toast.LENGTH_LONG).show();
    }

    private void addTracksToAdapter(ArrayList<Track> result) {
        for (Track track : result) {
            ArrayList<Image> list = (ArrayList<Image>) track.album.images;
            String urlOfImage;
            if (list.size()!=0) {
                urlOfImage = list.get(0).url;
            } else {
                urlOfImage = getActivity().getResources().getString(R.string.blank_image);
            }
            mSongsAdapter.add(new SongLocal(track.id, track.name, track.album.name, track.href, urlOfImage));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artist_detailed, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        ArtistLocal artistLocal = bundle.getParcelable(MainActivityFragment.EXTRA_ARTIST_DETAILS);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_artist_detailed);
            actionBar.setSubtitle(artistLocal.getmName());
        }

        mSongsAdapter = new SpotifySongsAdapter(
                getActivity(),
                new ArrayList<SongLocal>()
        );

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(mSongsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongLocal songLocal = (SongLocal) parent.getAdapter().getItem(position);
                Toast.makeText(getActivity(), "Clicking on: " + songLocal.getmName(), Toast.LENGTH_LONG).show();
            }
        });
        TextView emptyText = (TextView) rootView.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        displayTopSongs(artistLocal.getId());

        return rootView;
    }
}