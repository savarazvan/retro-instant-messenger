package com.example.ressenger;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapterChat extends FragmentStatePagerAdapter {

    int numberOfTabs;

    public PagerAdapterChat(FragmentManager fm, int tabs)
    {
        super(fm);
        this.numberOfTabs=tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
            {
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            }

            case 1:
            {
                ProfileBuddyFragment profileBuddyFragment = new ProfileBuddyFragment();
                return profileBuddyFragment;
            }

            case 2:
            {
                OptionsBuddyFragment optionsBuddyFragment = new OptionsBuddyFragment();
                return optionsBuddyFragment;
            }

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
