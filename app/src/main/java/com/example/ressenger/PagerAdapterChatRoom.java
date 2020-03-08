package com.example.ressenger;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapterChatRoom extends FragmentStatePagerAdapter {

    int numberOfTabs;

    public PagerAdapterChatRoom(FragmentManager fm, int tabs)
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
                //TODO: ADD GROUPCHAT DETAILS TAB
            }

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
