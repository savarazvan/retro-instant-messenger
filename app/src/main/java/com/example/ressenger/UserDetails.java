package com.example.ressenger;

import android.media.Image;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class UserDetails {

    public String name;
    public String email;
    public String status;
    public String bio;
    public String uid;

   public UserDetails(String name, String email, String uid)
   {
       this.name = name;
       this.email = email;
       this.uid = uid;
       status = "I'm using Retro Instant Messenger!";
       bio = "Hello there!";
   }
}
