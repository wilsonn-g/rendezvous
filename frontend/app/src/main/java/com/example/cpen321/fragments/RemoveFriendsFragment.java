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
import com.example.cpen321.adapters.NewChatAdapter;
import com.example.cpen321.retrofit.RetroFitClientUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.cpen321.data.User.userId;


public class RemoveFriendsFragment extends Fragment {
    private ArrayList<String> friendIds = new ArrayList<>();
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> removedIds = new ArrayList<>();

    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    private NewChatAdapter newChatAdapter = new NewChatAdapter(friends, friendIds, removedIds);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Call<List<String>> friendCall = rfit.getFriends(userId);

        friendCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                showFriends(response);
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
        View view = inflater.inflate(R.layout.fragment_remove_friends, container, false);

        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle("Remove Friends");
        tb.inflateMenu(R.menu.new_chat_toolbar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.done:
                        removeFriends();
                        Fragment newFrag = new ChatsFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, newFrag)
                                .commit();
                        break;

                    case R.id.cancel:
                        newFrag = new ChatsFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, newFrag)
                                .commit();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        RecyclerView friends = view.findViewById(R.id.friends);
        LinearLayoutManager layoutM = new LinearLayoutManager(getActivity());
        layoutM.setStackFromEnd(true);
        friends.setLayoutManager(layoutM);
        friends.setAdapter(newChatAdapter);
        return view;
    }

    private void showFriends(Response<List<String>> resp) {
        for (String friendId : resp.body()) {
            Call<User> userCall = rfit.getUser(friendId);

            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    friendIds.add(response.body().getId());
                    friends.add(response.body().getFirstName() + " " + response.body().getLastName());
                    newChatAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void removeFriends() {
        for (String memberId : removedIds) {
            Call<String> removeCall = rfit.removeFriend(userId, memberId);

            removeCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    //TODO: implement onResponse
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            removeCall = rfit.removeFriend(memberId, userId);

            removeCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    //TODO: implement onResponse
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

}
