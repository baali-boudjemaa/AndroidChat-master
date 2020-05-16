package com.example.androidchat.Services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.androidchat.Model.User;
import com.example.androidchat.Model.response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.androidchat.Constants.Constant.baseUrl.BASE_URL;


public class RetrofitClient {

    private static RetrofitClient instance = null;

    private ResultReadyCallback callback;

    private static final String BASE_URL = "http://192.168.43.51:8082";
    private ServiceApi service;

    boolean success = false;
     List<User> users=new ArrayList<>();
    public RetrofitClient() {



    }

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();


    public static ServiceApi getApiClient(String baseUrl) {
        Retrofit retrofit;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();

                                Request.Builder requestBuilder = original.newBuilder()

                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        }
                ).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();


        Log.e("RetrofitClient", baseUrl);
        return retrofit.create(ServiceApi.class);

    }


    interface ResultReadyCallback {
        public void resultReady(List<User> users);
    }



    public void setCallback(ResultReadyCallback callback) {
        this.callback = callback;
    }


    public void connect(final Context c) {
        Call a = getApiClient(BASE_URL).connect();
        a.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(c, "Couldn't create user", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
}
