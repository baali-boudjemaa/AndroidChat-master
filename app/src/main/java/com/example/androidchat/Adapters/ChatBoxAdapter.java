package com.example.androidchat.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidchat.Model.Message;
import com.example.androidchat.R;

import java.util.ArrayList;
import java.util.List;


public class ChatBoxAdapter extends RecyclerView.Adapter<ChatBoxAdapter.MyViewHolder> {
    private List<Message> MessageList = new ArrayList<>();
    String suid, euid;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public TextView message;
        public LinearLayout lin;


        public MyViewHolder(View view) {
            super(view);

            nickname = (TextView) view.findViewById(R.id.text_message_name);
            message = (TextView) view.findViewById(R.id.text_message_body);


        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = MessageList.get(position);

        if (message.getSuid().equals(suid)) {
            System.out.println("MESSAGE_SENT" + message.getSuid() + "//" + euid);
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else if(message.getSuid().equals(euid)) {
            System.out.println("MESSAGE_RECEIVED" + message.getEuid() + "//" + euid);
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }else {
            return VIEW_TYPE_MESSAGE_SENT;
        }

    }


    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    @Override
    public ChatBoxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
        }

        return new ChatBoxAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatBoxAdapter.MyViewHolder holder, final int position) {
        final Message m = MessageList.get(position);
        holder.nickname.setText(m.getNickname());

        holder.message.setText(m.getMessage());


    }

    public void setMessageList(List<Message> messages) {
        MessageList = messages;
    }

    public void setChatUid(String suid, String euid) {
        this.suid = suid;
        this.euid = euid;
    }

    public void addItem(Message message) {
        MessageList.add(message);

        notifyItemInserted(getItemCount()+1);
    }
}