package com.example.cpen321.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.cpen321.R;

import java.util.ArrayList;

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.NewChatViewHolder> {

    public ArrayList<String> membersIds;
    private ArrayList<String> friends;
    private ArrayList<String> friendsIds;

    public class NewChatViewHolder extends RecyclerView.ViewHolder {
        private TextView text;
        private CheckBox checkBox;
        public NewChatViewHolder (View cell) {
            super(cell);
            text = cell.findViewById(R.id.interest);
            checkBox = cell.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        membersIds.add(friendsIds.get(friends.indexOf(text.getText().toString())));
                    }
                    else {
                        membersIds.remove(friendsIds.get(friends.indexOf(text.getText().toString())));
                    }
                }
            });
        }


    }

    public NewChatAdapter (ArrayList<String> friends, ArrayList<String> friendsIds, ArrayList<String> membersIds) {
        this.friends = friends;
        this.membersIds = membersIds;
        this.friendsIds = friendsIds;

    }

    @Override
    public NewChatViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_chat_friend, parent, false);
        return new NewChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder (NewChatViewHolder holder, int pos) {
        holder.text.setText(friends.get(pos));
    }

    @Override
    public int getItemCount () {
        return friends.size();
    }

}
