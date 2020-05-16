package com.example.androidchat.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidchat.Adapters.ChatBoxAdapter;
import com.example.androidchat.Adapters.ContactListAdapter;
import com.example.androidchat.Model.Message;
import com.example.androidchat.Model.User;
import com.example.androidchat.Model.response;
import com.example.androidchat.R;
import com.example.androidchat.utils.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.androidchat.Constants.Constant.baseUrl.BASE_URL;
import static com.example.androidchat.Services.RetrofitClient.getApiClient;

public class ContactList extends AppCompatActivity {
    public RecyclerView myRecylerView ;
    public List<User> users ;
    public ContactListAdapter contactAdapter;
    String suid;
    String nikname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactlist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        suid=(String)getIntent().getExtras().getString("suid");
        nikname=(String)getIntent().getExtras().getString("NICKNAME");
        System.out.println("1111111111111111111111111111"+suid);
        myRecylerView=findViewById(R.id.recyc);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        myRecylerView.addItemDecoration(new SpacesItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        contactAdapter=new ContactListAdapter(this,nikname,suid);
        getUsers();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.men,menu);
        final MenuItem menuItem=menu.findItem(R.id.action_search);
        return false;
    }
    public List<User> getUsers() {

        Call<response> userlist = getApiClient(BASE_URL).getUsers(suid);
        userlist.enqueue(new Callback<response>() {
            @Override
            public void onResponse(Call<response> call, Response<response> response) {

                assert response.body() != null;
                users = response.body().getUser();
                contactAdapter.setUsers(users);
                contactAdapter.notifyDataSetChanged();
                myRecylerView.setAdapter(contactAdapter);

            }

            @Override
            public void onFailure(Call<response> call, Throwable t) {
                // Toast.makeText(c, "Couldn't find users", Toast.LENGTH_SHORT).show();

            }


        });
        return users;
    }
}
