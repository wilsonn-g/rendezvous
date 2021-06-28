package com.example.cpen321.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cpen321.data.Place;
import com.example.cpen321.data.PlaceIds;
import com.example.cpen321.data.PlannedEvent;
import com.example.cpen321.R;
import com.example.cpen321.retrofit.RetrofitAPICalls;
import com.example.cpen321.retrofit.RetrofitAPIClientUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.cpen321.MainActivity.userLat;
import static com.example.cpen321.MainActivity.userLng;
import static com.example.cpen321.data.User.userId;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    /* The main UI element that is the focus of this fragment */
    private MapView mapView;

    /* Constants used by the MapView object so we aren't just passing magic values */
    private static final int DEFAULT_ZOOM = 15;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    /* Lists of groupids/plannedEvents for plotting planned events
    *  and bookmarks for plotting the user's bookmarks */
    private List<String> placeids = new ArrayList<>();
    private List<Place> bookmarks = new ArrayList<>();
    private List<String> groupids = new ArrayList<>();
    private List<PlannedEvent> plannedEvents = new ArrayList<>();
    private List<Place> plannedEventsPlaces = new ArrayList<>();

    /* Iterating variables */
    private int i = 0; // for iterating through the list of placeids to populate the list of bookmarks
    private int j = 0; // for iterating through the list of groupids to populate the list of planned events
    private int k = 0; // for iterating through the list of plannedEvents to populate the list of places


    /* Fetches the data we need to obtain a list of bookmarks
     * and planned events to be shown */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadBookmarks();
        loadPlannedEvents();
    }

    private void loadBookmarks() {
        bookmarks.clear();
        placeids.clear();
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        Call<List<String>> rfitPlaceIds = rfit.getBookmarks(userId);
        rfitPlaceIds.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> place, Response<List<String>> response) {
                if (response.isSuccessful() && response.body().size() != 0) {
                    placeids.addAll(response.body());
                    if (!placeids.isEmpty()) {
                        Log.i("Success", "Loaded placeids");
                        System.out.println(placeids);
                        while (i < placeids.size()) {
                            onResponseHelper(i);
                            i++;
                        }
                    } else {
                        Log.e("Error", "placeids was empty despite successful response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("Error Message", "Can't load placeids");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlannedEvents() {
        plannedEventsPlaces.clear();
        plannedEvents.clear();
        groupids.clear();
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        Call<List<String>> rfitGroupIds = rfit.getGroupIds(userId);
        rfitGroupIds.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> place, Response<List<String>> response) {
                if (response.isSuccessful() && response.body().size() != 0) {
                    groupids.addAll(response.body());
                    if (!groupids.isEmpty()) {
                        Log.i("Success", "Loaded groupids");
                        while (j < groupids.size()) {
                            onResponseHelperEvents(j);
                            j++;
                        }
                    } else {
                        Log.e("Error", "groupids was empty despite successful response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("Error Message", "Can't load groupids");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*  Helper function to get data for a bookmark for when we successfully
     *  fetch the list of placeids from the backend
     *  Pre-Condition: index is within the bounds of the list */
    private void onResponseHelper(int index) {
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        String curPlaceId = placeids.get(index);
        Call<List<Place>> rfitPlace = rfit.getPlace(curPlaceId);

        rfitPlace.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                if (response.isSuccessful() && response.body().size()!= 0) {
                    bookmarks.add(response.body().get(0));
                    System.out.println(bookmarks);
                    System.out.println(bookmarks.size());
                    Log.i("Success", "Loaded Bookmarks");
                    loadMap();
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Log.e("Error Message", "Can't load bookmarked places");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*  Helper function to get data for a bookmark for when we successfully
     *  fetch the list of placeids from the backend
     *  Pre-Condition: index is within the bounds of the list */
    private void onResponseHelperEvents(int index) {
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        String curGroupId = groupids.get(index);
        Call<List<PlannedEvent>> rfitEvent = rfit.getPlannedEvents(curGroupId);

        rfitEvent.enqueue(new Callback<List<PlannedEvent>>() {
            @Override
            public void onResponse(Call<List<PlannedEvent>> call, Response<List<PlannedEvent>> response) {
                if (response.isSuccessful() && response.body().size()!= 0) {
                    plannedEvents.add(response.body().get(0));
                    Log.i("Success", "Loaded planned events");
                    while (k < plannedEvents.size()) {
                        onResponseHelperPlaces(k);
                        k++;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PlannedEvent>> call, Throwable t) {
                Log.e("Error Message", "Can't load planned events");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*  Helper function to get data for a bookmark for when we successfully
     *  fetch the list of placeids from the backend
     *  Pre-Condition: index is within the bounds of the list */
    private void onResponseHelperPlaces(int index) {
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        PlannedEvent curEvent = plannedEvents.get(index);
        Call<List<Place>> rfitEvent = rfit.getPlace(curEvent.getPlaceId());

        rfitEvent.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                if (response.isSuccessful() && response.body().size()!= 0) {
                    plannedEventsPlaces.add(response.body().get(0));
                    loadMap();
                    Log.i("Success", "Loaded Places for events");
                    System.out.println(plannedEventsPlaces);
                }
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                Log.e("Error Message", "Can't load planned event places");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Fill the placeids list with the placeids returned from the backend */
    private void addPlaceIds(PlaceIds response) {
        placeids = response.getPlaceids();
    }

    /* Inflates the ExploreFragment */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        return v;
    }


    private void loadMap(){
        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    /* We simply call the onResume methods from super and MapView */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /* We simply call the onStart methods from super and MapView */
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    /* We simply call the onStop methods from super and MapView */
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    /* The map's UI settings are set here */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        for (Place bookmark : bookmarks) {
            System.out.println(bookmark);
            System.out.println(bookmark.getPlaceName());
            System.out.println(bookmark.getLat());
            System.out.println(bookmark.getLng());
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(bookmark.getLat(), bookmark.getLng())) // Place lat and lng
                    .title(bookmark.getPlaceName()) // Place name
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }

        for (int p = 0; p < plannedEventsPlaces.size(); p++) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(plannedEventsPlaces.get(p).getLat(), plannedEventsPlaces.get(p).getLng())) // Place lat and lng
                    .title(plannedEventsPlaces.get(p).getPlaceName()) // Place name
                    .snippet(plannedEvents.get(p).getGroupName() + " " + plannedEvents.get(p).getTime())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLng), DEFAULT_ZOOM));
    }

    /* We simply call the onPause methods from super and MapView */
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    /* We simply call the onDestroy methods from super and MapView */
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    /* We simply call the onLowMemory methods from super and MapView */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
