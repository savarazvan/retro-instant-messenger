package com.example.ressenger;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapterChatRoom extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    PagerAdapterChatRoom(FragmentManager fm, int tabs)
    {
        super(fm);
        this.numberOfTabs=tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //if(position==0)
        return new ChatRoomFragment();
        //TODO: ADD GROUPCHAT DETAILS TAB
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
