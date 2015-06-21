package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.content.Intent;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    public static String EXTRA_ARTIST_DETAILS = "spotifystreamer.krzyzek.confkit.net.spotifystreamer.ARTIST_DETAILS";
    private static String TAG = MainActivityFragment.class.getName();
    ArrayAdapter<ArtistLocal> mArtistAdapter;
    TextView mSearchArtist;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mArtistAdapter = new SpotifyArtistsAdapter(
                getActivity(),
                new ArrayList<ArtistLocal>()
        );

        mSearchArtist = (TextView) rootView.findViewById(R.id.editText);
        mSearchArtist.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                displayArtists(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistLocal artist = (ArtistLocal) parent.getAdapter().getItem(position);
                Toast.makeText(getActivity(), "Clicking on: " + artist.getmName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), ArtistDetailedActivity.class);
                intent.putExtra(EXTRA_ARTIST_DETAILS, artist);
                startActivity(intent);
            }
        });

        TextView emptyText = (TextView)rootView.findViewById(android.R.id.empty);
        listView.setEmptyView(emptyText);

        return rootView;
    }

    private void displayArtists(String artistName) {
        mArtistAdapter.clear();

        if (!artistName.isEmpty()) {

            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            spotify.searchArtists(artistName, new Callback<ArtistsPager>() {
                @Override
                public void success(final ArtistsPager artistsPager, Response response) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<kaaes.spotify.webapi.android.models.Artist> artistList =
                                    (ArrayList) artistsPager.artists.items;
                            if (artistList.isEmpty()) {
                                displayNoArtistMessage();
                            } else {
                                addArtistToAdapter(artistList);
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
                            displayNoArtistMessage();
                        }
                    };
                    getActivity().runOnUiThread(runnable);
                }
            });
        }
    }

    private void displayNoArtistMessage() {
        Toast.makeText(getActivity(), getResources().getText(R.string.empty_list_artists), Toast.LENGTH_LONG).show();
    }

    private void addArtistToAdapter(ArrayList<Artist> artistsArray) {
        for (Artist artist : artistsArray) {
            ArrayList<Image> list = (ArrayList<Image>) artist.images;
            String urlOfImage;
            if (list.size()!=0) {
                urlOfImage = list.get(0).url;
            } else {
                urlOfImage = getActivity().getResources().getString(R.string.blank_image);
            }
            mArtistAdapter.add(new ArtistLocal(artist.id, artist.name, urlOfImage));
        }
    }
}