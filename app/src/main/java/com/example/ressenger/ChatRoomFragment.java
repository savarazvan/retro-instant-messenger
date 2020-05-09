package com.example.ressenger;

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
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class ChatRoomFragment extends Fragment {

    static int preferredFont = 0, textSize = 0, color = Color.parseColor("#000000");
    private TextInputEditText textBoxText;
    private DatabaseReference messagesRef;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    private ValueEventListener retrieveMessages = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //-------------------------------------------------------------------
            textBoxText.setHint("Write a message to " + ChatRoomActivity.title);
            final List<Message> mList = new ArrayList<>();
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                final Message newMessage = ds.getValue(Message.class);
                final boolean sent = newMessage.getSender().equals(myUid);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + newMessage.getSender());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        if(!sent) newMessage.setSender(ds.child("name").getValue(String.class) + " says:");
                        else newMessage.setSender("Me:");
                        newMessage.setContent(newMessage.getContent());
                        mList.add(newMessage);

                        if(messageAdapter!=null)
                            messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            messageAdapter = new MessageAdapter(ChatRoomFragment.this.getActivity(), mList);
            recyclerView.setAdapter(messageAdapter);
            }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {}
    };
    public ChatRoomFragment() {}

    private void sendMessage(String message)
    {
        if(message==null)
            return;

        Map<String, Object> map = new HashMap<String, Object>();
        String tempKey = messagesRef.push().getKey();
        messagesRef.updateChildren(map);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);
        DatabaseReference messageRef = messagesRef.child(tempKey);
        //----------------------------------------------------------
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("content", message);
        map2.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map2.put("date", currentDate);
        map2.put("font", preferredFont);
        map2.put("size", textSize);
        map2.put("color", color);
        messageRef.updateChildren(map2);
    }

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
                String messageSent = textBoxText.getText().toString().trim();
                sendMessage(messageSent);
                textBoxText.setText("");
            }
        });

        fontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FontBottomSheet bottomSheet = new FontBottomSheet(ChatRoomFragment.this.getActivity());
                bottomSheet.show(getFragmentManager(), "sampleBottomSheet");
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatRoomFragment.this.getActivity()));
        messagesRef = FirebaseDatabase.getInstance().getReference("conversations/" + ChatRoomActivity.id + "/messages");
        messagesRef.addValueEventListener(retrieveMessages);
        return rootView;

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}

