package spotifystreamer.krzyzek.confkit.net.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.ArtistLocal;

/**
 * Created by £ukasz on 2015-06-14.
 */
public class SpotifyArtistsAdapter extends ArrayAdapter<ArtistLocal> {

    Context mContext;
    ArrayList<ArtistLocal> mArrayList;

    public SpotifyArtistsAdapter(Context context, ArrayList<ArtistLocal> arrayList) {
        super(context, 0, arrayList);
        mContext = context;
        mArrayList = arrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ArtistLocal artist = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_main_list_item, parent, false);
            viewHolder.artistName = (TextView) convertView.findViewById(R.id.artistName);
            viewHolder.artistPicture = (ImageView) convertView.findViewById(R.id.artistPicture);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.artistName.setText(artist.getmName());
        Picasso.with(mContext).load(artist.getmImage()).into(viewHolder.artistPicture);

        return convertView;
    }

    static class ViewHolder {
        TextView artistName;
        ImageView artistPicture;
    }
}