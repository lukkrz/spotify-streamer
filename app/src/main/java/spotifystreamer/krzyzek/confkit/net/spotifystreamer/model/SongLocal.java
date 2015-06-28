package spotifystreamer.krzyzek.confkit.net.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by £ukasz on 2015-06-20.
 */
public class SongLocal implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SongLocal createFromParcel(Parcel in) {
            return new SongLocal(in);
        }

        public SongLocal[] newArray(int size) {
            return new SongLocal[size];
        }
    };
    private String id;
    private String mName;
    private String mAlbum;
    private String mUrl;
    private String mImageSmall;
    private String mImageBig;

    public SongLocal(String id, String name, String album, String url, String imageSmall, String imageBig) {
        this.id = id;
        this.mName = name;
        this.mAlbum = album;
        this.mUrl = url;
        this.mImageSmall = imageSmall;
        this.mImageBig = imageBig;
    }

    public SongLocal(Parcel object) {
        this.id = object.readString();
        this.mName = object.readString();
        this.mAlbum = object.readString();
        this.mUrl = object.readString();
        this.mImageSmall = object.readString();
        this.mImageBig = object.readString();
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public String getmUrl() {
        return mUrl;
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

    public String getmImageSmall() {
        return mImageSmall;
    }

    public void setmImageSmall(String mImageSmall) {
        this.mImageSmall = mImageSmall;
    }

    public String getmImageBig() {
        return mImageBig;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mName);
        dest.writeString(mAlbum);
        dest.writeString(mUrl);
        dest.writeString(mImageSmall);
        dest.writeString(mImageBig);

    }


}
