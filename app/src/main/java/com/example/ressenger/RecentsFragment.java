package com.example.ressenger;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Collections;

public class RecentsFragment extends Fragment {

    private DatabaseReference messagesRef;
    private RecyclerView recentMessages;

    public RecentsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recents, container, false);
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        recentMessages = rootView.findViewById(R.id.recentMessages);
        recentMessages.setHasFixedSize(true);
        recentMessages.setLayoutManager(new LinearLayoutManager(RecentsFragment.this.getActivity()));
        //fetchMessages();
        return rootView;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void fetchMessages()
    {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query recentsQuery = messagesRef
                .orderByChild("receiver").startAt(myUid).endAt(myUid + "\uf8ff");
        FirebaseRecyclerAdapter<RecentMessages, AddFriendsFragment.FindPeopleViewHolder> adapter =
                new FirebaseRecyclerAdapter<RecentMessages, AddFriendsFragment.FindPeopleViewHolder>
                        (
                                RecentMessages.class,
                                R.layout.layout_buddy,
                                AddFriendsFragment.FindPeopleViewHolder.class,
                                recentsQuery
                        ) {
                    @Override
                    protected void populateViewHolder(final AddFriendsFragment.FindPeopleViewHolder findPeopleViewHolder, final RecentMessages recentMessages, int i) {

                        final DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference("users/" + recentMessages.getSender());

                        while(recentMessages.getName()==null) {
                            currentUser.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    recentMessages.setName(dataSnapshot.child("name").getValue(String.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }

                        findPeopleViewHolder.setUsername(recentMessages.getName());
                        findPeopleViewHolder.setStatus(recentMessages.getContent());
                        findPeopleViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChatActivity.ref = currentUser;
                                Intent intent = new Intent(RecentsFragment.this.getActivity(), ChatActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                };

        recentMessages.setAdapter(adapter);
    }
}
