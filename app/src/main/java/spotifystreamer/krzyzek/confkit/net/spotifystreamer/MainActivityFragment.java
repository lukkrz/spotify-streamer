package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;

public class MainActivityFragment extends Fragment {
    public static String EXTRA_ARTIST_DETAILS = "spotifystreamer.krzyzek.confkit.net.spotifystreamer.ARTIST_DETAILS";
    private static String TAG = MainActivityFragment.class.getName();
    private static String ARTIST_ARRAY_KEY = "artistArray";
    private static String SEARCH_TEXT_KEY = "searchText";

    OnArtistListSelectedListener mCallback;
    private ArrayAdapter<ArtistLocal> mArtistAdapter;
    private TextView mSearchArtist;
    private SpotifyApi mSpotifyApi;
    private ArrayList<ArtistLocal> mArtistList;
    private String mSearchText;
    private Toast mToastText;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpotifyApi = new SpotifyApi();
        mToastText = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        if (savedInstanceState != null) {
            mArtistList = savedInstanceState.getParcelableArrayList(ARTIST_ARRAY_KEY);
            mArtistAdapter = new SpotifyArtistsAdapter(
                    getActivity(),
                    mArtistList
            );
            mSearchText = savedInstanceState.getString(SEARCH_TEXT_KEY);
        } else {
            mArtistAdapter = new SpotifyArtistsAdapter(
                    getActivity(),
                    new ArrayList<ArtistLocal>()
            );
            mSearchText = "";
        }
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelableArrayList(ARTIST_ARRAY_KEY, mArtistList);
        //to prevent invoking additional calls to Spotify API after screen rotation
        savedState.putString(SEARCH_TEXT_KEY, mSearchText);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnArtistListSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistListSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mSearchArtist = (TextView) rootView.findViewById(R.id.editText);
        mSearchArtist.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (!(cs.toString()).equals(mSearchText)) {
                    mSearchText = cs.toString();
                    displayArtists(cs.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.artist_list_view);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistLocal artistLocal = (ArtistLocal) parent.getAdapter().getItem(position);
                mCallback.OnArtistListSelected(artistLocal, position);
            }
        });

        TextView emptyText = (TextView) rootView.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        return rootView;
    }

    private void displayArtists(String artistName) {
        mArtistAdapter.clear();

        if (!artistName.isEmpty()) {

            SpotifyService spotify = mSpotifyApi.getService();

            spotify.searchArtists(artistName, new Callback<ArtistsPager>() {
                @Override
                public void success(final ArtistsPager artistsPager, Response response) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<kaaes.spotify.webapi.android.models.Artist> artistList =
                                    (ArrayList) artistsPager.artists.items;
                            if (artistList.isEmpty()) {
                                displayText(getResources().getText(R.string.empty_list_artists).toString());
                            } else {
                                mArtistList = getAristLocalArray(artistList);
                                mArtistAdapter.addAll(mArtistList);
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
                            displayText(getResources().getText(R.string.empty_list_artists_failure).toString());
                        }
                    };
                    getActivity().runOnUiThread(runnable);
                }
            });
        }
    }

    private void displayText(final String message) {
        mToastText.cancel();
        mToastText.setText(message);
        mToastText.show();
    }

    private ArrayList<ArtistLocal> getAristLocalArray(ArrayList<Artist> artistsArrayList) {
        ArrayList<ArtistLocal> artistLocalArray = new ArrayList<ArtistLocal>();
        for (Artist artist : artistsArrayList) {
            ArrayList<Image> list = (ArrayList<Image>) artist.images;
            String artistImage;
            if (list.size() != 0) {
                artistImage = list.get(0).url;
            } else {
                artistImage = getActivity().getResources().getString(R.string.blank_image);
            }
            artistLocalArray.add(new ArtistLocal(artist.id, artist.name, artistImage));
        }
        return artistLocalArray;
    }

    public interface OnArtistListSelectedListener {
        public void OnArtistListSelected(ArtistLocal artist, int position);
    }

}