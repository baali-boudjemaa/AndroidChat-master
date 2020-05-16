package com.example.androidchat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidchat.Activity.ContactList;
import com.example.androidchat.Activity.MainActivity;
import com.example.androidchat.Model.Message;
import com.example.androidchat.Model.User;
import com.example.androidchat.R;

import de.hdodenhof.circleimageview.CircleImageView;


import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {
    private List<User> users;
    Context cc;
    String suid;
    String nikname;
    @NonNull
    @Override
    public ContactListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemcontact, parent, false);
        return new ContactListAdapter.MyViewHolder(itemView);
    }

    public ContactListAdapter(Context c, String nikname,String suid) {
        cc = c;
        this.suid = suid;
        this.nikname=nikname;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListAdapter.MyViewHolder holder, final int position) {

        final String niknams = users.get(position).getName();
        holder.nickname.setText(niknams);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String euid = users.get(position).getUid();
                Intent i = new Intent(cc, com.example.androidchat.Activity.ChatBoxActivity.class);
                //retreive nickname from textview and add it to intent extra
                i.putExtra("NICKNAME", niknams);
                i.putExtra("NICKNAMS",nikname);
                i.putExtra("euid", euid);
                i.putExtra("suid", suid);
               System.out.println("22222222222222222222222222//"+suid+"22222222222222222222//"+euid);
                i.putExtra("index",2);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cc.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname;
        public CircleImageView pic;

        public MyViewHolder(View view) {
            super(view);
            nickname = view.findViewById(R.id.name);
            pic = view.findViewById(R.id.imageView);


        }
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
