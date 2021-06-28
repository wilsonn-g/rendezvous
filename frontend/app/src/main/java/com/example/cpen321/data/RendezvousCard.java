package com.example.cpen321.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// POJO object for use with Retrofit
public class RendezvousCard {
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;


    public RendezvousCard(String photo, String name, double lat, double lng) {
        this.photo = photo;
        this.name = name;
        this.lat = lat;
        this.lat = lng;
    }

    public String getPhoto() { return photo; }

    public void setPhoto(String imgurl) { this.photo = photo; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() { return lng; }

    public void setLng(double lng) { this.lng = lng; }

    public String getDistance(Double userLat, Double userLng) {
        Double placeLat = new Double(lat);
        Double placeLng = new Double(lng);
        Double p = Math.PI/180;
        Double calc = 0.5 - Math.cos((placeLat - userLat) * p) / 2 +
                Math.cos(userLat * p) * Math.cos(placeLat * p) *
                (1 - Math.cos((placeLng - userLng) * p)) / 2;
        Double distance = 12742 * Math.asin(Math.sqrt(calc));
        int distanceAsInt = distance.intValue();
        Integer distanceAsInteger = new Integer(distanceAsInt);
        return distanceAsInteger.toString();
    }
}

