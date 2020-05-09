package com.example.ressenger;


import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    PagerAdapter(FragmentManager fm, int tabs)
    {
        super(fm);
        this.numberOfTabs=tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new RecentsFragment();
            case 1: return new PeopleFragment();
            case 2: return new ChatRoomsFragment();
            case 3: return new ProfileFragment();
            case 4: return new AddFriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
