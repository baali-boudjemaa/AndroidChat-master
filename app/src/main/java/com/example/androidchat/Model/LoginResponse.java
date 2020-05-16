package com.example.androidchat.Model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("response")
    private String message=" " ;


    public LoginResponse(boolean error, String message) {
        this.setError(error);
        this.setMessage(message);

    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
