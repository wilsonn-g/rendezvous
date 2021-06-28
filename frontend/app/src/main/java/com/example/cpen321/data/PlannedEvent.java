package com.example.cpen321.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PlannedEvent {

    @SerializedName("placeid")
    @Expose
    private String placeId;

    @SerializedName("datetime")
    @Expose
    private long dateTime; //unix time

    @SerializedName("groupname")
    @Expose
    private String groupName;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTime() {
        Date date = new Date(getDateTime());
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(date);
        return  datetime;
    }
}
