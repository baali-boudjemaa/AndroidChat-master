package com.example.androidchat.Services;

import com.example.androidchat.Model.DefaultResponse;
import com.example.androidchat.Model.LoginResponse;
import com.example.androidchat.Model.Message;
import com.example.androidchat.Model.MessageResponse;
import com.example.androidchat.Model.User;
import com.example.androidchat.Model.response;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ServiceApi {

    @GET("register.php")
    Call<User> doRegistration(@Query("name") String name, @Query("email") String email, @Query("phone") String phone, @Query("password") String password);

    @GET("register.php")
    Call<DefaultResponse> Register(@Query("name") String name, @Query("email") String email, @Query("phone") String phone, @Query("password") String password);

    @GET("login")
    Call<LoginResponse> doLogin(@Query("name") String name, @Query("password") String password);

    @GET("/users")
    Call<response> getUsers(@Query("suid") String suid);
    @GET("/user")
    Call<response> getUsers(@Query("name") String name,@Query("pass") String pass);

    @GET("/user")
    Call<response> getUser(@Query("suid") String suid);

    @GET("/AllUsers")
    Call<response> getAllUsers();
    @GET("/users")
    Call<List<User>> getsers();

    @POST("/add")
    Call <LoginResponse> createUser(@Body User user);
    @POST("/ap")
    Call connect();

    @GET("/msgs")
    Call<MessageResponse> getMessages(@Query("suid") String suid, @Query("euid") String euid);
}
