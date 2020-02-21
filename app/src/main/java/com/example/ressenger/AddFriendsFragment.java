package com.example.ressenger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.OnNmeaMessageListener;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class AddFriendsFragment extends Fragment {

    Button searchButton;
    EditText searchText;
    private DatabaseReference myRef;
    RecyclerView searchResults, requests;
    private RecyclerView.Adapter mAdapter;
    private OnFragmentInteractionListener mListener;

    Query friendRequests;

    public AddFriendsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_friends, container, false);

        searchButton = rootView.findViewById(R.id.searchButton);
        searchText = rootView.findViewById(R.id.searchText);
        myRef = FirebaseDatabase.getInstance().getReference("users");
        searchResults = rootView.findViewById(R.id.searchResults);
        searchText = rootView.findViewById(R.id.searchText);
        requests = rootView.findViewById(R.id.friendRequests);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPeople(searchText.getText().toString().trim(), false);
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                SearchPeople(searchText.getText().toString().trim(), false);
            }
        });

        searchResults.setHasFixedSize(true);
        searchResults.setLayoutManager(new LinearLayoutManager((AddFriendsFragment.this.getActivity())));
        SearchPeople(null, true);

        requests.setHasFixedSize(false);
        requests.setLayoutManager(new LinearLayoutManager((AddFriendsFragment.this.getActivity())));
        return rootView;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void SearchPeople(String usernameInput, final boolean fetchRequests)
    {
        Query searchPeopleQuery;
        if(fetchRequests)
            searchPeopleQuery = friendRequests = FirebaseDatabase.getInstance().getReference("friend-requests/"+ FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderByChild("type").startAt("received").endAt("received\uf8ff");
        else
            searchPeopleQuery = myRef.orderByChild("name").startAt(usernameInput).endAt(usernameInput + "\uf8ff");

        FirebaseRecyclerAdapter<FindNewFriends, FindPeopleViewHolder> adapter =
                new FirebaseRecyclerAdapter<FindNewFriends, FindPeopleViewHolder>
                        (
                                FindNewFriends.class,
                                R.layout.layout_buddy,
                                FindPeopleViewHolder.class,
                                searchPeopleQuery
                        ) {
                    @Override
                    protected void populateViewHolder(FindPeopleViewHolder findPeopleViewHolder, final FindNewFriends findNewFriends, int i) {
                            findPeopleViewHolder.setUsername(findNewFriends.getName());

                            if(fetchRequests)
                                findPeopleViewHolder.setStatus(findNewFriends.getStatus());
                            findPeopleViewHolder.setStatus(findNewFriends.getStatus());

                            findPeopleViewHolder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ChatActivity.ref = FirebaseDatabase.getInstance().getReference("users/"+findNewFriends.getUid());
                                    Intent intent = new Intent(AddFriendsFragment.this.getActivity(), ChatActivity.class);
                                    startActivity(intent);
                                }
                            });
                    }
                };

        if(fetchRequests)
            requests.setAdapter(adapter);
        else
            searchResults.setAdapter(adapter);
    }

    public static class FindPeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        View view;
        RecentsAdapter.onMessageListener listener;

        public FindPeopleViewHolder(View itemView)
        {
            super(itemView);
            view = itemView;
        }

        public FindPeopleViewHolder(View itemView, RecentsAdapter.onMessageListener listener)
        {
            super(itemView);
            view = itemView;
            this.listener=listener;
            itemView.setOnClickListener(this);
        }

        public void setUsername(String name) {
            TextView nameText = view.findViewById(R.id.Name);
            nameText.setText(name);
        }

        public void setStatus(String status)
        {
            TextView statusText = view.findViewById(R.id.Status);
            statusText.setText(status);
        }


        @Override
        public void onClick(View v) {
            if(listener!=null)
                listener.onMessage(getAdapterPosition());
        }
    }

}
