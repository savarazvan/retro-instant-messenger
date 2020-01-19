package com.example.ressenger;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int numberOfTabs;

    public PagerAdapter(FragmentManager fm, int tabs)
    {
        super(fm);
        this.numberOfTabs=tabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0: {
                RecentsFragment recentsFragment = new RecentsFragment();
                return recentsFragment;
            }
            case 1:
            {
                PeopleFragment peopleFragment = new PeopleFragment();
                return peopleFragment;
            }
            case 2:
            {
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            }
            case 3:
            {
                ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            }

            case 4:
            {
                AddFriendsFragment addFriendsFragment = new AddFriendsFragment();
                return addFriendsFragment;
            }

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
