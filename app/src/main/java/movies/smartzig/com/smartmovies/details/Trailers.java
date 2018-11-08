package movies.smartzig.com.smartmovies.details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Trailers implements Parcelable {


    public static final Parcelable.Creator<Trailers> CREATOR = new Creator<Trailers>() {
        public Trailers createFromParcel(Parcel source) {
            Trailers trailer = new Trailers();
            trailer.id = source.readString();
            trailer.key = source.readString();
            trailer.name = source.readString();
            trailer.site = source.readString();
            trailer.size = source.readString();
            return trailer;
        }

        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };
    @SuppressWarnings("unused")
    //public static final String LOG_TAG = Trailers.class.getSimpleName();

    @SerializedName("id")
    private String id;
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("site")
    private String site;
    @SerializedName("size")
    private String size;

    public Trailers() {

    }


    public Trailers(String id, String key, String name, String site, String size) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getSite() {
        return site;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getSize() {
        return size;
    }

    public String getTrailerUrl() {
        return "http://www.youtube.com/watch?v=" + key;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeString(size);
    }
}