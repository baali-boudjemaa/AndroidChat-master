package com.example.androidchat.Activity;

import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidchat.Model.User;
import com.example.androidchat.Model.response;
import com.example.androidchat.R;
import com.example.androidchat.Services.RetrofitClient;
import com.example.androidchat.Services.ServiceApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.androidchat.Constants.Constant.baseUrl.BASE_URL;
import static com.example.androidchat.Services.RetrofitClient.getApiClient;


public class MainActivity extends AppCompatActivity {
    List<String> user;
    private Button btn;
    private EditText nickname;
    public static final String NICKNAME = "NICKNAME";
    List<User> users = new ArrayList<>();
    Spinner spin;
    ArrayAdapter<String> adapter;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spin = (Spinner) findViewById(R.id.spinner);

        //call UI component  by id
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println( "11111111111111111111111111111111111");
                nickname.setText(spin.getSelectedItem().toString()+"");
                uid=users.get(position).getUid();
                System.out.println(uid + "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn = (Button) findViewById(R.id.enterchat);
        nickname = (EditText) findViewById(R.id.nickname);

        RetrofitClient retrofitClient = new RetrofitClient();
        //retrofitClient.connect(getApplicationContext());
        users = getUsers();

//        System.out.println(users.get(2).getName() + "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the nickname is not empty go to chatbox activity and add the nickname to the intent extra
                if (!nickname.getText().toString().isEmpty()) {
                    Intent i = new Intent(MainActivity.this, ContactList.class);
                    //retreive nickname from textview and add it to intent extra
                    i.putExtra(NICKNAME, nickname.getText().toString());
                    i.putExtra("suid",uid);
                    startActivity(i);
                }
            }
        });

    }

    public List<User> getUsers() {

        Call<response> userlist = getApiClient(BASE_URL).getAllUsers();
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

}
