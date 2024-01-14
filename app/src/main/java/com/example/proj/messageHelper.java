package com.example.proj;

public class messageHelper {
    String msg, senderID;


    public messageHelper(String msg, String senderID) {
        this.msg = msg;
        this.senderID = senderID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

}
