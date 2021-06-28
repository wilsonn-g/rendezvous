package com.example.cpen321.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cpen321.adapters.AvailabilityAdapter;
import com.example.cpen321.data.ProfileInfo;
import com.example.cpen321.R;
import com.example.cpen321.retrofit.RetrofitCalls;
import com.example.cpen321.data.User;
import com.example.cpen321.retrofit.RetroFitClientUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import static com.example.cpen321.data.User.userId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AvailabilityFragment extends Fragment  {

    private ArrayList<Boolean> available = new ArrayList<>();
    private User currentUser;
    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    private AvailabilityAdapter availabilityAdapter = new AvailabilityAdapter(available);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Call<User> userCall = rfit.getUser(userId);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                currentUser = response.body();
                available.clear();
                available.addAll(currentUser.getAvailability());
                availabilityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availability, container, false);

        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle("Set your availability");

        RecyclerView grid = view.findViewById(R.id.grid);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 8);
        grid.setLayoutManager(layoutManager);
        grid.setAdapter(availabilityAdapter);

        tb.inflateMenu(R.menu.interests_toolbar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.done:
                        //apply changes then exit
//                        setExampleAvailability();
                        currentUser.setAvailability(available);
                        updateProfileInfo(currentUser);
                        Fragment newFragProf = new ProfileFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, newFragProf)
                                .commit();

                        break;

                    case R.id.cancel:
                        //just exit
                        newFragProf = new ProfileFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, newFragProf)
                                .commit();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        return view;
    }

    public void updateProfileInfo (User user) {
        ProfileInfo profile = new ProfileInfo(user.getFirstName(), user.getLastName(), user.getFriends(), user.getAvailability(), user.getInterests());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        RetrofitCalls retrofit = new Retrofit.Builder()
                .baseUrl("http://18.236.160.32:8000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RetrofitCalls.class);


        Call<ProfileInfo> profileCall = retrofit.updateProfile(userId, profile);
        profileCall.enqueue(new Callback<ProfileInfo>() {
            @Override
            public void onResponse(Call<ProfileInfo> call, Response<ProfileInfo> response) {
                Fragment newFrag = new ProfileFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .commit();
            }

            @Override
            public void onFailure(Call<ProfileInfo> call, Throwable t) {
                Fragment newFrag = new ProfileFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .commit();
                t.printStackTrace();
            }
        });

    }

}
