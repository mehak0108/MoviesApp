package com.example.mehak.movies.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieTrailer implements Parcelable {

    public String key;
    public MovieTrailer(){}

    public MovieTrailer(Parcel in){
        key = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(key);
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };
}
