package com.example.androidchat.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Aymen on 08/06/2018.
 */

public class Message {

    private String nickname;

    @SerializedName("message")
    @Expose
    private String message;

    private boolean msg;



    @SerializedName("suid")
    @Expose
    private String suid;
    @SerializedName("euid")
    @Expose
    private String euid;





    public String getSuid() {
        return suid;
    }

    public void setSuid(String suid) {
        this.suid = suid;
    }

    public String getEuid() {
        return euid;
    }

    public void setEuid(String euid) {
        this.euid = euid;
    }



    public Message() {

    }



    public Message(String nickname, String message, String suid,String euid) {
        this.setNickname(nickname);
        this.setMessage(message);
        this.suid=suid;
        this.euid=euid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSent() {
        return msg;
    }

    public void setSent(boolean msg) {
        this.msg = msg;
    }
}
