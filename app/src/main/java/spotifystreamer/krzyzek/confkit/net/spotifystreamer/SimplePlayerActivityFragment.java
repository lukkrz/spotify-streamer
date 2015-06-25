package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.SongLocal;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SimplePlayerActivityFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SimplePlayerActivityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimplePlayerActivityFragment extends Fragment {
    private static final String TAG = SimplePlayerActivityFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private ImageView mLeftButton;


    public SimplePlayerActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_simple_player_activity, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();

        bundle.setClassLoader(SongLocal.class.getClassLoader());
        SongLocal songLocal = bundle.getParcelable(ArtistDetailedActivity.EXTRA_SONG_DETAILS);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_player_activity);
            actionBar.setSubtitle(songLocal.getmName());
        }

        mLeftButton = (ImageView) rootView.findViewById(R.id.button_left);
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCLick");
                Intent serviceIntent = new Intent(getActivity(), SimplePlayerService.class);
                serviceIntent.setAction(SimplePlayerService.ACTION_PLAY);
                getActivity().startService(serviceIntent);
            }
        });

        return rootView;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
