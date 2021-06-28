package com.example.cpen321.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.cpen321.data.PlaceIds;
import com.example.cpen321.R;
import com.example.cpen321.data.RendezvousCard;
import com.example.cpen321.retrofit.RetrofitAPICalls;
import com.example.cpen321.retrofit.RetrofitAPIClientUtils;
import com.google.android.material.card.MaterialCardView;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.cpen321.MainActivity.userLat;
import static com.example.cpen321.MainActivity.userLng;
import static com.example.cpen321.data.User.userId;
import static com.example.cpen321.MainActivity.finishTimeMilli;
import static com.example.cpen321.MainActivity.startTimeMilli;

public class DiscoverFragment extends Fragment implements View.OnClickListener {

    /* The UI element we manipulate for the functionality of this fragment */
    private MaterialCardView cardView;

    /* Definitions for actions we will use in onClickHelper */
    private static final int DISMISS = 0;
    private static final int LIKE = 1;
    private static final int SUPERLIKE = 2;

    /* Lists that we need to fetch data from the backend for */
    private List<String> placeids = new ArrayList<>();
    private ArrayList<RendezvousCard> cards = new ArrayList<>();

    /* Iterating variables */
    private int index = 0; // for iterating through the current list of cards
    private int i = 0; // for iterating through the list of placeids to populate the list of cards

