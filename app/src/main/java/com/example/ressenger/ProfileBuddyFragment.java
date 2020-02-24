package com.example.ressenger;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileBuddyFragment extends Fragment {

    private DatabaseReference sendRequest;
    private DatabaseReference receiveRequest;
    private TextView name, status, bio;
    public final int NOT_FRIENDS = 0, REQUEST_SENT=1, REQUEST_RECEIVED=2, FRIENDS=3;
    private int friendshipState=0;
    private Button addFriendButton;
    private String uid;
    private String myUid;
    private ImageView profilePic;

    public ProfileBuddyFragment() {}
    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            name.setText(dataSnapshot.child("name").getValue(String.class));
            status.setText(dataSnapshot.child("status").getValue(String.class));
            bio.setText(dataSnapshot.child("bio").getValue(String.class));
            uid=dataSnapshot.child("uid").getValue(String.class);
            sendRequest = FirebaseDatabase.getInstance().getReference("friend-requests/"+myUid).child(uid);
            receiveRequest = FirebaseDatabase.getInstance().getReference("friend-requests/"+uid).child(myUid);
            updatePic(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    @Override
    public void onStart() {
        DatabaseReference reference = ChatActivity.ref;
        DatabaseReference requests = FirebaseDatabase.getInstance().getReference("friend-requests");
        reference.addValueEventListener(listener);
        requests.addValueEventListener(updateState);
        super.onStart();
    }
    private ValueEventListener updateState = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            DataSnapshot requestSent = dataSnapshot.child(myUid).child(uid+"/type");
            DataSnapshot requestReceived = dataSnapshot.child(uid).child(myUid+"/type");
            friendsCheck();
            if(!requestSent.exists() && !requestReceived.exists())
            {
                friendshipState=NOT_FRIENDS;
                addFriendButton.setText("Add buddy");
                addFriendButton.setVisibility(View.VISIBLE);
                return;
            }

            if(requestSent.getValue()=="received" && requestReceived.getValue()=="sent") {
                friendshipState = REQUEST_RECEIVED;
                addFriendButton.setText("Accept request");
                addFriendButton.setVisibility(View.VISIBLE);
                return;
            }

            friendshipState = REQUEST_SENT;
            addFriendButton.setText("Cancel request");
            addFriendButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    private void sendRequest()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);

        FriendRequest requestSent = new FriendRequest(name.getText().toString(), currentDate, "sent", uid);
        FriendRequest requestReceived = new FriendRequest(MainActivity.name, currentDate, "received", myUid);

        sendRequest.setValue(requestSent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(ProfileBuddyFragment.this.getActivity(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        receiveRequest.setValue(requestReceived).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(ProfileBuddyFragment.this.getActivity(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cancelRequest()
    {
        sendRequest.removeValue();
        receiveRequest.removeValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_buddy, container, false);
        name = rootView.findViewById(R.id.buddyProfileName);
        status = rootView.findViewById(R.id.buddyProfileStatus);
        bio = rootView.findViewById(R.id.buddyProfileBio);
        addFriendButton = rootView.findViewById(R.id.addFriendButton);
        profilePic = rootView.findViewById(R.id.buddyProfilePic);
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {handleRequest();}});

        return rootView;
    }

    private void acceptRequest()
    {
        DatabaseReference myRef, buddyRef;
        myRef = FirebaseDatabase.getInstance().getReference("friends/"+myUid).child(uid);
        buddyRef = FirebaseDatabase.getInstance().getReference("friends/"+uid).child(myUid);
        myRef.setValue("");
        buddyRef.setValue("");
        cancelRequest();
    }

    private void handleRequest()
    {
        switch (friendshipState)
        {
            case NOT_FRIENDS:
            {
                sendRequest();
                return;
            }
            case REQUEST_RECEIVED:
            {
                acceptRequest();
                return;
            }
            case REQUEST_SENT:
            {
                cancelRequest();
                return;
            }
            default:
        }
    }

    private void friendsCheck()
    {
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends");
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot friend_me = dataSnapshot.child(myUid).child(uid);
                DataSnapshot friend_them = dataSnapshot.child(uid).child(myUid);
                if(friend_me.exists() && friend_them.exists())
                {
                    friendshipState = FRIENDS;
                    addFriendButton.setVisibility(View.INVISIBLE);
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updatePic(DataSnapshot snapshot)
    {
        if (!snapshot.child("profile pic").exists()){

            profilePic.setImageResource(R.mipmap.ic_launcher);
            return;
        }

        String url = snapshot.child("profile pic").getValue().toString();
        Glide.with(getActivity()).load(url).asBitmap().into(profilePic);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStop() {
        super.onDestroy();
        super.onStop();
    }
}

