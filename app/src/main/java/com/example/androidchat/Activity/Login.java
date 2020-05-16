package com.example.androidchat.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidchat.Model.User;
import com.example.androidchat.Model.response;
import com.example.androidchat.R;
import com.example.androidchat.Services.ChatService;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.androidchat.Constants.Constant.baseUrl.BASE_URL;
import static com.example.androidchat.Services.RetrofitClient.getApiClient;

public class Login extends AppCompatActivity {
    @BindView(R.id.username1)
    TextInputEditText username;
    @BindView(R.id.password1)
    TextInputEditText password;
    @BindView(R.id.textView1)
    TextView register;
    @BindView(R.id.log)
    Button log;
    String Nickname;
    public static String suid;
    private Socket socket;
    Intent mServiceIntent;
    private ChatService chatService;
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = password.getText().toString();
                String name = username.getText().toString();

                if (!pass.isEmpty() && !name.isEmpty()) {
                    getUser(name, pass);
                 /*  User usr= .get(0);
                   ;*/

                } else {


                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Register.class);

                //retreive nickname from textview and add it to intent extra
                // i.putExtra(NICKNAME, nickname.getText().toString());
                //i.putExtra("suid",uid);
                startActivity(i);
            }
        });
    }

    public List<User> getUser(String name, String pass) {

        Call<response> userlist = getApiClient(BASE_URL).getUsers(name,pass);
        userlist.enqueue(new Callback<response>() {
            @Override
            public void onResponse(Call<response> call, Response<response> response) {
                users = response.body().getUser();
                if (response.body().getSuccess().equals("success")) {
                    System.out.println("zzzzzzzzzzzzzzzzzzzzzzz" + users.get(0).getUid());
                    Connect(users.get(0).getUid());
                    Intent i = new Intent(Login.this, ContactList.class);
                    //retreive nickname from textview and add it to intent extra
                    i.putExtra("NICKNAME", users.get(0).getName());
                    suid=users.get(0).getUid();
                    i.putExtra("suid", suid);
                    startActivity(i);
                    startChatService();

                } else {
                    Toast.makeText(getBaseContext(), response.body().getSuccess() + "", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<response> call, Throwable t) {
                // Toast.makeText(c, "Couldn't find users", Toast.LENGTH_SHORT).show();

            }


        });
        return users;
    }

    public void Connect(String uid) {
        try {
            IO.Options options = new IO.Options();
            options.port = 8082;
            socket = IO.socket(BASE_URL);

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
            Nickname = username.getText().toString();
            socket.emit("join", Nickname,uid);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e + "", Toast.LENGTH_LONG)
                    .show();

        }
    }
    void  startChatService(){
        chatService = new ChatService();
        mServiceIntent = new Intent(this, ChatService.class);
        if (!isServiceRunning(ChatService.class)) {
            startService(mServiceIntent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }
}
