package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    ArrayAdapter<ArtistLocal> mArtistAdapter;
    private static String TAG = MainActivityFragment.class.getName();

    public static String EXTRA_ARTIST_DETAILS = "spotifystreamer.krzyzek.confkit.net.spotifystreamer.ARTIST_DETAILS";
    TextView mSearchArtist;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void getDataFromServer() {
        // mocking getting data from server
        mArtistAdapter.clear();

        String toSearch = mSearchArtist.getText().toString();
        if (!toSearch.isEmpty()) {
            getDataFromSpotify(toSearch);
        }
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
                // When user changed the Text
                getDataFromServer();
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

    private void getDataFromSpotify(String params) {
        if (!params.isEmpty()) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            spotify.searchArtists(params, new Callback<ArtistsPager>() {
                @Override
                public void success(final ArtistsPager artistsPager, Response response) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<kaaes.spotify.webapi.android.models.Artist> artistList = new ArrayList<>();
                            artistList = (ArrayList) artistsPager.artists.items;

                            updateUISuccess(artistList);
                        }
                    };
                    getActivity().runOnUiThread(runnable);
                }

                @Override
                public void failure(RetrofitError error) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            updateUIFailure();
                        }
                    };
                    getActivity().runOnUiThread(runnable);
                }
            });
        } else {
            Log.d(TAG, "Params is empty");
        }
    }

    private void updateUISuccess(ArrayList result) {
        if (result.size()==0) {
            Log.d(TAG, "onPostExecute(): List is empty.");
        } else {
            mArtistAdapter.clear();
            addResultsToAdapter(result);
        }
    }

    private void updateUIFailure() {
        Log.d(TAG, "Nothing was found");
    }

    private void addResultsToAdapter(ArrayList<Artist> result) {
        for (Artist artist : result) {
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


  /*  private class DownloadSpotifyArtist extends AsyncTask<String, Integer, ArrayList> {

        protected ArrayList doInBackground(String... params) {

            ArrayList<kaaes.spotify.webapi.android.models.Artist> artistList = new ArrayList<>();
            if (params.length!=0) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                ArtistsPager artistsPager = spotify.searchArtists(params[0], new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        Log.d(TAG, "Artist found: " + artistsPager.artists.items.toString());
                        artistList = (ArrayList) artistsPager.artists.items;
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });


            }
            return artistList;
        }

        protected void onProgressUpdate(Integer... progress) {
        //    setProgressPercent(progress[0]);
        }

        protected void onPostExecute(ArrayList result) {
            if (result.size()==0) {
                Log.d(TAG, "onPostExecute(): List is empty.");
            } else {
                mArtistAdapter.clear();
                addResultsToAdapter(result);
            }
        }

        private void addResultsToAdapter(ArrayList<Artist> result) {
            for (Artist artist : result) {
                ArrayList<Image> list = (ArrayList<Image>) artist.images;
                String urlOfImage;
                if (list.size()!=0) {
                    urlOfImage = list.get(0).url;
                } else {
                    urlOfImage = "http://i.imgur.com/DvpvklR.png";
                }
                mArtistAdapter.add(new ArtistLocal(artist.id, artist.name, urlOfImage));
            }
        }
    }*/
}