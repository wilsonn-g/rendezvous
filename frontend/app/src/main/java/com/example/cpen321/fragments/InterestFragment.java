package com.example.cpen321.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpen321.data.ProfileInfo;
import com.example.cpen321.R;
import com.example.cpen321.retrofit.RetrofitCalls;
import com.example.cpen321.data.User;
import com.example.cpen321.adapters.InterestAdapter;
import com.example.cpen321.retrofit.RetroFitClientUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.cpen321.data.User.userId;
//import static com.example.cpen321.fragments.ProfileFragment.updateProfileInfo;

public class InterestFragment extends Fragment {

    private ArrayList<String> potentialInterests = new ArrayList<>();
    private ArrayList<String> chosenInterests = new ArrayList<>();
    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    private InterestAdapter interestAdapter = new InterestAdapter(this, potentialInterests, chosenInterests);
    private User currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Call<User> userCall = rfit.getUser(userId);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                currentUser = response.body();
                chosenInterests.addAll(currentUser.getInterests());
                interestAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();

            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interests, container, false);
        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle("Select your interests");
        tb.inflateMenu(R.menu.interests_toolbar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.done:
                        //apply changes then exit
                        applyChanges();
                        break;

                    case R.id.cancel:
                        //just exit
                        Fragment newFragProf = new ProfileFragment();
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


        potentialInterests.add("General");
        potentialInterests.add("Sports");
        potentialInterests.add("Food");
        potentialInterests.add("Shopping");
        potentialInterests.add("Nature");
        potentialInterests.add("Movies");
        potentialInterests.add("Art");
        potentialInterests.add("Health and Safety");
        potentialInterests.add("Spirituality");
        potentialInterests.add("Nightlife");
        potentialInterests.add("Dessert");
        potentialInterests.add("Literature");
        potentialInterests.add("Animals");


        RecyclerView interestList = view.findViewById(R.id.availability);
        interestList.setHasFixedSize(true);
        interestList.setLayoutManager(new LinearLayoutManager(getActivity()));

        interestList.setAdapter(interestAdapter);

        return view;
    }

    private void applyChanges() {

        for (String interest : chosenInterests) {

            if ("Health and Safety".equals(interest)) {
                chosenInterests.remove(interest);
                chosenInterests.add("healthsafety");
            }
        }

        currentUser.setInterests(chosenInterests);
        updateProfileInfo(currentUser);

    }

    public String interestBackEndRep (String str) {
        String frontEndRep = str;
        if ("Health and Safety".equals(frontEndRep)) {
            frontEndRep = "healthsafety";
        }
        return frontEndRep.toLowerCase();
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
