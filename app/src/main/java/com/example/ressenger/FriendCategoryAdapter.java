package com.example.ressenger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ressenger.AddFriendsFragment.FindPeopleViewHolder.FindPeopleCategoryViewHolder;
import com.example.ressenger.FriendCategory.CategoryViewHolder;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

public class FriendCategoryAdapter extends ExpandableRecyclerViewAdapter<CategoryViewHolder, FindPeopleCategoryViewHolder> {

    public FriendCategoryAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public FindPeopleCategoryViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_buddy, parent, false);
        return new FindPeopleCategoryViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(FindPeopleCategoryViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final UserDetails user = ((FriendCategory) group).getItems().get(childIndex);
        holder.setUsername(user.getName());
        holder.setStatus(user.getStatus());
    }

    @Override
    public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition,
                                      ExpandableGroup group) {

        holder.setCategoryTitle(group);
    }
}