package com.example.ressenger;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    TabLayout tabLayout;
    public static DatabaseReference userRef;
    public static String uid, myUid;
    Toolbar toolbar;
    public static String conversation;
    public static String name;
    private DatabaseReference conversationRef=FirebaseDatabase.getInstance().getReference("conversations");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                toolbar.setTitle(name);
                uid = dataSnapshot.child("uid").getValue(String.class);
            }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}});

        setContentView(R.layout.activity_chat);
        tabLayout = findViewById(R.id.tabLayoutChat);
        toolbar = findViewById(R.id.toolbar2);
        final ViewPager viewPager = findViewById(R.id.viewPagerChat);
        PagerAdapterChat pagerAdapter = new PagerAdapterChat(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

}
