package com.example.androidchat.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.androidchat.Model.Message;
import com.example.androidchat.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Transport;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.androidchat.Activity.Login.suid;

public class ChatService extends Service {
    public int counter = 0;
    private Socket socket;

    @Override
    public void onCreate() {
        super.onCreate();
        //if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)





       // connect();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        }else   startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        connect();
       // startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
         broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }


    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Count", "=========  " + (counter++));
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    void connect(){
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
                Toast.makeText(this, "failed", Toast.LENGTH_LONG)
                        .show();
            }
            socket.emit("join", "Nickname");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e + "", Toast.LENGTH_LONG)
                    .show();

        }
        socket.on("az", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                String data = (String) args[0];
                System.out.println("eeeeeeeeeee"+data);
                showNotification(ChatService.this,"aaaa");
                //Toast.makeText(MainActivity.this, data, Toast.LENGTH_LONG).show();

            }
        });
        socket.on(suid, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {


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
                            showNotification(getBaseContext(), message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }



        });

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

}