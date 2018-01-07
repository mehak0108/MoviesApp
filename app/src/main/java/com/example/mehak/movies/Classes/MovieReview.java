package com.example.mehak.movies.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReview implements Parcelable{

    public String userId;
    public String reviewPosted;

    public MovieReview(){}

    public MovieReview(Parcel in){
        userId = in.readString();
        reviewPosted = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(userId);
        parcel.writeString(reviewPosted);
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}
