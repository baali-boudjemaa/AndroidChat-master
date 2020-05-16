package com.example.androidchat.Model;

import com.example.androidchat.Model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class response {

    @SerializedName("user")
    @Expose
    private List<User> user = null;
    @SerializedName("success")
    @Expose
    private String success;

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

}