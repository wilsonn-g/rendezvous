
package com.example.cpen321.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VotingEvent {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("membersTotal")
    @Expose
    private List<String> membersVoted;
    @SerializedName("votesYes")
    @Expose
    private int votesYes;
    @SerializedName("votesNo")
    @Expose
    private int votesNo;

    @SerializedName("placeName")
    @Expose
    private String placeName;
    @SerializedName("placeid")
    @Expose
    private String placeid;
    @SerializedName("senderId")
    @Expose
    private String senderId;
    @SerializedName("senderName")
    @Expose
    private String senderName;
    @SerializedName("datetime")
    @Expose
    private long datetime;

    @SerializedName("photo")
    @Expose
    private String photo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VotingEvent withId(String id) {
        this.id = id;
        return this;
    }

    public List<String> getMembersVoted() {
        return membersVoted;
    }

    public void setMembersVoted(List<String> membersVoted) {
        this.membersVoted = membersVoted;
    }

    public VotingEvent withMembersYes(List<String> membersYes) {
        this.membersVoted = membersYes;
        return this;
    }

    public VotingEvent withName(String name) {
        this.placeName = name;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getVotesYes() {
        return votesYes;
    }

    public void setVotesYes(int votesYes) {
        this.votesYes = votesYes;
    }

    public int getVotesNo() {
        return votesNo;
    }

    public void setVotesNo(int votesNo) {
        this.votesNo = votesNo;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
