package com.example.cpen321.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ChatMessage {
    @SerializedName("senderName")
    @Expose
    private String name;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("timeStamp")
    @Expose
    private Date timeStamp;


    public ChatMessage(String name, String message, Date timeStamp) {
        this.name = name;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getName() { return name; }

    public String getMessage() {
        return message;
    }

    public Date getTimeStamp() { return timeStamp; }


}
