package com.example.cpen321.retrofit;

import io.reactivex.Observable;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NodeJS {
    @POST("users")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("firstname") String first_name,
                                    @Field("lastname") String last_name,
                                    @Field("password") String password);

    @POST("users/login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("email") String email,
                                 @Field("password") String password,
                                 @Field("token") String token);
}