    /* Fetches the data we need to obtain a list of cards that we will be displaying
    *  Note: the userId and lat/lng is needed to send to the backend for receiving a proper
    *        and accurate list of cards to swipe on */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadCards();
    }

    /* Get a new list of placeids and a new list of cards
    *  Modifies: placeids - on a successful request to the backend, placeids contains
    *                           a list of placeids, it is clear otherwise
    *            cards - if placeids was successful and any subsequent requests
    *                       to fetch cards is successful, we have a list of card(s),
    *                       and it is clear otherwise */
    private void loadCards() {
        cards.clear();
        placeids.clear();
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        // For debugging, UBC is userLat == 49.267941 and userLng == -123.247360
        Call<PlaceIds> rfitPlaceIds = rfit.getPlaceIds(userId, String.valueOf(userLat), String.valueOf(userLng));
        rfitPlaceIds.enqueue(new Callback<PlaceIds>() {
            @Override
            public void onResponse(Call<PlaceIds> place, Response<PlaceIds> response) {
                if (response.isSuccessful()) {
                    addPlaceIds(response.body());
                    if (!placeids.isEmpty()) {
                        Log.i("Success", "Loaded placeids");
                        while (i < placeids.size()) {
                            onResponseHelper(i);
                            i++;
                        }
                        finishTimeMilli = System.currentTimeMillis();
                        System.out.println((finishTimeMilli - startTimeMilli));
                    } else {
                        Log.e("Error", "placeids was empty despite successful response");
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceIds> call, Throwable t) {
                Log.e("Error Message", "Can't load placeids");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Helper function to get data for a single card for when we successfully
    *  fetch the list of placeids from the backend
    *  Pre-Condition: index is within the bounds of the list */
    private void onResponseHelper(int index) {
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        String curPlaceId = placeids.get(index);
        Call<List<RendezvousCard>> rfitCard = rfit.getCard(curPlaceId);

        rfitCard.enqueue(new Callback<List<RendezvousCard>>() {
            @Override
            public void onResponse(Call<List<RendezvousCard>> call, Response<List<RendezvousCard>> response) {
                if (response.isSuccessful() && response.body().size()!= 0) {
                    addCard(response.body().get(0));
                    Log.i("Success", "Loaded card");
                }
            }

            @Override
            public void onFailure(Call<List<RendezvousCard>> call, Throwable t) {
                Log.e("Error Message", "Can't load cards");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Inflates the DiscoverFragment UI */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discover, null);
        ImageButton button_dismiss = (ImageButton) view.findViewById(R.id.dismiss);
        ImageButton button_super_like = (ImageButton) view.findViewById(R.id.super_like);
        ImageButton button_like = (ImageButton) view.findViewById(R.id.like);
        ImageButton button_refresh = (ImageButton) view.findViewById((R.id.refresh));
        cardView = view.findViewById(R.id.materialCardView);

        button_dismiss.setOnClickListener(this);
        button_super_like.setOnClickListener(this);
        button_like.setOnClickListener(this);
        button_refresh.setOnClickListener(this);

        return view;
    }

    /* We just call the super onResume for now since we don't completely understand how this
    *  exactly works given that we need to track both the activity and fragment life-cycles */
    @Override
    public void onResume() {
        super.onResume();
    }

    /* Shared onClick method for the refresh, dismiss, like, and super-like buttons
    *  Pre-condition: v is one of the four ImageButtons
    *  Modifies: index = 1 if button_refresh was pressed, otherwise modifies
    *            index according to ButtonClickAnimation spec */
    @Override
    public void onClick(View v) {
        if (!placeids.isEmpty() && !cards.isEmpty()) {

            switch (v.getId()) {

                case R.id.refresh:
                    /* code for refresh button, we modify index here
                     * since there is no corresponding animation */
                    index = 0;
                    nextPhoto(v);
                    break;

                case R.id.dismiss:
                    // code for dismiss button
                    onClickPostHelper(DISMISS, index);
                    buttonClickAnimation(v, R.anim.to_the_left);
                    break;

                case R.id.super_like:
                    // code for super like button
                    onClickPostHelper(SUPERLIKE, index);
                    buttonClickAnimation(v, R.anim.slide_up);
                    break;

                case R.id.like:
                    // code for like button
                    onClickPostHelper(LIKE, index);
                    buttonClickAnimation(v, R.anim.to_the_right);
                    break;

                default:
                    // mistakes were made, some other id was somehow encountered
                    Toast.makeText(getActivity(), "UH-OH", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            loadCards();
        }
    }

    /* Fill the placeids list with the placeids returned from the backend */
    private void addPlaceIds(PlaceIds response) {
        placeids = response.getPlaceids();
    }

    /* Add a card returned from the backend to the cards list */
    private void addCard(RendezvousCard response) {
        //Toast.makeText(getActivity(), "Cards loaded, hit refresh", Toast.LENGTH_SHORT).show();
        cards.add(response);
    }

    /* Perform the appropriate animation based on the ImageButton that was pressed
    *  Modifies: i -> unchanged if index < cards.size(), 0 otherwise
    *            index -> index++ if index < cards.size(), 0 otherwise
    *            placeids -> fresh list of placeids if index >= cards.size(), unchanged otherwise
    *            cards -> fresh list of cards if index >= cards.size(), unchanged otherwise */
    private void buttonClickAnimation(View v, int animation) {
        Animation swipeAnimation = AnimationUtils.loadAnimation(getActivity(), animation);
        cardView.startAnimation(swipeAnimation);
        /* the condition might be slightly off but it should resolve because
        * of the reloading of cards, we were getting index out of bounds
        * otherwise */
        if (index >= cards.size() - 1) {
            index = 0;
            i = 0;
            loadCards();
        }
        /* we have to increment before the nextPhoto call,
        * otherwise we bamboozle the user by sending data that corresponds
        * to the previous element */
        else {
            index++;
            nextPhoto(v);
        }
    }

    /* Update the card UI, i.e. the image, distance, and text
     *  Note: this method uses the user's current lat/lng as parameters
     *       for the distance calculation */
    private void nextPhoto(View v) {
        View vParent;
        TextView place;
        TextView distance;
        vParent = v.getRootView();
        new DownloadImageFromInternet((ImageView) vParent.findViewById(R.id.image_activity))
                .execute(cards.get(index).getPhoto());
        place = vParent.findViewById(R.id.text_place);
        distance = vParent.findViewById(R.id.text_distance);
        place.setText(cards.get(index).getName());
        distance.setText(cards.get(index).getDistance(userLat,userLng) + " km");
    }

    /* Helper function for posting to the appropriate urls based on the user's action
    *  with respect to the current activity
    *
    *  Note: It is assumed that this is called before buttonClickAnimation so that
    *        currIndex reflects the card the user just acted on
    *
    *  Side-note: This also hopefully helps with the asynchronous callbacks although
    *             we may still run into issues when we need more cards */
    private void onClickPostHelper(int action, int currIndex) {
        switch (action) {
            case DISMISS:
                seen(currIndex);
                break;

            case LIKE:
                seen(currIndex);
                liked(currIndex);
                break;

            case SUPERLIKE:
                seen(currIndex);
                liked(currIndex);
                superLiked(currIndex);
                break;

            default:
                // mistakes were made, some other id was somehow encountered
                Toast.makeText(getActivity(), "UH-OH", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /* Helper function for posting to users/userid/seen */
    private void seen(int currIndex) {
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        Call<ResponseBody> rfitResponse = rfit.postSeen(userId, placeids.get(currIndex));
        rfitResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("Success", "Seen Posted");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error Message", "Failed post to users/userid/seen");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Helper function for posting to users/userid/liked */
    private void liked(int currIndex) {
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        Call<ResponseBody> rfitResponse = rfit.postLike(userId, placeids.get(currIndex));
        rfitResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("Success", "Like Posted");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error Message", "Failed post to users/userid/liked");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Helper function for posting to users/userid/bookmarked */
    private void superLiked(int currIndex) {
        RetrofitAPICalls rfit = RetrofitAPIClientUtils.getRetrofitInstance().create(RetrofitAPICalls.class);
        Call<ResponseBody> rfitResponse = rfit.postBookmark(userId, placeids.get(currIndex));
        rfitResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("Success", "Bookmark Posted");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Error Message", "Failed to post to users/userid/bookmarked");
                Toast.makeText(getActivity(), "Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Class that takes care of asynchronously loading the image for the next card */
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}
