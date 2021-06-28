package com.example.cpen321.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cpen321.R;
import com.example.cpen321.fragments.RequestsFragment;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

    public ArrayList<String> requestNames;
    public ArrayList<String> requestIds;
    private RequestsFragment requestsFragment;

    public class RequestsViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private Button accept;
        private Button decline;
        public RequestsViewHolder (View cell) {
            super(cell);
            text = cell.findViewById(R.id.friend_name);
            accept = cell.findViewById(R.id.accept);
            decline = cell.findViewById(R.id.decline);
        }
    }

    public RequestsAdapter (RequestsFragment requestsFragment, ArrayList<String> requestIds, ArrayList<String> requestNames) {
        this.requestIds = requestIds;
        this.requestNames = requestNames;
        this.requestsFragment = requestsFragment;
    }

    @Override
    public RequestsViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request, parent, false);
        return new RequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder (RequestsViewHolder holder, int pos) {
        holder.text.setText(requestNames.get(pos));
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestsFragment.acceptRequest(requestIds.get(pos));
            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestsFragment.declineRequest(requestIds.get(pos));
            }
        });

    }

    @Override
    public int getItemCount () {
        return requestNames.size();
    }

}
