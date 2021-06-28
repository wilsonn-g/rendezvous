package com.example.cpen321.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cpen321.LoginActivity;
import com.example.cpen321.R;
import com.example.cpen321.retrofit.RetrofitCalls;
import com.example.cpen321.data.User;
import com.example.cpen321.retrofit.RetroFitClientUtils;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.cpen321.data.User.userId;
import static com.example.cpen321.fragments.ChatsFragment.userFirstName;
import static com.example.cpen321.fragments.ChatsFragment.userLastName;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends Fragment {
    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    private TextView num_friends;
    private TextView interests;
    private List<Boolean> available = new ArrayList<>();
    private AvailScreenAdapter availScreenAdapter = new AvailScreenAdapter(available);
    private List<User> currentUserFriends = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Call<User> userCall = rfit.getUser(userId);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loadProfileInfo(response.body());
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle(userFirstName + " " + userLastName);
        tb.inflateMenu(R.menu.profile_bar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                          @Override
                                          public boolean onMenuItemClick(MenuItem menuItem) {
                                              if (menuItem.getItemId() == R.id.action_logout) {
                                                  Intent myIntent = new Intent(getActivity(), LoginActivity.class);
                                                  startActivity(myIntent);
                                                  getApplicationContext().deleteFile("login-info.txt");
                                                  LoginManager.getInstance().logOut();
                                                  getActivity().finish();
                                              }
                                              return false;
                                          }});
        interests = view.findViewById(R.id.interest_list);
        num_friends = view.findViewById(R.id.friend_list);
        Button editInterests = view.findViewById(R.id.edit_interests);
        editInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFrag = new InterestFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .addToBackStack(null).commit();
            }
        });
        Button editAvail = view.findViewById(R.id.edit_avail);
        editAvail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFrag = new AvailabilityFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .addToBackStack(null).commit();
            }
        });
        Button remove = view.findViewById(R.id.remove_friends);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFriends();
            }
        });

        RecyclerView availGrid = view.findViewById(R.id.avail_calendar);
        availGrid.setLayoutManager(new GridLayoutManager(getContext(), 7));
        availGrid.setAdapter(availScreenAdapter);

        return view;
    }

    private void loadProfileInfo(User user) {
        String str = "";
        currentUserFriends.clear();
        for (int i = 0; i < user.getFriends().size(); i++) {
            Call<User> userCall = rfit.getUser(user.getFriends().get(i));

            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    currentUserFriends.add(response.body());
                    String str = num_friends.getText().toString();
                    str = str.concat(response.body().getFirstName() + " " + response.body().getLastName() + "\n");
                    num_friends.setText(str);

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                }
            });

            str = num_friends.getText().toString();

            if (i >= 4) {
                str = str.concat("and " + (user.getFriends().size() - 5) + " more");
                break;
            }
        }

        num_friends.setText(str);
        str = "";
        for (int i = 0; i < user.getInterests().size(); i++) {
            if (i > 4) {
                str = str.concat("and " + (user.getInterests().size() - 5) + " more");
                break;
            }
            str = str.concat(convertFrontEndRep(user.getInterests().get(i)) + "\n");
        }
        interests.setText(str);

        available.addAll(user.getAvailability());
        availScreenAdapter.notifyDataSetChanged();
    }

    private String convertFrontEndRep (String interest) {
        if ("healthsafety".equals(interest))
            return "Health & Safety";
        else
            return (interest.substring(0,1).toUpperCase() + interest.substring(1));
    }

    public class AvailScreenAdapter extends RecyclerView.Adapter<AvailScreenAdapter.AvailScreenViewHolder>{
        public List<Boolean>available;

        public AvailScreenAdapter(List<Boolean> available) {
            super();
            this.available = available;
        }

        @NonNull
        @Override
        public AvailScreenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.red_square, parent, false);
            }
            else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.green_square, parent, false);
            }
            return new AvailScreenViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AvailScreenViewHolder holder, int position) {
            //intentionally empty
        }

        @Override
        public int getItemViewType(int position) {
            if (available.get(conv(position)))
                return 1;
            else
                return 0;
        }

        @Override
        public int getItemCount() {
            return available.size();
        }

        public class AvailScreenViewHolder extends RecyclerView.ViewHolder {
            public AvailScreenViewHolder (View cell) {
                super(cell);
            }
        }

        private int conv (int pos) {
            int row = pos / 7;
            int col = pos % 7;
            return col * 48 + row;
        }
    }

    private void removeFriends() {
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int friend) {
                Log.d("remove friend", ":(");
            }
        };
        String [] friends = new String [currentUserFriends.size()];
        for (int i = 0; i < currentUserFriends.size(); i++) {
            friends[i] = currentUserFriends.get(i).getFirstName() + " " + currentUserFriends.get(i).getLastName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Your friends");
        builder.setItems(friends, dialogListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
