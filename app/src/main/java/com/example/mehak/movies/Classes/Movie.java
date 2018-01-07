package com.example.mehak.movies.Classes;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    public String title;
    public String poster_path;
    public String user_rating;
    public String thumbnail;
    public String plot;
    public String release_date;
    public String language;
    public String movie_id;

    public Movie(){}

    public Movie(Parcel in){
        poster_path=in.readString();
        thumbnail=in.readString();
        plot=in.readString();
        user_rating=in.readString();
        release_date=in.readString();
        title=in.readString();
        movie_id=in.readString();
        language = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(poster_path);
        dest.writeString(thumbnail);
        dest.writeString(plot);
        dest.writeString(user_rating);
        dest.writeString(release_date);
        dest.writeString(title);
        dest.writeString(movie_id);
        dest.writeString(language);

    }

    public static final Parcelable.Creator<Movie> CREATOR=new Parcelable.Creator<Movie>(){
        public Movie createFromParcel(Parcel parcel){
            return new Movie(parcel);
        }
        public Movie[] newArray(int i){
            return new Movie[i];
        }
    };
}
