package com.example.ressenger;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapterChat extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    PagerAdapterChat(FragmentManager fm, int tabs)
    {
        super(fm);
        this.numberOfTabs=tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            default: return new ChatFragment();
            case 1: return new ProfileBuddyFragment();
            case 2: return new OptionsBuddyFragment();
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
