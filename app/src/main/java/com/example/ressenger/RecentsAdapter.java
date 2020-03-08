package com.example.ressenger;

import android.content.Context;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<AddFriendsFragment.FindPeopleViewHolder> {

    private Context context;
    private List<String> conversations;
    private onMessageListener listener;

    public RecentsAdapter(Context context, List<String> conversations, onMessageListener listener) {
        this.context = context;
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddFriendsFragment.FindPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_buddy, parent, false);
        return new AddFriendsFragment.FindPeopleViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddFriendsFragment.FindPeopleViewHolder holder, int position) {
        final String conversation = conversations.get(position);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("conversations/"+conversation);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("private").getValue(Boolean.class))
                {
                    String uid = "default";
                    List<String> participants = new ArrayList<>();
                    for(DataSnapshot participant : dataSnapshot.child("participants").getChildren()) {
                        if (participant.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            continue;
                        uid = participant.getKey();
                    }
                    DatabaseReference usersRef=FirebaseDatabase.getInstance().getReference("users/"+uid);
                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userDs) {
                            holder.setUsername(userDs.child("name").getValue(String.class));
                            holder.setStatus(userDs.child("status").getValue(String.class));
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}});
                }
                else
                {
                    DatabaseReference chatroom = FirebaseDatabase.getInstance().getReference("chatrooms/"+conversation);
                    chatroom.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            holder.setUsername(dataSnapshot.child("title").getValue(String.class));
                            holder.setStatus(dataSnapshot.child("description").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}});
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public interface onMessageListener{
        void onMessage(int position);
    }
}
