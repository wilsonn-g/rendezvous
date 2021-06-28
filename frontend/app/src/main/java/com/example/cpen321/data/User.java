
package com.example.cpen321.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    public static String userId;

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("friends")
    @Expose
    private List<String> friends = null;
    @SerializedName("groups")
    @Expose
    private List<String> groups = null;
    @SerializedName("interests")
    @Expose
    private List<String> interests = null;
    @SerializedName("likedPlaces")
    @Expose
    private List<String> likedPlaces = null;
    @SerializedName("seenPlaces")
    @Expose
    private List<String> seenPlaces = null;
    @SerializedName("bookmarkedPlaces")
    @Expose
    private List<String> bookmarkedPlaces = null;
    @SerializedName("firstname")
    @Expose
    private String firstName;
    @SerializedName("lastname")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("salt")
    @Expose
    private String salt;

    @SerializedName("availability")
    @Expose
    private List<Boolean> availability;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User withId(String id) {
        this.id = id;
        return this;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public User withFriends(List<String> friends) {
        this.friends = friends;
        return this;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public User withGroups(List<String> groups) {
        this.groups = groups;
        return this;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public User withInterests(List<String> interests) {
        this.interests = interests;
        return this;
    }

    public List<String> getLikedPlaces() {
        return likedPlaces;
    }

    public void setLikedPlaces(List<String> likedPlaces) {
        this.likedPlaces = likedPlaces;
    }

    public User withLikedPlaces(List<String> likedPlaces) {
        this.likedPlaces = likedPlaces;
        return this;
    }

    public List<String> getSeenPlaces() {
        return seenPlaces;
    }

    public void setSeenPlaces(List<String> seenPlaces) {
        this.seenPlaces = seenPlaces;
    }

    public User withSeenPlaces(List<String> seenPlaces) {
        this.seenPlaces = seenPlaces;
        return this;
    }

    public List<String> getBookmarkedPlaces() {
        return bookmarkedPlaces;
    }

    public void setBookmarkedPlaces(List<String> bookmarkedPlaces) {
        this.bookmarkedPlaces = bookmarkedPlaces;
    }

    public User withBookmarkedPlaces(List<String> bookmarkedPlaces) {
        this.bookmarkedPlaces = bookmarkedPlaces;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public User withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public User withAge(String age) {
        this.age = age;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User withPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public User withSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public List<Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(List<Boolean> availability) {
        this.availability = availability;
    }


}
