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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class ChatRoomFragment extends Fragment {

    static int preferredFont = 0, textSize = 0, color = Color.parseColor("#000000");
    private TextInputEditText textBoxText;
    private DatabaseReference messagesRef;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private byte[] encryptionKey = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    private ValueEventListener retrieveMessages = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //-------------------------------------------------------------------
            textBoxText.setHint("Write a message to " + ChatRoomActivity.name);
            List<Message> mList = new ArrayList<>();
            mList.clear();
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            for(DataSnapshot ds : dataSnapshot.getChildren())
            {
                Message newMessage = ds.getValue(Message.class);
                final boolean sent = newMessage.getSender().equals(myUid);
                final String[] sender = new String[1];
                if(!sent)
                {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+newMessage.getSender());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ds) {
                            sender[0] = ds.child("name").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}});
                }
                newMessage.setSender(sent ? "Me:" : sender[0] + " says:");
                try {
                    newMessage.setContent(AESDecryptionMethod(newMessage.getContent()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mList.add(newMessage);
            }

            messageAdapter = new MessageAdapter(ChatRoomFragment.this.getActivity(), mList);
            recyclerView.setAdapter(messageAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
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
        map2.put("content", AESEncryptionMethod(message));
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

        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");

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
        messagesRef = FirebaseDatabase.getInstance().getReference("conversations/" + ChatActivity.conversation + "/messages");
        messagesRef.addValueEventListener(retrieveMessages);
        return rootView;

    }

    public String AESEncryptionMethod(String message)
    {
        byte[] messageBytes = message.getBytes();
        byte[] encryptedBytes = new byte[messageBytes.length];

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedBytes = cipher.doFinal(messageBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String returnedMessage = null;

        returnedMessage = new String(encryptedBytes, StandardCharsets.ISO_8859_1);

        return  returnedMessage;

    }

    public String AESDecryptionMethod(String message) throws UnsupportedEncodingException {

        byte[] encryptedBytes = message.getBytes(StandardCharsets.ISO_8859_1);
        String decryptedMessage = message;

        byte[] decryption;

        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryption = decipher.doFinal(encryptedBytes);
            decryptedMessage = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return decryptedMessage;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}

