package com.example.ressenger;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


public class ProfileFragment extends Fragment {

    ImageView profilePic, profilePicEdit;
    TextView profileName, profileStatus, profileBio;
    Button profileEdit, profileEditSave, profileEditCancel;
    EditText profileNameEdit, profileStatusEdit, profileBioEdit;
    DatabaseReference myRef, myRefName, myRefStatus, myRefBio;

    ScrollView scrollViewProfile, scrollViewProfileEdit;

    Uri imageUri;

    public ProfileFragment() {}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_profile, container, false);
        
        profilePic = rootView.findViewById(R.id.profilePic);
        profilePicEdit = rootView.findViewById(R.id.profilePicEdit);
        profileName = rootView.findViewById(R.id.profileName);
        profileStatus = rootView.findViewById(R.id.profileStatus);
        profilePic = rootView.findViewById(R.id.profilePic);
        profileBio = rootView.findViewById(R.id.profileBio);
        profileEdit = rootView.findViewById(R.id.profileEdit);
        profileEditSave = rootView.findViewById(R.id.profileEditSave);
        profileEditCancel = rootView.findViewById(R.id.profileEditCancel);
        profileNameEdit = rootView.findViewById(R.id.profileNameEdit);
        profileStatusEdit = rootView.findViewById(R.id.profileStatusEdit);
        profileBioEdit = rootView.findViewById(R.id.profileBioEdit);
        scrollViewProfile = rootView.findViewById(R.id.scrollViewProfile);
        scrollViewProfileEdit = rootView.findViewById(R.id.scrollViewEditProfile);
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRefName = myRef.child("name");
        myRefStatus = myRef.child("status");
        myRefBio = myRef.child("bio");

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

        scrollViewProfile.setVisibility(View.VISIBLE);
        scrollViewProfileEdit.setVisibility(View.GONE);

        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollViewProfile.setVisibility(View.GONE);
                scrollViewProfileEdit.setVisibility(View.VISIBLE);
            }
        });

        profileEditCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollViewProfileEdit.setVisibility(View.GONE);
                scrollViewProfile.setVisibility(View.VISIBLE);
            }
        });

        profileEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(profileNameEdit.getText().toString().trim(),
                        profileStatusEdit.getText().toString().trim(),
                        profileBioEdit.getText().toString().trim());

                scrollViewProfileEdit.setVisibility(View.GONE);
                scrollViewProfile.setVisibility(View.VISIBLE);

            }
        });

        profilePicEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBrowser();
            }
        });
        
        return rootView;
    }
    
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void updateProfile(String username, String status, String bio)
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

            uploadImage(imageUri);
            Picasso.get().load(imageUri).into(profilePic);
            Picasso.get().load(imageUri).into(profilePicEdit);
        }
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    private void uploadImage(Uri uri)
    {
        if(uri==null)
            return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storage = FirebaseStorage.getInstance().getReference("pictures/"+uid);
        storage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ProfileFragment.this.getActivity(), "Image uploaded successfully!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileFragment.this.getActivity(), "Image upload failed!", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

    }

    private void retrieveImage()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            File profilePic = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
