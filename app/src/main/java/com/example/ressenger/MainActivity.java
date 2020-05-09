package com.example.ressenger;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity implements RecentsFragment.OnFragmentInteractionListener, PeopleFragment.OnFragmentInteractionListener, ChatRoomsFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, AddFriendsFragment.OnFragmentInteractionListener {


    FirebaseAuth mAuth;
    Toolbar toolbar;
    TabLayout tabLayout;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static String name;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    MenuItem secondButton, thirdButton;

    @Override
    public boolean onCreateOptionsMenu(Menu m) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, m);
        secondButton = m.findItem(R.id.secondButton);
        thirdButton = m.findItem(R.id.thirdButton);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.LogOut:
            {
                FirebaseAuth.getInstance().signOut();
                goToLogin();
                break;
            }
            case R.id.secondButton:
            {
                switch (viewPager.getCurrentItem())
                {
                    case 1:
                    {
                        AddPeopleBottomSheet bottomSheet = new AddPeopleBottomSheet(this,false);
                        bottomSheet.show(getSupportFragmentManager(), "bottomsheet");
                        break;
                    }
                    case 2:
                    {
                        AddPeopleBottomSheet bottomSheet = new AddPeopleBottomSheet(this,true);
                        bottomSheet.show(getSupportFragmentManager(), "bottomsheet");
                        break;
                    }
                    case 3:
                    {
                        if(ProfileFragment.status == ProfileFragment.DEFAULT) {
                            ProfileFragment.scrollViewProfile.setVisibility(View.GONE);
                            ProfileFragment.scrollViewProfileEdit.setVisibility(View.VISIBLE);
                            ProfileFragment.status = ProfileFragment.EDITING;
                            secondButton.setIcon(R.drawable.savebutton);
                            thirdButton.setVisible(true);
                            break;
                        }

                        ProfileFragment.updateProfile(ProfileFragment.profileNameEdit.getText().toString().trim(),
                                ProfileFragment.profileStatusEdit.getText().toString().trim(),
                                ProfileFragment.profileBioEdit.getText().toString().trim());
                        ProfileFragment.scrollViewProfileEdit.setVisibility(View.GONE);
                        ProfileFragment.scrollViewProfile.setVisibility(View.VISIBLE);
                        ProfileFragment.uploadImage();
                        ProfileFragment.status = ProfileFragment.DEFAULT;
                        secondButton.setIcon(R.drawable.editicon);
                        thirdButton.setVisible(false);
                        break;
                    }
                }
                break;
            }

            case R.id.thirdButton:
            {
                ProfileFragment.scrollViewProfileEdit.setVisibility(View.GONE);
                ProfileFragment.profilePicEdit.setImageDrawable(ProfileFragment.profilePic.getDrawable());
                ProfileFragment.scrollViewProfile.setVisibility(View.VISIBLE);
                ProfileFragment.status = ProfileFragment.DEFAULT;
                secondButton.setIcon(R.drawable.editicon);
                thirdButton.setVisible(false);
                break;
            }
            default:{}

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if(mAuth.getCurrentUser()==null) {
            goToLogin();
            return;
        }

        myRef = database.getReference("users/"+mAuth.getCurrentUser().getUid());
        tabLayout = findViewById(R.id.tabLayout);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == 0 || tab.getPosition() == 4)
                    secondButton.setVisible(false);

                else if(tab.getPosition() == 1 || tab.getPosition() == 2)
                {
                    secondButton.setVisible(true);
                    secondButton.setIcon(R.drawable.plusbutton);
                }

                else if(tab.getPosition() == 3)
                {
                    secondButton.setVisible(true);
                    secondButton.setIcon(R.drawable.editicon);
                }

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                toolbar.setTitle("Hello there, " + name+'!');

                myRef.child("presence").onDisconnect().setValue(false);
                myRef.child("presence").setValue(true);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    @Override
    protected void onResume() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                toolbar.setTitle("Hello there, " + name+'!');
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        super.onResume();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    @Override
    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
        super.onTopResumedActivityChanged(isTopResumedActivity);
    }

    private void goToLogin()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

}