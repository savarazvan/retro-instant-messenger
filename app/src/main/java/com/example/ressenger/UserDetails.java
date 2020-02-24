package com.example.ressenger;

import android.media.Image;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

public class UserDetails implements Parcelable {

    public String name;
    public String email;
    public String status;
    public String bio;
    public String uid;
    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
    public Boolean presence;

    public UserDetails(){}

   public UserDetails(String name, String email, String uid)
   {
       this.name = name;
       this.email = email;
       this.uid = uid;
       status = "I'm using Retro Instant Messenger!";
       bio = "Hello there!";
       presence = true;
   }

    protected UserDetails(Parcel in) {
        name = in.readString();
        email = in.readString();
        status = in.readString();
        bio = in.readString();
        uid = in.readString();
        byte tmpPresence = in.readByte();
        presence = tmpPresence == 0 ? null : tmpPresence == 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getPresence() {
        return presence;
    }

    public void setPresence(Boolean presence) {
        this.presence = presence;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(status);
        dest.writeString(bio);
        dest.writeString(uid);
        dest.writeByte((byte) (presence == null ? 0 : presence ? 1 : 2));
    }
}
