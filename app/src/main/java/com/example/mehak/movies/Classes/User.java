package com.example.mehak.movies.Classes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String userName;
    public String email;
    public String password;
    public String userId;

    public User(){}

    public User(Parcel in){
        userName = in.readString();
        email = in.readString();
        password = in.readString();
       // userId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(userName);
        parcel.writeString(email);
        parcel.writeString(password);
      //  parcel.writeString(userId);

    }

    public static final Parcelable.Creator<User> CREATOR=new Parcelable.Creator<User>(){
        public User createFromParcel(Parcel parcel){
            return new User(parcel);
        }
        public User[] newArray(int i){
            return new User[i];
        }
    };
}
