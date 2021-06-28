package com.example.cpen321.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cpen321.R;
import com.example.cpen321.fragments.SuggestFragment;
import com.example.cpen321.data.Place;

import java.util.ArrayList;

import static com.example.cpen321.MainActivity.userLng;
import static com.example.cpen321.MainActivity.userLat;

public class SuggestedPlacesAdapter extends RecyclerView.Adapter<SuggestedPlacesAdapter.SuggestedPlacesViewHolder> {
    private ArrayList<Place> places;
    private SuggestFragment sf;


    public class SuggestedPlacesViewHolder extends RecyclerView.ViewHolder {
        private TextView place_name;
        private TextView distance;
        private Place suggestedPlace;
        public SuggestedPlacesViewHolder (View place) {
            super(place);
            place_name = place.findViewById(R.id.placeName);
            distance = place.findViewById(R.id.distance);
            place.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sf.createSuggestion(suggestedPlace);
                }
            });
        }
    }

    public SuggestedPlacesAdapter(ArrayList<Place> places, SuggestFragment sf) {
        this.places = places;
        this.sf = sf;
    }


    @Override
    public SuggestedPlacesAdapter.SuggestedPlacesViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_place, parent, false);

        return new SuggestedPlacesAdapter.SuggestedPlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SuggestedPlacesViewHolder holder, int pos) {
        holder.place_name.setText(places.get(pos).getPlaceName());
        String dist = places.get(pos).getDistance(userLat, userLng) + " km";
        holder.distance.setText(dist);
        holder.suggestedPlace = places.get(pos);

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

}
