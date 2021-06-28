package com.example.cpen321.retrofit;

import com.example.cpen321.data.User;
import com.example.cpen321.data.VotingEvent;
import com.example.cpen321.data.ChatMessage;
import com.example.cpen321.data.Group;
import com.example.cpen321.data.Place;
import com.example.cpen321.data.PlaceIds;
import com.example.cpen321.data.ProfileInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitCalls {

    @GET("groups/{id}")
    Call<Group> getGroup(@Path("id") String groupId);

    @GET("users/{id}")
    Call<User> getUser(@Path ("id") String userId);

    @GET("users/{id}/groups")
    Call<List<String>> getGroupIds(@Path("id") String userId);

    @GET("places/{id}")
    Call<List<Place>> getPlace(@Path("id") String placeId);

    @GET("groups/{id}/likedPlaces")
    Call<List<Place>> getSuggestedPlaces(@Path ("id") String groupId);

    @GET("groups/{id}/messages")
    Call<List<ChatMessage>> getMessages(@Path ("id") String groupId);


    @POST("groups/{id}/voting")
    @FormUrlEncoded
    Call<VotingEvent> makeSuggestion(@Path ("id") String groupId, @Field ("senderId") String senderId, @Field ("placeid") String placeId, @Field ("placeName") String placeName, @Field("index") int timeInd, @Field ("sendername") String senderName, @Field("photo") String photo);

    @POST("users/{id}/interests")
    @FormUrlEncoded
    Call<String> addInterest(@Path ("id") String userId, @Field("interest") String interest);

    @POST("groups")
    @FormUrlEncoded
    Call<Group> addGroup(@Field("groupname") String groupName);

    @POST("groups/{id}/members")
    @FormUrlEncoded
    Call<String> addToGroup(@Path ("id") String groupId, @Field("member") String memberId);

    @POST("users/{id}/groups")
    @FormUrlEncoded
    Call<String> addToGroupArray(@Path ("id") String userId, @Field("group") String groupId);

    @POST("users/{id}/friendrequests")
    @FormUrlEncoded
    Call<String> sendRequest(@Path ("id") String friendId, @Field("userid") String userId);

    @POST("users/{id}/friends")
    @FormUrlEncoded
    Call<String> acceptRequest(@Path ("id") String userId, @Field("userid") String friendId);

    @POST("recommend/groups/{groupid}")
    @FormUrlEncoded
    Call<PlaceIds> getPlaceIds(@Path("groupid") String groupId, @Field("lat") String lat, @Field("lng") String lng);

    @GET("users/{id}/interests")
    Call<List<String>> getInterests (@Path("id") String userId);

    @GET("users/{id}/friends")
    Call<List<String>> getFriends (@Path("id") String userId);

    @GET("users/{id}/search/{query}")
    Call<List<String>> searchFriends (@Path ("id") String userId, @Path("query") String query);


    @GET("users/{id}/friendrequests")
    Call<List<String>> getRequests (@Path("id") String userId);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "users/{userid}/friendrequests", hasBody = true)
    Call<String> removeRequest(@Path ("userid") String userid, @Field("userid") String friendId);

    @Headers("Content-Type: application/json")
    @PUT("users/{id}")
    Call<ProfileInfo> updateProfile (@Path ("id") String userId, @Body ProfileInfo body);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "users/{userid}/groups", hasBody = true)
    Call<String> removeGroup(@Path ("userid") String userid, @Field("group") String group);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "groups/{groupid}/members", hasBody = true)
    Call<String> removeMember(@Path ("groupid") String groupid, @Field("member") String member);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "users/{id}/friends", hasBody = true)
    Call<String> removeFriend(@Path ("id") String userId, @Field("userid") String friendId);

    @POST("groups/{groupid}/vote")
    @FormUrlEncoded
    Call<String> vote (@Path ("groupid") String groupid, @Field("datetime") long datetime, @Field("placeid") String placeid, @Field("vote") String vote, @Field ("userid") String userid);

    @POST("recommend/search")
    @FormUrlEncoded
    Call<PlaceIds> placeSearch (@Field("query") String query, @Field("lat") String lat, @Field("lng") String lng);

    @GET("groups/{groupid}/optimaltimes")
    Call<List<Integer>> getOptimalTimes(@Path("groupid") String groupId);

}
