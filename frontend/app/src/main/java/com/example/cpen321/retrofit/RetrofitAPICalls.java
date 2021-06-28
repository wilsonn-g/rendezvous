package com.example.cpen321.retrofit;

import com.example.cpen321.data.Place;
import com.example.cpen321.data.PlaceIds;
import com.example.cpen321.data.PlannedEvent;
import com.example.cpen321.data.RendezvousCard;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

// Interface for the API calls
public interface RetrofitAPICalls {
    /* HTTP Requests to populate the Discover Screen's cards */
    // get the list of placeids
    @POST("recommend/users/{userid}")
    @FormUrlEncoded
    Call<PlaceIds> getPlaceIds(@Path("userid") String userId, @Field("lat") String lat, @Field("lng") String lng);

    // get each card
    @GET("places/{placeid}")
    Call<List<RendezvousCard>> getCard(@Path("placeid") String placeId);

    /* HTTP Requests to update backend of user's response to a card */
    // tell the backend the user has acted in any way on the card
    @POST("users/{userid}/seen")
    @FormUrlEncoded
    Call<ResponseBody> postSeen(@Path("userid") String userId, @Field("placeid") String placeId);

    // tell the backend the user has liked the current card
    @POST("users/{userid}/liked")
    @FormUrlEncoded
    Call<ResponseBody> postLike(@Path("userid") String userId, @Field("placeid") String placeId);

    // tell the backend the user has super liked the current card
    @POST("users/{userid}/bookmarked")
    @FormUrlEncoded
    Call<ResponseBody> postBookmark(@Path("userid") String userId, @Field("placeid") String placeId);

    /* HTTP Requests to populate the Explore (Map) Screen */
    // get the bookmarked placeids
    @GET("users/{userid}/bookmarked")
    Call<List<String>> getBookmarks(@Path("userid") String userId);

    @GET("places/{placeid}")
    Call<List<Place>> getPlace(@Path("placeid") String placeId);

    // get the groups to later search for planned events with group information
    @GET("users/{userid}/groups")
    Call<List<String>> getGroupIds(@Path("userid") String userId);

    @GET("groups/{groupid}/planned")
    Call<List<PlannedEvent>> getPlannedEvents(@Path("groupid") String groupId);

}
