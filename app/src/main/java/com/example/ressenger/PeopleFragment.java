package com.example.ressenger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


public class PeopleFragment extends Fragment {

    private ExpandableListView expandableListView;

    public PeopleFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_people, container, false);

        expandableListView = rootView.findViewById(R.id.expandableListView);
        ExpandableListViewAdapter adapter = new ExpandableListViewAdapter();
        expandableListView.setAdapter(adapter);

        return rootView;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

        String[] groupNames = {"Buddies", "Family", "Work"};
        String[][] childNames = {{"Popescu", "Vladescu", "Ionescu"}, {"Mama","Tata", "Cumnatu"}, {"Bossu", "Colegu", "Atat"}};
        @Override
        public int getGroupCount() {
            return groupNames.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childNames[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupNames[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childNames[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(PeopleFragment.this.getActivity());
            Typeface typeface = ResourcesCompat.getFont(PeopleFragment.this.getActivity(), R.font.times_new_roman_bold_italic);
            textView.setTextSize(15);
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(typeface);
            String text = groupNames[groupPosition] + " (" + getChildrenCount(groupPosition)+')';
            textView.setText(text);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) PeopleFragment.this.getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.layout_buddy, null);
            }
            TextView buddyName =  convertView.findViewById(R.id.Name);
            buddyName.setText(childNames[groupPosition][childPosition]);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}



