package com.example.cpen321.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cpen321.R;
import com.example.cpen321.fragments.AddFriendFragment;

import java.util.ArrayList;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.AddFriendViewHolder> {

    public ArrayList<String> searchResults;
    public ArrayList<String> searchResultsIds;
    private AddFriendFragment addFriendFragment;

    public class AddFriendViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private Button add;
        public AddFriendViewHolder (View cell) {
            super(cell);
            text = cell.findViewById(R.id.friend_name);
            add = cell.findViewById(R.id.add);
        }
    }

    public AddFriendAdapter (AddFriendFragment addFriendFragment, ArrayList<String> searchResults, ArrayList<String> searchResultsIds) {
        this.searchResults = searchResults;
        this.addFriendFragment = addFriendFragment;
        this.searchResultsIds = searchResultsIds;
    }

    @Override
    public AddFriendViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result, parent, false);
        return new AddFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder (AddFriendViewHolder holder, int pos) {
        holder.text.setText(searchResults.get(pos));
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriendFragment.addFriend(searchResultsIds.get(pos));
            }
        });
    }

    @Override
    public int getItemCount () {
        return searchResults.size();
    }

}
