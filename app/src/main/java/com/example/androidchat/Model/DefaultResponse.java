package com.example.androidchat.Model;

public class DefaultResponse {

   // @SerializedName("success")
    private String err;

 //   @SerializedName("message")
    private String msg;

    public DefaultResponse(String err, String msg) {
        this.err = err;
        this.msg = msg;
    }

    public String isErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }
}
