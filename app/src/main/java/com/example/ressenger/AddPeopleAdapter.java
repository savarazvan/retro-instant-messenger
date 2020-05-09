package com.example.ressenger;

import android.content.Context;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
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

public class AddPeopleAdapter extends RecyclerView.Adapter<AddPeopleAdapter.ViewHolder> {

    private Context context;
    private List<UserDetails> friendsList;

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    public AddPeopleAdapter(Context context, List<UserDetails> friendsList, @NonNull OnItemCheckListener onItemCheckListener)
    {
        this.context = context;
        this.friendsList = friendsList;
        this.onItemCheckListener = onItemCheckListener;
    }

    @NonNull
    @Override
    public AddPeopleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_add_people, parent, false);

        return new AddPeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddPeopleAdapter.ViewHolder holder, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+friendsList.get(position).getUid());
        final UserDetails currentUser = friendsList.get(position);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.setName(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(
                        !holder.checkBox.isChecked());
                if (holder.checkBox.isChecked()) {
                    onItemCheckListener.onItemCheck(currentUser);
                } else {
                    onItemCheckListener.onItemUncheck(currentUser);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    interface OnItemCheckListener {
        void onItemCheck(UserDetails user);
        void onItemUncheck(UserDetails user);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name;
        public View itemView;
        public CheckBox checkBox;

        public ViewHolder(View itemView)
        {
            super(itemView);
            this.itemView=itemView;
            name = itemView.findViewById(R.id.addPeopleName);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setClickable(false);
        }
        public void setName(String username)
        {
            name.setText(username);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }

    }
}
