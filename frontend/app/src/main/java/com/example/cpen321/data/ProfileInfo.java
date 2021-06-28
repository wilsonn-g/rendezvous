package com.example.cpen321.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileInfo {

    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("friends")
    @Expose
    private List<String> friends = null;
    @SerializedName("availability")
    @Expose
    private List<Boolean> availability = null;
    @SerializedName("interests")
    @Expose
    private List<String> interests = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public ProfileInfo() {
    }

    /**
     *
     * @param firstname
     * @param availability
     * @param interests
     * @param friends
     * @param lastname
     */
    public ProfileInfo(String firstname, String lastname, List<String> friends, List<Boolean> availability, List<String> interests) {
        super();
        this.firstname = firstname;
        this.lastname = lastname;
        this.friends = friends;
        this.availability = availability;
        this.interests = interests;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public ProfileInfo withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public ProfileInfo withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public ProfileInfo withFriends(List<String> friends) {
        this.friends = friends;
        return this;
    }

    public List<Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(List<Boolean> availability) {
        this.availability = availability;
    }

    public ProfileInfo withAvailability(List<Boolean> availability) {
        this.availability = availability;
        return this;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public ProfileInfo withInterests(List<String> interests) {
        this.interests = interests;
        return this;
    }

}