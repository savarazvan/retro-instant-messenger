package com.example.ressenger;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PeopleFragment extends Fragment {

    private final int DEFAULT=0, ADDING=1, EDITING=2;
    private List<UserDetails> onlineUsers = new ArrayList<UserDetails>();
    private List<UserDetails> offlineUsers = new ArrayList<UserDetails>();
    private ImageView addButton, editButton;
    private TextInputLayout addGroupInput;
    private int status = DEFAULT;
    private DatabaseReference people;
    public PeopleFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        people = FirebaseDatabase.getInstance().getReference("people/"+FirebaseAuth.getInstance().getUid());
        final View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.peopleRecycler);
        people.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dsPeople)
            {
                final List<FriendCategory> categories = new ArrayList<FriendCategory>();
                final DatabaseReference friends = FirebaseDatabase.getInstance().getReference("friends/"+ FirebaseAuth.getInstance().getUid());
                final List<String> friendsList = new ArrayList<String>();
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dsUsers) {
                        friends.addValueEventListener(new ValueEventListener() { //updating the list of friends
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                friendsList.clear();
                                onlineUsers.clear();
                                offlineUsers.clear();
                                for (DataSnapshot friend:dataSnapshot.getChildren()
                                ) {
                                    String uid = friend.getKey();
                                    friendsList.add(uid);}
                                for (DataSnapshot user : dsUsers.getChildren())
                                {
                                    UserDetails curUser = user.getValue(UserDetails.class);
                                    if(!friendsList.contains(curUser.getUid()) || onlineUsers.contains(curUser) || offlineUsers.contains(curUser))//if not friend or already in list
                                        continue;

                                    if(curUser.getPresence())
                                    {
                                        onlineUsers.add(curUser);
                                        continue;
                                    }

                                    offlineUsers.add(curUser);
                                }

                                categories.add(new FriendCategory("Online", onlineUsers));
                                categories.add(new FriendCategory("Offline", offlineUsers));

                                for(DataSnapshot parent : dsPeople.getChildren())
                                {
                                    List<UserDetails> children = new ArrayList<>();
                                    for (DataSnapshot user : parent.getChildren()) {
                                        UserDetails userDetails = dsUsers.child(user.getKey()).getValue(UserDetails.class);
                                        children.add(userDetails);
                                    }
                                    categories.add(new FriendCategory(parent.getKey(), children));
                                }

                                LinearLayoutManager layoutManager = new LinearLayoutManager(PeopleFragment.this.getActivity());
                                FriendCategoryAdapter adapter = new FriendCategoryAdapter(categories);
                                adapter.notifyDataSetChanged();
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                            }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}}); }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}});}
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}});

        addButton = rootView.findViewById(R.id.addButton);
        editButton = rootView.findViewById(R.id.editButton);
        addGroupInput = rootView.findViewById(R.id.addGroupInput);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status)
                {
                    case DEFAULT:
                    {
                        AddPeopleBottomSheet bottomSheet = new AddPeopleBottomSheet(PeopleFragment.this.getActivity());
                        bottomSheet.show(getFragmentManager(), "bottomsheet");
                        break;
                    }

                    case EDITING:
                    {
                        status=DEFAULT;
                        break;
                    }
                    default:{break;}
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status)
                {
                    case DEFAULT:
                    {
                        status=EDITING;
                        break;
                    }
                    case ADDING:
                    {
                        addGroupInput.setVisibility(View.GONE);
                        status=DEFAULT;
                        break;
                    }

                    case EDITING:
                    {
                        status=DEFAULT;
                        break;
                    }
                    default:{break;}
                }
            }
        });

        return rootView;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void updatePresence(DataSnapshot dsUsers)
    {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}



