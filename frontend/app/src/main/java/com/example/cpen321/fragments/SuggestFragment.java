package com.example.cpen321.fragments;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;


import com.example.cpen321.R;
import com.example.cpen321.data.User;
import com.example.cpen321.data.VotingEvent;
import com.example.cpen321.adapters.SuggestedPlacesAdapter;
import com.example.cpen321.data.Place;
import com.example.cpen321.data.PlaceIds;
import com.example.cpen321.retrofit.RetroFitClientUtils;
import com.example.cpen321.retrofit.RetrofitCalls;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.cpen321.data.User.userId;
import static com.example.cpen321.MainActivity.userLat;
import static com.example.cpen321.fragments.ChatsFragment.userFirstName;
import static com.example.cpen321.fragments.ChatsFragment.userLastName;
import static com.example.cpen321.MainActivity.userLng;
import static java.lang.String.valueOf;


public class SuggestFragment extends Fragment {
    private ArrayList<Place> suggestedPlaces = new ArrayList<>();
    private SuggestedPlacesAdapter suggestedPlacesAdapter = new SuggestedPlacesAdapter(suggestedPlaces, this);
    private ArrayList<Place> bookmarkedPlaces = new ArrayList<>();
    private SuggestedPlacesAdapter bookmarkedPlacesAdapter = new SuggestedPlacesAdapter(bookmarkedPlaces, this);
    private RetrofitCalls rfit = RetroFitClientUtils.getInstanceChat().create(RetrofitCalls.class);
    private List<Integer> optimalTimes = new ArrayList<>();
    private String groupName;
    private String groupId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getArguments().getString("groupId");
        groupName = getArguments().getString("groupName");

        suggestedPlaces.clear();

        Call<List<Integer>> timeCall = rfit.getOptimalTimes(groupId);
        timeCall.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                optimalTimes.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                t.printStackTrace();
            }
        });


        Call<PlaceIds> placeIdCall = rfit.getPlaceIds(groupId, valueOf(userLat), valueOf(userLng));
        placeIdCall.enqueue(new Callback<PlaceIds>() {
            @Override
            public void onResponse(Call<PlaceIds> call, Response<PlaceIds> response) {
                getPlaces(response);
            }

            @Override
            public void onFailure(Call<PlaceIds> call, Throwable t) {
                t.printStackTrace();

            }
        });

        Call<User> userCall = rfit.getUser(userId);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                getBookmarkedPlaces(response);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();

            }
        });

//        addFixedPlaces(); //FOR TESTING UI SO AS TO NOT USE CREDITS
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggest, container, false);

        String str = "Suggest to " + groupName;
        Toolbar tb = view.findViewById(R.id.my_toolbar);
        tb.setTitle(str);
        tb.inflateMenu(R.menu.suggest_toolbar);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.cancel) {
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
                return false;
            }
        });

        EditText search_field = view.findViewById(R.id.search_bar);
        Button search = view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(search_field.getText().toString());
            }
        });


        RecyclerView suggestedPlaces = view.findViewById(R.id.suggestedPlaces);
        suggestedPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        suggestedPlaces.setAdapter(suggestedPlacesAdapter);

        RecyclerView bookmarkedPlaces = view.findViewById(R.id.bookmarked);
        bookmarkedPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        bookmarkedPlaces.setAdapter(bookmarkedPlacesAdapter);
        return view;
    }

    private void sendSuggestion (Place place, int day, int hour, int minute) {
        int index = day * 48 + hour * 2;
        if (minute >= 30)
            index++;

        Call<VotingEvent> eventCall = rfit.makeSuggestion(groupId, userId, place.getPlaceId(), place.getPlaceName(), index,
                (userFirstName + " " + userLastName + " "), place.getPhoto());

        eventCall.enqueue(new Callback<VotingEvent>() {
            @Override
            public void onResponse(Call<VotingEvent> call, Response<VotingEvent> response) {
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

            @Override
            public void onFailure(Call<VotingEvent> call, Throwable t) {

                Fragment newFrag = new ConversationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("groupName", groupName);
                bundle.putString("groupId", groupId);
                newFrag.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, newFrag)
                        .commit();
                t.printStackTrace();
            }
        });

    }

    public void createSuggestion(Place place) {

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                makeSuggestionDay(place, selectedHour, selectedMinute);

            }
        }, 0, 0, true);


        String message = "Optimal times: ";
        for (Integer time : optimalTimes) {
            message = message.concat("\n" + timeIndexToString(time));
        }

        mTimePicker.setMessage(message + "\n");
        mTimePicker.show();
    }

    private void makeSuggestionDay (Place place, int hour, int minute) {

        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int day) {
                sendSuggestion(place, day, hour, minute);
            }
        };
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("For what day would you like to plan your event?");
        builder.setItems(days, dialogListener);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void addFixedPlaces() {
        suggestedPlaces.add(new Place("aaa", "Playland"));
        suggestedPlaces.add(new Place("bbb", "Nitobe Garden"));
        suggestedPlaces.add(new Place("ccc", "Mercjknnnnnnnnnnnnnnnnnnnnnnn nnnijjjjjjjjjjjjjjjjjjjjjjjante"));
        suggestedPlaces.add(new Place("ddd", "Dunbar Public House"));
        suggestedPlaces.add(new Place("eee", "Rogers Arena"));
        suggestedPlaces.add(new Place("aaa", "Playland"));
        suggestedPlaces.add(new Place("bbb", "Nitobe Garden"));
        suggestedPlaces.add(new Place("ccc", "Mercante"));
        suggestedPlaces.add(new Place("ddd", "Dunbar Publi bbgnnnnnnnnnnnnnnnnkjc House"));
        suggestedPlaces.add(new Place("eee", "Rogers Arena"));
    }

    private void getPlaces(Response<PlaceIds> resp) {
        suggestedPlaces.clear();
        for (String s : resp.body().getPlaceids()) {
            Call<List<Place>> placeCall = rfit.getPlace(s);
            placeCall.enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.body().size() > 0) {
                            suggestedPlaces.add(response.body().get(0));
                            suggestedPlacesAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    private void getBookmarkedPlaces(Response<User> resp) {
        for (String s : resp.body().getBookmarkedPlaces()) {
            Call<List<Place>> placeCall = rfit.getPlace(s);
            placeCall.enqueue(new Callback<List<Place>>() {
                @Override
                public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                    if (response.body().size() > 0) {
                        bookmarkedPlaces.add(response.body().get(0));
                        bookmarkedPlacesAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Place>> call, Throwable t) {
                    t.printStackTrace();

                }
            });
        }
    }

    private void search (String query) {
        Call<PlaceIds> searchCall = rfit.placeSearch(query, valueOf(userLat), valueOf(userLng));
        searchCall.enqueue(new Callback<PlaceIds>() {
            @Override
            public void onResponse(Call<PlaceIds> call, Response<PlaceIds> response) {
                getPlaces(response);
            }

            @Override
            public void onFailure(Call<PlaceIds> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String timeIndexToString(Integer index) {
        String hour = valueOf((index % 48) / 2);
        String min;
        if (index % 2 == 0)
            min = ":00";
        else
            min = ":30";
        String day;
        switch (index / 48) {
            case 0: day = "Monday"; break;
            case 1: day = "Tuesday"; break;
            case 2: day = "Wednesday"; break;
            case 3: day = "Thursday"; break;
            case 4: day = "Friday"; break;
            case 5: day = "Saturday"; break;
            case 6: day = "Sunday"; break;
            default: day = "any day"; break;
        }
        return hour + min + " on " + day;
    }
}
