package com.example.ressenger;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddPeopleBottomSheet extends BottomSheetDialogFragment implements AdapterView.OnItemSelectedListener {

    Context mContext;
    List<UserDetails> selectedUsers = new ArrayList<>();
    private BottomSheetListener listener;

    public AddPeopleBottomSheet(Context context)
    {
        mContext=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.layout_add_people_bottomsheet, container, false);
        final TextInputLayout groupTitle = v.findViewById(R.id.groupTitle);
        final Button okButton = v.findViewById(R.id.okButtonAddPeople);
        final RecyclerView addPeopleRecycler=v.findViewById(R.id.addPeopleRecyclerBottomsheet);
        final DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference friendsRef=FirebaseDatabase.getInstance().getReference("friends/"+ FirebaseAuth.getInstance().getUid());
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> friends = new ArrayList<String>();

                for(DataSnapshot ds : dataSnapshot.getChildren())
                    friends.add(ds.getKey());

                usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<UserDetails> userDetails = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            if(!friends.contains(ds.getKey())) continue;
                            userDetails.add(ds.getValue(UserDetails.class));
                        }
                        addPeopleRecycler.setAdapter(new AddPeopleAdapter(mContext, userDetails, new AddPeopleAdapter.OnItemCheckListener() {
                            @Override
                            public void onItemCheck(UserDetails user) {
                                selectedUsers.add(user);}

                            @Override
                            public void onItemUncheck(UserDetails user) {
                                selectedUsers.remove(user);}
                        }));
                        addPeopleRecycler.setLayoutManager(new LinearLayoutManager(mContext));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = groupTitle.getEditText().getText().toString().trim();

                if(!title.equals(""))
                    for (UserDetails selectedUser :selectedUsers)
                        FirebaseDatabase.getInstance().getReference("people/"+FirebaseAuth.getInstance().getUid()).child(title + '/' + selectedUser.getUid()).setValue("");

                dismiss();
            }
        });

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(mContext);
    }

    public interface BottomSheetListener
    {

    }

}
