
package com.example.cpen321.data;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("members")
    @Expose
    private List<String> members = null;
    @SerializedName("interests")
    @Expose
    private List<String> interests = null;
    @SerializedName("likedPlaces")
    @Expose
    private List<String> likedPlaces = null;
    @SerializedName("lastMessage")
    @Expose
    private String lastMessage;
    @SerializedName("lastSender")
    @Expose
    private String lastSender;
    @SerializedName("groupname")
    @Expose
    private String groupName;
    @SerializedName("messages")
    @Expose
    private List<Message> messages;
    @SerializedName("votingEvents")
    @Expose
    private List<VotingEvent> votingEvents;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<VotingEvent> getVotingEvents() {
        return votingEvents;
    }

    public void setVotingEvents(List<VotingEvent> votingEvents) {
        this.votingEvents = votingEvents;
    }


    public Group() {
    }

    public Group(String id, List<String> members, List<String> interests, List<String> likedPlaces, String lastMessage, String lastSender, String groupName) {
        super();
        this.id = id;
        this.members = members;
        this.interests = interests;
        this.likedPlaces = likedPlaces;
        this.lastMessage = lastMessage;
        this.lastSender = lastSender;
        this.groupName = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Group withId(String id) {
        this.id = id;
        return this;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Group withMembers(List<String> members) {
        this.members = members;
        return this;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public Group withInterests(List<String> interests) {
        this.interests = interests;
        return this;
    }

    public List<String> getLikedPlaces() {
        return likedPlaces;
    }

    public void setLikedPlaces(List<String> likedPlaces) {
        this.likedPlaces = likedPlaces;
    }

    public Group withLikedPlaces(List<String> likedPlaces) {
        this.likedPlaces = likedPlaces;
        return this;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Group withLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public String getLastSender() {
        return lastSender;
    }

    public void setLastSender(String lastSender) {
        this.lastSender = lastSender;
    }

    public Group withLastSender(String lastSender) {
        this.lastSender = lastSender;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Group withGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

}
