package com.example.ressenger;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatFragment extends Fragment {

    static int preferredFont = 0, textSize = 0;
    static String color = "#000000";
    private TextInputEditText textBoxText;
    private DatabaseReference messagesRef;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;


    public ChatFragment() {}
    private ValueEventListener retrieveMessages = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("conversations/"+ChatActivity.conversation+"/participants");
            ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(true);
            ref.child(ChatActivity.uid).setValue(true);
            //-------------------------------------------------------------------
            textBoxText.setHint("Write " + ChatActivity.name + " a message");
            List<Message> mList = new ArrayList<>();
            mList.clear();
            String myUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            for(DataSnapshot ds : dataSnapshot.getChildren())
            {
                Message newMessage = ds.getValue(Message.class);
                assert newMessage != null;
                final boolean sent = newMessage.getReceiver().equals(ChatActivity.uid) && newMessage.getSender().equals(myUid);
                newMessage.setSender(sent ? "Me:" : ChatActivity.name + " says:");
                newMessage.setContent(newMessage.getContent());
                mList.add(newMessage);
            }

            messageAdapter = new MessageAdapter(ChatFragment.this.getActivity(), mList);
            recyclerView.setAdapter(messageAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void sendMessage(String message)
    {
        if(message==null)
            return;

        Map<String, Object> map = new HashMap<>();
        String tempKey = messagesRef.push().getKey();
        messagesRef.updateChildren(map);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);
        assert tempKey != null;
        DatabaseReference messageRef = messagesRef.child(tempKey);
        //----------------------------------------------------------
        Map<String, Object> map2 = new HashMap<>();
        map2.put("content", message);
        map2.put("sender", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        map2.put("receiver", ChatActivity.uid);
        map2.put("date", currentDate);
        map2.put("font", preferredFont);
        map2.put("size", textSize);
        map2.put("color", color);
        messageRef.updateChildren(map2);
    }

    @SuppressLint("GetInstance")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ImageButton sendButton = rootView.findViewById(R.id.sendButton);
        textBoxText = rootView.findViewById(R.id.textBoxText);
        recyclerView = rootView.findViewById(R.id.messagesScroll);
        ImageButton fontButton = rootView.findViewById(R.id.fontButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageSent = Objects.requireNonNull(textBoxText.getText()).toString().trim();
                sendMessage(messageSent);
                textBoxText.setText("");
            }
        });

        fontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontBottomSheet bottomSheet = new FontBottomSheet(ChatFragment.this.getActivity());
                assert getFragmentManager() != null;
                bottomSheet.show(getFragmentManager(), "sampleBottomSheet");
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatFragment.this.getActivity()));
        messagesRef = FirebaseDatabase.getInstance().getReference("conversations/" + ChatActivity.conversation + "/messages");
        messagesRef.addValueEventListener(retrieveMessages);
        return rootView;

    }

}

