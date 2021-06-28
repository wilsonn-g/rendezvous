package com.example.cpen321.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cpen321.data.Group;
import com.example.cpen321.R;
import com.example.cpen321.fragments.ChatsFragment;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    private ArrayList<Group> groups;

    private ChatsFragment cf;

    public class ChatsViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView msg;
        private String groupId;
        public ChatsViewHolder(View cell) {
            super(cell);
            name = cell.findViewById(R.id.name);
            msg = cell.findViewById(R.id.msg);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newConversationFragment(view, groupId);
                }
            });
        }
    }

    public ChatsAdapter(ArrayList<Group> groups, ChatsFragment cf) {
        this.groups = groups;
        this.cf = cf;
    }

    @Override
    public ChatsViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_cell, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int pos) {
        holder.name.setText(groups.get(pos).getGroupName());
        String msgWithSender = "";
        if (!(groups.get(pos).getLastMessage().equals("")))
            msgWithSender = groups.get(pos).getLastSender() + ": " + groups.get(pos).getLastMessage();
        holder.msg.setText(msgWithSender);
        holder.groupId = groups.get(pos).getId();
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    private void newConversationFragment(View chat_cell, String groupId) {
        TextView groupName = chat_cell.findViewById(R.id.name);
        String name = (String)groupName.getText();

        cf.loadConversation(name, groupId);

    }


}
