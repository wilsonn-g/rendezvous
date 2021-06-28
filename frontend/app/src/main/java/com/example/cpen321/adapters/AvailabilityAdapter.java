package com.example.cpen321.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cpen321.R;

import java.util.ArrayList;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.AvailabilityViewHolder> {
    private ArrayList<Boolean> available;
    private static final int CHECK_BOX = 0;
    private static final int DAY_OF_WEEK = 1;
    private static final int TIME_OF_DAY = 2;

    public AvailabilityAdapter(ArrayList<Boolean> available) {
        this.available = available;
    }


    @NonNull
    @Override
    public AvailabilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TIME_OF_DAY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availability_border_cell, parent, false);
        }
        else if (viewType == DAY_OF_WEEK) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availability_border_cell, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.availability_cell, parent, false);
        }

        return new AvailabilityViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailabilityViewHolder holder, int pos) {
        if (getItemViewType(pos) == TIME_OF_DAY) {
            String str;
            if (pos % 16 == 0)
                str =(pos / 16 - 1) + ":30";
            else
                str = (pos / 16) + ":00";
            holder.text.setText(str);
        }
        else if (getItemViewType(pos) == DAY_OF_WEEK) {
            if (pos == 0)
                return;
            String [] days = {"M", "T", "W", "T", "F", "S", "S"};
            holder.text.setText(days[pos-1]);
        }
        else {
            holder.box.setChecked(available.get(conv(pos)));
            holder.box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    available.set(conv(pos),holder.box.isChecked());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 8)
            return DAY_OF_WEEK;
        else if (position % 8 == 0)
            return TIME_OF_DAY;
        else
            return CHECK_BOX;
    }

    @Override
    public int getItemCount() {
        if (available.isEmpty())
            return 0;
        else
            return 392;
    }

    public class AvailabilityViewHolder extends RecyclerView.ViewHolder {
        private CheckBox box;
        private TextView text;
        public AvailabilityViewHolder (View cell, int itemType) {
            super(cell);
            if (itemType == TIME_OF_DAY || itemType == DAY_OF_WEEK) {
                box = null;
                text = cell.findViewById(R.id.textView);
            }
            else {
                box = cell.findViewById(R.id.avail);
                text = null;
            }
        }
    }

    private int conv(int pos) {
      int row = pos / 8;
      int col = pos % 8;
      return (col - 1) * 48 + row - 1;
    }


}
