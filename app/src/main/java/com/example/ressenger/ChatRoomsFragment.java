package com.example.ressenger;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

public class ChatRoomsFragment extends Fragment implements RecentsAdapter.onMessageListener {

    private RecyclerView chatRoomsRecycler;
    private DatabaseReference chatRoomsReference;
    private List<String> fetchedConversations = new ArrayList<>();
    RecentsAdapter.onMessageListener listener = this;
    private ValueEventListener fetchChatRooms = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            fetchedConversations.clear();
            for (DataSnapshot node:dataSnapshot.getChildren())
                if (node.child("participants/" + myUid).exists() &&
                        node.child("participants/" + myUid).getValue(Boolean.class) &&
                        node.child("private").getValue(Boolean.class)==false)
                    fetchedConversations.add(node.getKey());

            RecentsAdapter adapter = new RecentsAdapter(ChatRoomsFragment.this.getActivity(), fetchedConversations, listener);
            chatRoomsRecycler.setAdapter(adapter);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chatrooms, container, false);
        chatRoomsReference = FirebaseDatabase.getInstance().getReference("conversations");
        chatRoomsRecycler = rootView.findViewById(R.id.chatRoomsRecycler);
        chatRoomsRecycler.setHasFixedSize(true);
        chatRoomsRecycler.setLayoutManager(new LinearLayoutManager(ChatRoomsFragment.this.getActivity()));
        chatRoomsReference.addValueEventListener(fetchChatRooms);
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                    ChatRoomActivity.conversationRef=ref;
                    ChatRoomActivity.id=message;
                    Intent intent = new Intent(ChatRoomsFragment.this.getActivity(), ChatRoomActivity.class);
                    startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});
    }
}
