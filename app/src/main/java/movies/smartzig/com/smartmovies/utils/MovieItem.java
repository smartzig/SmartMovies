package movies.smartzig.com.smartmovies.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class MovieItem  implements Parcelable {


    @SerializedName("id")
    private Long id;
    @SerializedName("vote_average")
    private String voteAverage;
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("overview")
    private String overview;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("poster_path")
    private String posterPath;


    private MovieItem(Parcel in){
        id = in.readLong();
        voteAverage = in.readString();
        originalTitle = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(voteAverage);
        parcel.writeString(originalTitle);
        parcel.writeString(backdropPath);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(posterPath);
    }

    //Getter Methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getBackdropPath() {
        if (backdropPath != null && !backdropPath.isEmpty()) {
            if(!backdropPath.toLowerCase().contains("http://")){
                return "http://image.tmdb.org/t/p/original" + backdropPath;
            }else{
                return backdropPath;
            }

        }
        return null; //Use Picasso to put placeholder for poster
    }


    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Nullable
    public String getPosterPath() {
        if (posterPath != null && !posterPath.isEmpty()) {

            if(!posterPath.toLowerCase().contains("http://")){
                return "http://image.tmdb.org/t/p/w342" + posterPath;
            }else{
                return posterPath;
            }

        }
        return null; //Use Picasso to put placeholder for poster
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        if(!(o instanceof MovieItem))
            return false;

        MovieItem other = (MovieItem) o;
        if(this.id!=null){
            return this.id.equals(other.id);
        }else{
            return false;
        }
    }
}