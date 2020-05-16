package com.example.androidchat.Activity;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidchat.Adapters.ChatBoxAdapter;
import com.example.androidchat.Model.Message;
import com.example.androidchat.Model.MessageResponse;
import com.example.androidchat.Model.User;
import com.example.androidchat.Model.response;
import com.example.androidchat.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.androidchat.Constants.Constant.baseUrl.BASE_URL;
import static com.example.androidchat.Services.RetrofitClient.getApiClient;

public class ChatBoxActivity extends AppCompatActivity {
    public RecyclerView myRecylerView;
    public List<Message> MessageList;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messagetxt;
    public Button send;
    //declare socket object
    private Socket socket;
    public String Nickname,Nicknams;
    List<User> users = new ArrayList<>();
    Spinner spin;
    ArrayAdapter<String> adapter;
    List<String> user;
    String suid;
    String euid;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        spin = (Spinner) findViewById(R.id.spinner);
        messagetxt = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);
        Nickname = (String) getIntent().getExtras().getString("NICKNAME");
        Nicknams = (String) getIntent().getExtras().getString("NICKNAMS");

        suid = (String) getIntent().getExtras().getString("suid");
        euid = (String) getIntent().getExtras().getString("euid");
        System.out.println("22222222222222222222222222//"+suid+"22222222222222222222//"+euid);
        //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        chatBoxAdapter = new ChatBoxAdapter();
        chatBoxAdapter.setMessageList(MessageList);
        myRecylerView.setAdapter(chatBoxAdapter);

        getUsers();

        getMessageList();
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             //   euid = users.get(position).getUid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // get the nickame of the user

        // position=Integer.parseInt(getIntent().getExtras().getString("index"));

        //connect you socket client to the server
        try {
            IO.Options options = new IO.Options();
            options.port = 8082;
            socket = IO.socket("http://192.168.43.51:8082");

            socket.connect();
            socket.on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Transport transport = (Transport) args[0];
                    transport.on(Transport.EVENT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Exception e = (Exception) args[0];
                            Log.e("TAG", "Transport error " + e);
                            e.printStackTrace();
                            e.getCause().printStackTrace();
                        }
                    });
                }
            });
            if (socket.connected()) {
                Toast.makeText(this, "conn", Toast.LENGTH_LONG)
                        .show();
            } else {

            }
           // socket.emit("join", Nickname);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e + "", Toast.LENGTH_LONG)
                    .show();

        }


        // message send action
        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                //retrieve the nickname and the message content and fire the event messagedetection
                if (!messagetxt.getText().toString().isEmpty()) {
                    socket.emit("123456", Nickname, messagetxt.getText().toString(), suid, euid,getDateTime());
                    Message m = new Message(Nickname, messagetxt.getText().toString(), suid,euid);


                    //add the message to the messageList

                   // MessageList.add(m);
                    chatBoxAdapter.addItem(m);

                    myRecylerView.smoothScrollToPosition(MessageList.size() - 1);
                    // notify the adapter to update the recycler view
                    chatBoxAdapter.setChatUid(suid,euid);


                    //set the adapter for the recycler view

                   // myRecylerView.setAdapter(chatBoxAdapter);
                    messagetxt.setText("");
                }


            }
        });

        //implementing socket listeners
        socket.on("az", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String data = (String) args[0];
                        if(data!=suid){
                            showNotification(getBaseContext(),""+data);
                            Toast.makeText(ChatBoxActivity.this, data, Toast.LENGTH_LONG).show();
                        }


                    }
                });
            }
        });
        socket.on("userdisconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];

                        Toast.makeText(ChatBoxActivity.this, data, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        socket.on(suid, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = null;
                        System.out.println(args[0] + "");
                        try {
                            data = new JSONObject((String) args[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            //extract data from fired event

                            String nickname = data.getString("senderNickname");
                            String message = data.getString("message");

                            // make instance of message

                            Message m = new Message(nickname, message,  euid,suid);
                            chatBoxAdapter.setChatUid(suid,euid);

                            //add the message to the messageList

                            MessageList.add(m);


                            myRecylerView.smoothScrollToPosition(MessageList.size() - 1);
                            // notify the adapter to update the recycler view

                            chatBoxAdapter.notifyDataSetChanged();

                            //set the adapter for the recycler view


                            showNotification(getBaseContext(), m.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
    }

    void showNotification(Context context, String msg) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01"; // We'll add Notification Channel Id using NotificationManager.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            //notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.qqq)
                .setContentTitle("DataFlair sent a notification")
                .setContentText("" + msg);
        notificationManager.notify(/*notification id*/1, notificationBuilder.build());

    }


    public List<User> getUsers() {

        Call<response> userlist = getApiClient(BASE_URL).getUsers(suid);
        userlist.enqueue(new Callback<response>() {
            @Override
            public void onResponse(Call<response> call, Response<response> response) {

                assert response.body() != null;
                users = response.body().getUser();
                user = new ArrayList<>();
                //System.out.println(response.body().getUser().get(3).getName() + "aaaaaaaaaaaaaaaaaaaaaaaaaaa");
                for (User s : users) {
                    System.out.println(s.getUid() + "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
                    user.add(s.getName());
                }


                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, user);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(adapter);
                // Toast.makeText(c, response.headers().toString()+"zzz", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<response> call, Throwable t) {
                // Toast.makeText(c, "Couldn't find users", Toast.LENGTH_SHORT).show();

            }


        });
        return users;
    }

    public List<Message> getMessageList() {

        Call<MessageResponse> userlist = getApiClient(BASE_URL).getMessages(suid, euid);
        userlist.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {

                assert response.body() != null;
                MessageList=response.body().getMessage();
                chatBoxAdapter = new ChatBoxAdapter();
                chatBoxAdapter.setChatUid(suid,euid);
                chatBoxAdapter.setMessageList(MessageList);

                chatBoxAdapter.notifyDataSetChanged();
                myRecylerView.setAdapter(chatBoxAdapter);
                myRecylerView.smoothScrollToPosition(MessageList.size() );

                // Toast.makeText(c, response.headers().toString()+"zzz", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                // Toast.makeText(c, "Couldn't find users", Toast.LENGTH_SHORT).show();

            }


        });
        return MessageList;

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getDateTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(

                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = new Date();

        return dateFormat.format(date);

    }
}
