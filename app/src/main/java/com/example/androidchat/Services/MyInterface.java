package com.example.androidchat.Services;

public interface MyInterface {

    // for login
    void register();
    void login(String name, String email, String created_at);
    void logout();
    void ggetAll();
}
