package com.example.cpen321.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetroFitClientUtils {
    private static Retrofit instanceLogin;
    private static Retrofit instanceChat;
    private static Retrofit instanceMessages;
    private static Retrofit instancePlaces;

    public static Retrofit getInstanceLogin() {
        if (instanceLogin == null)
            instanceLogin = new Retrofit.Builder().baseUrl("http://18.236.160.32:8000/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return instanceLogin;
    }
    public static Retrofit getInstanceChat() {
        if (instanceChat == null)
            instanceChat = new Retrofit.Builder().baseUrl("http://18.236.160.32:8000/")
                    .addConverterFactory(GsonConverterFactory.create()).build();
        return instanceChat;
    }
    public static Retrofit getInstanceMessages() {
        if (instanceMessages == null)
            instanceMessages = new Retrofit.Builder().baseUrl("https://api.myjson.com/bins/")
                    .addConverterFactory(GsonConverterFactory.create()).build();
        return instanceMessages;
    }
    public static Retrofit getInstancePlaces() {
        if (instancePlaces == null)
            instancePlaces = new Retrofit.Builder().baseUrl("https://api.myjson.com/bins/")
                    .addConverterFactory(GsonConverterFactory.create()).build();
        return instancePlaces;
    }
}
