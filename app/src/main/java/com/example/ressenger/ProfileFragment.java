package com.example.ressenger;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    public static int status;
    public static final int DEFAULT=0, EDITING=1;
    public static ImageView profilePic, profilePicEdit;
    public static TextView profileName, profileStatus, profileBio;
    public static EditText profileNameEdit, profileStatusEdit, profileBioEdit;
    public static DatabaseReference myRef, myRefName, myRefStatus, myRefBio;
    public static ScrollView scrollViewProfile, scrollViewProfileEdit;
    public static Uri imageUri;
    public static StorageReference storage;
    public ProfileFragment() {}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_profile, container, false);
        status = DEFAULT;

        profilePic = rootView.findViewById(R.id.profilePic);
        profilePicEdit = rootView.findViewById(R.id.profilePicEdit);
        profileName = rootView.findViewById(R.id.profileName);
        profileStatus = rootView.findViewById(R.id.profileStatus);
        profileBio = rootView.findViewById(R.id.profileBio);
        profileNameEdit = rootView.findViewById(R.id.profileNameEdit);
        profileStatusEdit = rootView.findViewById(R.id.profileStatusEdit);
        profileBioEdit = rootView.findViewById(R.id.profileBioEdit);
        scrollViewProfile = rootView.findViewById(R.id.scrollViewProfile);
        scrollViewProfileEdit = rootView.findViewById(R.id.scrollViewEditProfile);

        myRef = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRefName = myRef.child("name");
        myRefStatus = myRef.child("status");
        myRefBio = myRef.child("bio");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance().getReference("pictures/"+uid);

        myRefName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                profileName.setText(value);
                profileNameEdit.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        myRefStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                profileStatus.setText(value);
                profileStatusEdit.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        myRefBio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                profileBio.setText(value);
                profileBioEdit.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                retrieveImage(dataSnapshot, uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


        scrollViewProfile.setVisibility(View.VISIBLE);
        scrollViewProfileEdit.setVisibility(View.GONE);
        profilePicEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBrowser();
            }
        });
        return rootView;
    }

    public static void updateProfile(String username, String status, String bio)
    {
        if(!username.equals(profileName.getText().toString()))
            myRefName.setValue(username);

        if(!status.equals(profileStatus.getText().toString()))
            myRefStatus.setValue(status);

        if(!bio.equals(profileBio.getText().toString()))
            myRefBio.setValue(bio);
    }

    private void imageBrowser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profilePicEdit);
        }
    }

    public static void uploadImage()
    {
        if(imageUri==null)
            return;
        storage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();
                myRef.child("profile pic").setValue(downloadUrl.toString());

            }
        });
    }

    private void retrieveImage(DataSnapshot dataSnapshot, String uid)
    {
        if (dataSnapshot.exists()){
            String url = dataSnapshot.child("profile pic").getValue(String.class);
            Glide.with(getActivity()).load(url).asBitmap().into(profilePic);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

