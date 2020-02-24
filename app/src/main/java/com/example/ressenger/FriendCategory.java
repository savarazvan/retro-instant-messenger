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
            check = null;// itemView.findViewById(R.id.list_item_genre_check);

            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    categoryTitle.setEnabled(true);
                    downArrow.setVisibility(View.GONE);
                    check.setVisibility(View.VISIBLE);
                    return false;
                }
            };

            View.OnClickListener checkListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downArrow.setVisibility(View.VISIBLE);
                    check.setVisibility(View.GONE);
                    String input = categoryTitle.getText().toString().trim();
                    if (input.equals(categoryTitle.getHint().toString()))
                        return;
                    categoryTitle.setText("");
                    categoryTitle.setHint(input);
                }
            };
    }

    public void setCategoryTitle(ExpandableGroup group) {
        categoryTitle.setHint(group.getTitle());
    }


    }


}