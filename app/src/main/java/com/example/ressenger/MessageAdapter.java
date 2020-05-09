package com.example.ressenger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Type;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> message;

    public MessageAdapter(Context context, List<Message> message)
    {
        this.context = context;
        this.message = message;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_message, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message chat = message.get(position);
        int messageFont = chat.getFont();
        Typeface typeface;

        switch (messageFont)
        {
            case (0):{
                typeface = ResourcesCompat.getFont(context, R.font.abel);
                break;
            }
            case (1):
            {
                typeface = ResourcesCompat.getFont(context, R.font.the_girl_next_door);
                break;
            }

            default:
            {
                typeface = ResourcesCompat.getFont(context, R.font.times_new_roman);
                break;
            }

        }

        int textSize = chat.getSize(), newSize;

        switch (textSize)
        {
            case (1):
            {
                newSize = 18;
                break;
            }

            case (2):
            {
                newSize = 24;
                break;
            }

            case (3):
            {
                newSize = 36;
                break;
            }

            case(4):
            {
                newSize = 54;
                break;
            }

            default:
            {
                newSize = 12;
                break;
            }
        }

        String textColor = chat.getColor();

        holder.messageContent.setTextSize(newSize);
        holder.messageContent.setTypeface(typeface);
        holder.messageContent.setTextColor(Color.parseColor(textColor));
        holder.messageContent.setText(HtmlCompat.fromHtml(chat.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.senderName.setText(chat.getSender());
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderName;
        public TextView messageContent;
        public LinearLayout messageBackground;

        public ViewHolder(View itemView)
        {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            messageContent = itemView.findViewById(R.id.messageContent);
            messageBackground = itemView.findViewById(R.id.messageBackground);
        }
    }
}
