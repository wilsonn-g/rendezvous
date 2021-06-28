package com.example.cpen321.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import com.example.cpen321.R;
import com.example.cpen321.retrofit.RetrofitCalls;
import com.example.cpen321.data.User;
import com.example.cpen321.adapters.RequestsAdapter;
import com.example.cpen321.retrofit.RetroFitClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.cpen321.data.User.userId;


public class RequestsFragment extends Fragment {
    private ArrayList<String> requestIds = new ArrayList<>();
    private ArrayList<String> requestNames = new ArrayList<>();

    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    private RequestsAdapter requestsAdapter = new RequestsAdapter(this, requestIds, requestNames);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Call<List<String>> requestsCall = rfit.getRequests(userId);

        requestsCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                requestIds.addAll(response.body());
               grabRequestNames(response);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                t.printStackTrace();
            }
        });




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interests, container, false);

        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle("Requests");
        tb.inflateMenu(R.menu.request_toolbar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.done) {
                    Fragment newFrag = new ChatsFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newFrag)
                            .commit();
                }
                return false;
            }
        });

        RecyclerView results = view.findViewById(R.id.availability);
        LinearLayoutManager layoutM = new LinearLayoutManager(getActivity());
        layoutM.setStackFromEnd(true);
        results.setLayoutManager(layoutM);
        results.setAdapter(requestsAdapter);
        return view;
    }

    private void grabRequestNames(Response<List<String>> resp) {
        for (String requestId : resp.body()) {
            Call<User> userCall = rfit.getUser(requestId);

            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    requestNames.add(response.body().getFirstName() + " " + response.body().getLastName());
                    requestsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public void acceptRequest(String friendId) {
        Call<String> acceptCall = rfit.acceptRequest(userId, friendId);

        acceptCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // TODO: Write onResponse
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });

        acceptCall = rfit.acceptRequest(friendId, userId);

        acceptCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // TODO: Write onResponse
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });

        Call<String> removeRequestCall = rfit.removeRequest(userId, friendId);

        removeRequestCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Fragment newFrag = new ChatsFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .commit();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Fragment newFrag = new ChatsFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .commit();
                t.printStackTrace();
            }
        });
    }

    public void declineRequest(String friendId) {
        Call<String> removeRequestCall = rfit.removeRequest(userId, friendId);

        removeRequestCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Fragment newFrag = new ChatsFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .commit();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Fragment newFrag = new ChatsFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .commit();
                t.printStackTrace();
            }
        });
    }
}
