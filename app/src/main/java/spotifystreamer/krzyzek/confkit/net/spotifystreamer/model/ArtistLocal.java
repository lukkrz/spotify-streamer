package spotifystreamer.krzyzek.confkit.net.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by £ukasz on 2015-06-13.
 */
public class ArtistLocal implements Parcelable {
    private String id;
    private String mName;
    private String mImage;

    public ArtistLocal(String id, String mName, String mImage) {
        this.id = id;
        this.mName = mName;
        this.mImage = mImage;
    }

    public ArtistLocal(Parcel object) {
        this.id = object.readString();
        this.mName = object.readString();
        this.mImage = object.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImage() {
        return mImage;
    }

    public void setmImage(String mImage) {
        this.mImage = mImage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mName);
        if (mImage != null) {
            dest.writeString(mImage);
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ArtistLocal createFromParcel(Parcel in) {
            return new ArtistLocal(in);
        }

        public ArtistLocal[] newArray(int size) {
            return new ArtistLocal[size];
        }
    };

}
