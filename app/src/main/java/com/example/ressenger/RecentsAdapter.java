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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<AddFriendsFragment.FindPeopleViewHolder> {

    private Context context;
    private List<Message> recents;
    private onMessageListener listener;

    public RecentsAdapter(Context context, List<Message> recents, onMessageListener listener) {
        this.context = context;
        this.recents = recents;
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
        final Message recent = recents.get(position);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/"+recent.getSender());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.setUsername(dataSnapshot.child("name").getValue(String.class));
                holder.setStatus(dataSnapshot.child("status").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    @Override
    public int getItemCount() {
        return recents.size();
    }

    public interface onMessageListener{
        void onMessage(int position);
    }
}
