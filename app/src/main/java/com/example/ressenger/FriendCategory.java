package com.example.ressenger;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

public class FriendCategory extends ExpandableGroup<UserDetails> {

    public FriendCategory(String title, List<UserDetails> items) {
        super(title, items);
    }

    public static class CategoryViewHolder extends GroupViewHolder
    {
        private TextView categoryTitle;
        private ImageView downArrow, check;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.list_item_genre_name);
            downArrow = itemView.findViewById(R.id.list_item_genre_arrow);
    }

    public void setCategoryTitle(ExpandableGroup group) {
        categoryTitle.setText(group.getTitle());
    }

    }


}