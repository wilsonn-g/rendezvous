package com.example.cpen321.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpen321.adapters.ChatsAdapter;
import com.example.cpen321.data.Group;
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


public class ChatsFragment extends Fragment {
    private ArrayList<Group> groups = new ArrayList<>();
    private RecyclerView.Adapter chatAdapter = new ChatsAdapter(groups, this);
    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    // Inflates the ChatFragment
    public static String userFirstName;
    public static String userLastName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groups.clear();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            getActivity().recreate();
        } else {
            Log.d("Permissions", "already granted");
        }

        Call<User> userCall = rfit.getUser(userId);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                userFirstName = response.body().getFirstName();
                userLastName = response.body().getLastName();
                addChats(response.body().getGroups());
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
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle("Rendezvous");
        tb.inflateMenu(R.menu.chat_toolbar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.new_chat) {
                    Fragment newFrag = new NewChatFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newFrag)
                            .commit();
                }
                if (menuItem.getItemId() == R.id.requests) {
                    Fragment newFrag = new RequestsFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newFrag)
                            .commit();
                }
                if (menuItem.getItemId() == R.id.add_friends) {
                    Fragment newFrag = new AddFriendFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newFrag)
                            .commit();
                }
                if (menuItem.getItemId() == R.id.remove_friends) {
                    Fragment newFrag = new RemoveFriendsFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, newFrag)
                            .commit();
                }

                return false;
            }
        });
        RecyclerView chatList = view.findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatList.setAdapter(chatAdapter);
        return view;
    }


    private void addChats (List<String> resp) {
        for (String groupId : resp) {
            Call<Group> groupCall = rfit.getGroup(groupId);
            groupCall.enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    addGroup(response);
                    chatAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

    }

    private void addGroup (Response<Group> resp) {
        groups.add(resp.body());
    }

    public void loadConversation (String groupName, String groupId) {
        Fragment newFrag = new ConversationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("groupName", groupName);
        bundle.putString("groupId", groupId);
        newFrag.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFrag)
                .commit();
    }
}

