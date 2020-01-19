package com.example.ressenger;
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
    private String friendshipState;
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
    private ValueEventListener checkIfFriends = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child(myUid+"/buddies").hasChild(uid)
                && dataSnapshot.child(uid+"/buddies").hasChild(myUid))
                friendshipState= "friends";
                addFriendButton.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_profile_buddy, container, false);
        name = rootView.findViewById(R.id.buddyProfileName);
        status = rootView.findViewById(R.id.buddyProfileStatus);
        bio = rootView.findViewById(R.id.buddyProfileBio);
        addFriendButton = rootView.findViewById(R.id.addFriendButton);
        profilePic = rootView.findViewById(R.id.buddyProfilePic);
        DatabaseReference reference = ChatActivity.ref;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference requests = FirebaseDatabase.getInstance().getReference("friend-requests");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(checkIfFriends);
        reference.addValueEventListener(listener);
        requests.addValueEventListener(updateState);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (friendshipState) {
                    case "not friends":
                        sendRequest();
                    case "request sent":
                        cancelRequest();
                    case "request received":
                        acceptRequest();
                    default:{}
                }
            }
        });

        return rootView;
    }

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

    private void acceptRequest()
    {
        DatabaseReference myRef, buddyRef;
        myRef = FirebaseDatabase.getInstance().getReference("users/"+myUid+"/buddies/"+uid);
        buddyRef = FirebaseDatabase.getInstance().getReference("users/"+uid+"/buddies/"+myUid);
        myRef.setValue("true");
        buddyRef.setValue("true");
        cancelRequest();
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

    private ValueEventListener updateState = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            String requestSent = dataSnapshot.child(myUid).child(uid+"/type").getValue(String.class);
            String requestReceived = dataSnapshot.child(uid).child(myUid+"/type").getValue(String.class);

            if(requestSent==null || requestReceived==null || friendshipState==null)
            {
                friendshipState="not friends";
                addFriendButton.setText("Add buddy");
                addFriendButton.setVisibility(View.VISIBLE);
                return;
            }

            if(requestSent.equals("received") && requestReceived.equals("sent")) {
                friendshipState = "request received";
                addFriendButton.setText("Accept request");
                addFriendButton.setVisibility(View.VISIBLE);
                return;
            }

            friendshipState="request sent";
            addFriendButton.setText("Cancel request");
            addFriendButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

