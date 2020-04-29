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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RecentsFragment extends Fragment implements RecentsAdapter.onMessageListener {

    RecentsAdapter.onMessageListener listener = this;
    private RecyclerView recentMessages;
    private DatabaseReference messagesRef, userRef;
    private List<Message> fetchedMessages = new ArrayList<>();
    public RecentsFragment() {}
    private ValueEventListener fetchMessages = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            fetchedMessages.clear();
            List<String> uids = new ArrayList<>();//here we'll store the users that have already been fetched in order to avoid duplicates
            for (DataSnapshot node:dataSnapshot.getChildren()
                 ) {

                //if the message isn't addressed to us or the user has already been fetched, skip
                if(!node.child("receiver").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                || uids.contains(node.child("sender").getValue().toString()))
                    continue;
                uids.add(node.child("sender").getValue().toString());
                fetchedMessages.add(node.getValue(Message.class));
            }


            RecentsAdapter adapter = new RecentsAdapter(RecentsFragment.this.getActivity(), fetchedMessages, listener);

            recentMessages.setAdapter(adapter);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recents, container, false);
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        recentMessages = rootView.findViewById(R.id.recentMessages);
        recentMessages.setHasFixedSize(true);
        recentMessages.setLayoutManager(new LinearLayoutManager(RecentsFragment.this.getActivity()));
        messagesRef.addValueEventListener(fetchMessages);
        return rootView;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMessage(int position) {
        Message message = fetchedMessages.get(position);
        ChatActivity.ref = FirebaseDatabase.getInstance().getReference("users/"+message.getSender());
        Intent intent = new Intent(RecentsFragment.this.getActivity(), ChatActivity.class);
        startActivity(intent);
    }
}
