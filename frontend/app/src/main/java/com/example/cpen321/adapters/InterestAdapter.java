package com.example.cpen321.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.cpen321.fragments.InterestFragment;
import com.example.cpen321.R;

import java.util.ArrayList;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {

    private ArrayList<String> potentialInterests;
    public ArrayList<String> chosenInterests;
    private InterestFragment interestFragment;

    public class InterestViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private CheckBox checkBox;
        public InterestViewHolder (View cell) {
            super(cell);
            text = cell.findViewById(R.id.interest);
            checkBox = cell.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        if (!(chosenInterests.contains(interestFragment.interestBackEndRep(text.getText().toString()))))
                            chosenInterests.add(interestFragment.interestBackEndRep(text.getText().toString()));
                    }
                    else {
                        chosenInterests.remove(interestFragment.interestBackEndRep(text.getText().toString()));
                    }
                }
            });
        }


    }

    public InterestAdapter (InterestFragment interestFragment, ArrayList<String> potentialInterests, ArrayList<String> chosenInterests) {
        this.potentialInterests = potentialInterests;
        this.chosenInterests = chosenInterests;
        this.interestFragment = interestFragment;

    }

    @Override
    public InterestViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest, parent, false);
        return new InterestViewHolder(view);
    }

    @Override
    public void onBindViewHolder (InterestViewHolder holder, int pos) {
        holder.text.setText(potentialInterests.get(pos));
        if (chosenInterests.contains(interestFragment.interestBackEndRep(potentialInterests.get(pos)))) {
            holder.checkBox.setChecked(true);
        }

    }

    @Override
    public int getItemCount () {
        return potentialInterests.size();
    }

}
