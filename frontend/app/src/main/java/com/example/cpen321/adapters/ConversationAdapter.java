//package com.example.cpen321;
//
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//import static com.example.cpen321.data.User.userId;
//
//
//public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
//
//    private static final int SENT_MESSAGE = 0;
//    private static final int RECEIVED_MESSAGE = 1;
//    private static final int SENT_SUGGESTION = 2;
//    private static final int RECEIVED_SUGGESTION = 3;
//
//
//    private ArrayList<Message> messages;
//    private ArrayList<VotingEvent> votingEvents;
//
//    public ConversationAdapter(ArrayList<Message> messages, ArrayList<VotingEvent> votingEvents) {
//        this.messages = messages;
//        this.votingEvents = votingEvents;
//
//    }
//
//
//    public class ConversationViewHolder extends RecyclerView.ViewHolder {
//        private TextView message_content;
//        private TextView name_of_sender;
//        private String eventId;
//        public ConversationViewHolder(View message, int viewType) {
//            super(message);
//            message_content = message.findViewById(R.id.message_content);
//            if (viewType == RECEIVED_MESSAGE || viewType == RECEIVED_SUGGESTION)
//                name_of_sender = message.findViewById(R.id.name_of_sender);
//            if (viewType == RECEIVED_SUGGESTION) {
//                Button yes = message.findViewById(R.id.yes);
//                Button no = message.findViewById(R.id.no);
//                yes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // TODO: write onClick
//                    }
//                });
//                no.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // TODO: write onClick
//                    }
//                });
//            }
//        }
//    }
//
//    @Override
//    public ConversationViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
//        View view = null;
//        //we use a different colour and position depending on if the user sent the message or someone else did
//
//        switch (viewType) {
//            case SENT_MESSAGE: view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message, parent, false); break;
//            case RECEIVED_MESSAGE: view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_message, parent, false); break;
//            case SENT_SUGGESTION: view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_suggestion, parent, false); break;
//            case RECEIVED_SUGGESTION: view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_suggestion, parent, false); break;
//            default: break;
//        }
//
//        return new ConversationViewHolder(view, viewType);
//    }
//
//    @Override
//    public void onBindViewHolder(ConversationViewHolder holder, int pos) {
//        int posInArray;
//        String str;
//        switch (getItemViewType(pos)) {
//            case SENT_MESSAGE: break;
//            case RECEIVED_MESSAGE: break;
//            case RECEIVED_SUGGESTION:
//                posInArray = pos - messages.size();
//                str = votingEvents.get(posInArray).getSenderName() + " has suggested " + votingEvents.get(posInArray).getName() + ".";
//                holder.message_content.setText(str);
//                holder.eventId = votingEvents.get(posInArray).getId();
//                break;
//            case SENT_SUGGESTION:
//                posInArray = pos - messages.size();
//                str = "You suggested " + votingEvents.get(posInArray).getName() + ".";
//                holder.message_content.setText(str);
//                break;
//            default:
//                break;
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return messages.size() + votingEvents.size();
//    }
//
//    @Override
//    public int getItemViewType (int pos) {
//        int position = pos;
//        if (position < messages.size()) {
//            if (/*messages.get(position).getSenderId().equals(userId)*/false) {
//                return SENT_MESSAGE;
//            }
//            else {
//                return RECEIVED_MESSAGE;
//            }
//
//        }
//        else {
//            position = position - messages.size();
//            if (votingEvents.get(position).getSenderId().equals(userId)) {
//                return SENT_SUGGESTION;
//            }
//            else {
//                return RECEIVED_SUGGESTION;
//            }
//
//        }
//    }
//
//}
