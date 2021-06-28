package com.example.cpen321.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place {
    @SerializedName("placeid")
    @Expose
    private String placeId;

    @SerializedName("name")
    @Expose
    private String placeName;

    @SerializedName("photo")
    @Expose
    private String photo;

    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("lng")
    @Expose
    private double lng;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getLat() { return lat; }

    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }

    public void setLng(double lng) { this.lng = lng; }

    public Place(String placeId, String placeName) {
        this.placeId = placeId;
        this.placeName = placeName;
    }

    public String getDistance(double userLat, double userLng) {
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
