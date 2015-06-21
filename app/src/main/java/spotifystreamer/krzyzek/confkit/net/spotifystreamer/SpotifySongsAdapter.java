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

import spotifystreamer.krzyzek.confkit.net.spotifystreamer.model.SongLocal;

/**
 * Created by £ukasz on 2015-06-20.
 */
public class SpotifySongsAdapter extends ArrayAdapter<SongLocal> {

    Context mContext;
    ArrayList<SongLocal> mArrayList;

    public SpotifySongsAdapter(Context context, ArrayList<SongLocal> arrayList) {
        super(context, 0, arrayList);
        mContext = context;
        mArrayList = arrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SongLocal song = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_artist_detailed_list_item, parent, false);
            viewHolder.artistName = (TextView) convertView.findViewById(R.id.songName);
            viewHolder.albumName = (TextView) convertView.findViewById(R.id.albumName);
            viewHolder.albumPicture = (ImageView) convertView.findViewById(R.id.albumPicture);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.artistName.setText(song.getmName());
        viewHolder.albumName.setText(song.getmAlbum());
        Picasso.with(mContext).load(song.getmImage()).into(viewHolder.albumPicture);

        return convertView;
    }

    static class ViewHolder {
        TextView artistName;
        TextView albumName;
        ImageView albumPicture;
    }
}
