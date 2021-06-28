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
import android.widget.EditText;

import com.example.cpen321.adapters.AddFriendAdapter;
import com.example.cpen321.R;
import com.example.cpen321.retrofit.RetrofitCalls;
import com.example.cpen321.data.User;
import com.example.cpen321.retrofit.RetroFitClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.cpen321.data.User.userId;


public class AddFriendFragment extends Fragment {
    private ArrayList<String> searchResultsNames = new ArrayList<>();
    private ArrayList<String> searchResultsIds = new ArrayList<>();

    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    private AddFriendAdapter friendAdapter = new AddFriendAdapter(this, searchResultsNames, searchResultsIds);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        EditText textField = view.findViewById(R.id.editText2);

        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle("Add Friend");
        tb.inflateMenu(R.menu.add_friend_toolbar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.cancel) {
                    Fragment newFrag = new ChatsFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newFrag)
                            .commit();
                }
                if (menuItem.getItemId() == R.id.search) {
                    //search
                    searchResultsNames.clear();
                    search(textField.getText().toString());
                }
                return false;
            }
        });

        RecyclerView results = view.findViewById(R.id.search_results);
        LinearLayoutManager layoutM = new LinearLayoutManager(getActivity());
        results.setLayoutManager(layoutM);
        results.setAdapter(friendAdapter);
        return view;
    }

    public void addFriend(String friendId) {
        Call<String> addFriendCall = rfit.sendRequest(friendId, userId);

        addFriendCall.enqueue(new Callback<String>() {
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

    private void search(String query) {
        Call<List<String>> searchCall = rfit.searchFriends(userId, query);

        searchCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                grabResults(response);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void grabResults(Response<List<String>> resp) {
        for (String s: resp.body()) {
            searchResultsIds.add(s);
            Call<User> userCall = rfit.getUser(s);

            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    searchResultsNames.add(response.body().getFirstName() + " " + response.body().getLastName());
                    friendAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();

                }
            });
        }
    }
}
