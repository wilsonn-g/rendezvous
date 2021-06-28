package com.example.cpen321;

import androidx.annotation.NonNull;

import com.example.cpen321.fragments.ChatsFragment;
import com.example.cpen321.fragments.DiscoverFragment;
import com.example.cpen321.fragments.ExploreFragment;
import com.example.cpen321.fragments.ProfileFragment;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;

import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.cpen321.data.User.userId;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    /* Location client defined in MainActivity for use in the rest of the app
    *  since we are using a single activity and many fragments architecture */
    private FusedLocationProviderClient fusedLocationClient;

    /* Static definitions of locationRequest since we want to enforce that there
    *  is only one and REQUEST_CHECK_SETTINGS is some constant from Google example code */
    public static LocationRequest locationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /* Static definitions of the userLat and userLng for use in any of the fragments
    *  which are navigable within MainActivity via the bottom nav bar */
    public static double userLat;
    public static double userLng;

    public static long startTimeMilli;
    public static long finishTimeMilli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput("login-info.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                // userid
                ret = stringBuilder.toString();
                userId = ret;
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        /* If we are not logged in, we send the user to the LoginActivity
        *  otherwise we load the chat fragment */
        if ("".equals(ret)) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            MainActivity.this.finish();
        }

        /* If the user is logged in, we load the ChatsFragment
         *  We will change the implementation for checking if
         *  we are logged in, if we are, we will switch to a different
         *  ACTIVITY as a solution to the bottom navigation bar problem */

        loadFragment(new ChatsFragment());

        /* Creating the LocationRequest */
        createLocationRequest();

        /* Create a Location Settings Request */
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        /* Check whether the Location Settings are Satisfied */
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                setUserLocation();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        /* Getting bottom navigation view and attaching the listener */
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this);

    }

    /* Logic for loading a new fragment when an option in the bottom
    *  nav bar is selected */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        startTimeMilli = System.currentTimeMillis();

        switch (item.getItemId()) {
            case R.id.explore:
                fragment = new ExploreFragment();
                break;

            case R.id.discover:
                fragment = new DiscoverFragment();
                break;

            case R.id.profile:
                fragment = new ProfileFragment();
                break;

            default:
                fragment = new ChatsFragment();
                break;
        }

        return loadFragment(fragment);
    }


    /* Menu icons are inflated */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu. This adds items to the action bar, if it is present
        getMenuInflater().inflate(R.menu.chat_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // User chose the "Search" action
                // Show the user search screen
                // bring up keyboard, show a search text field
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /* Logic for switching fragments */
    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /* We set the interval (in ms) for how often we would like location updates in app
    *  as well as the priority. We have chosen a fairly modest update interval and
    *  priority level to be less demanding of system resources and power */
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    /* This is called by startResolutionOnResult allegedly? */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
            default: break;
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing - navigation to be done using in-app back buttons instead
    }

    /* Called when we have the user's permission to access location
    *  sets userLat and userLng to the user's current coarse location */
    private void setUserLocation () {
        /* Creating the Location Client */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /* Get the last location */
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        /* Got last known location. In some rare situations this can be null.
                         *  for example, the location is turned off in the app settings */
                        if (location != null) {
                            /* Logic to handle location object
                             *  according to Android specs, we are
                             *  guaranteed a valid lat/lng if we have
                             *  a non-null location */
                            userLat = location.getLatitude();
                            userLng = location.getLongitude();
                        }
                    }
                });
    }

}
