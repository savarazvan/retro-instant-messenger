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
    private List<String> fetchedConversations = new ArrayList<>();
    public RecentsFragment() {}
    private ValueEventListener fetchMessages = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            fetchedConversations.clear();

            for (DataSnapshot node:dataSnapshot.getChildren())
                if (node.child("participants/" + myUid).exists() &&
                        node.child("participants/" + myUid).getValue(Boolean.class))
                            fetchedConversations.add(node.getKey());

            RecentsAdapter adapter = new RecentsAdapter(RecentsFragment.this.getActivity(), fetchedConversations, listener);

            recentMessages.setAdapter(adapter);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recents, container, false);
        messagesRef = FirebaseDatabase.getInstance().getReference("conversations");
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
        final String message = fetchedConversations.get(position);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("conversations/" + message);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("private").getValue(Boolean.class))
                {
                    String uid=null;
                    for(DataSnapshot participant : dataSnapshot.child("participants").getChildren()) {
                        if (!participant.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            uid = participant.getKey();
                    }
                    if(uid==null)
                        return;
                    ChatActivity.conversation = dataSnapshot.getKey();
                    ChatActivity.userRef = FirebaseDatabase.getInstance().getReference("users/"+uid);
                    Intent intent = new Intent(RecentsFragment.this.getActivity(), ChatActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}
