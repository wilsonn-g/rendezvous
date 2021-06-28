package com.example.cpen321.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaceIds {

    @SerializedName("placeids")
    @Expose
    private List<String> placeids = null;

    public List<String> getPlaceids() {
        return placeids;
    }

    public void setPlaceids(List<String> placeids) {
        this.placeids = placeids;
    }
}
