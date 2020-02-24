package com.example.ressenger;

import android.content.Context;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PeopleFragment extends Fragment {

    List<UserDetails> onlineUsers = new ArrayList<UserDetails>();
    List<UserDetails> offlineUsers = new ArrayList<UserDetails>();

    public PeopleFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        final View rootView = inflater.inflate(R.layout.fragment_people, container, false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.peopleRecycler);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dsUsers)
            {
                final List<FriendCategory> categories = new ArrayList<FriendCategory>();
                DatabaseReference friends = FirebaseDatabase.getInstance().getReference("friends/"+ FirebaseAuth.getInstance().getUid());
                final List<String> friendsList = new ArrayList<String>();
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

                        LinearLayoutManager layoutManager = new LinearLayoutManager(PeopleFragment.this.getActivity());

                        //instantiate your adapter with the list of genres
                        FriendCategoryAdapter adapter = new FriendCategoryAdapter(categories);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
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

    public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

        String[] groupNames = {"Buddies", "Family", "Work"};
        String[][] childNames = {{"Popescu", "Vladescu", "Ionescu"}, {"Mama","Tata", "Cumnatu"}, {"Bossu", "Colegu", "Atat"}};
        @Override
        public int getGroupCount() {
            return groupNames.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childNames[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupNames[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childNames[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(PeopleFragment.this.getActivity());
            Typeface typeface = ResourcesCompat.getFont(PeopleFragment.this.getActivity(), R.font.times_new_roman_bold_italic);
            textView.setTextSize(15);
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(typeface);
            String text = groupNames[groupPosition] + " (" + getChildrenCount(groupPosition)+')';
            textView.setText(text);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) PeopleFragment.this.getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.layout_buddy, null);
            }
            TextView buddyName =  convertView.findViewById(R.id.Name);
            buddyName.setText(childNames[groupPosition][childPosition]);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}



