package com.example.androidchat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidchat.Model.LoginResponse;
import com.example.androidchat.Model.User;
import com.example.androidchat.Model.response;
import com.example.androidchat.R;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.androidchat.Constants.Constant.baseUrl.BASE_URL;
import static com.example.androidchat.Services.RetrofitClient.getApiClient;

public class Register extends AppCompatActivity implements Validator.ValidationListener {
    private Validator validator;
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    List<User> users = new ArrayList<>();
    @NotEmpty
    @Length(min = 3, max = 10)
    TextInputEditText username;

    TextInputEditText password;

    TextInputEditText npassword;
    Button register;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //ButterKnife.bind(this);
        username = findViewById(R.id.username1);
        password = findViewById(R.id.password1);
        npassword = findViewById(R.id.npassword);

        register = findViewById(R.id.register);
        validator = new Validator(this);
        validator.setValidationListener(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();

            }
        });

    }

    void addUser() {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        String uname = username.getText().toString();
        String pass = password.getText().toString();
        String uid = generateNewToken();
        if (getUser(uid) != null) {
            User u = new User();
            u.setName(uname);
            u.setPassword(pass);
            u.setUid(uid);
            createUser(u);
            back();

        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

    }

    public List<User> getUser(String uid) {

        Call<response> userlist = getApiClient(BASE_URL).getUsers(uid);
        userlist.enqueue(new Callback<response>() {
            @Override
            public void onResponse(Call<response> call, Response<response> response) {

                users = response.body().getUser();


                // Toast.makeText(c, response.headers().toString()+"zzz", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<response> call, Throwable t) {
                // Toast.makeText(c, "Couldn't find users", Toast.LENGTH_SHORT).show();

            }


        });
        return users;
    }

    public String createUser(User user) {
        final String[] s = new String[1];
        Call<LoginResponse> resp = getApiClient(BASE_URL).createUser(user);
        resp.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                s[0] = response.body().getMessage();

                System.out.println(user.getName() + "" + user.getPassword() + "/" + user.getUid() + "//" + s[0].toString());
                // Toast.makeText(c, response.headers().toString()+"zzz", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Toast.makeText(c, "Couldn't find users", Toast.LENGTH_SHORT).show();

            }


        });
        return s[0];
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static boolean isValidPassword(final String password) {
        final String a = "((?=.*[a-z]).{5,16})";
        return password.matches(a);

    }
    void back(){
        Intent i = new Intent(Register.this, Login.class);
        startActivity(i);

    }
}
