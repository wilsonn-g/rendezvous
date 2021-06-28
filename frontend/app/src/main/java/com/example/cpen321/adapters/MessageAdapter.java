package com.example.cpen321.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cpen321.R;
import com.example.cpen321.data.VotingEvent;
import com.example.cpen321.data.Message;
import com.example.cpen321.fragments.ConversationFragment;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.example.cpen321.data.User.userId;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private List<VotingEvent> votingEvents;
    private int[] mUsernameColors;
    private final static int RECEIVED_SUGGESTION = 11;
    private final static int RECEIVED_SUGGESTION_VOTED = 13;
    private final static int SENT_SUGGESTION = 12;
    private ConversationFragment cf;


    public MessageAdapter(Context context, List<Message> messages, List<VotingEvent> votingEvents, ConversationFragment cf) {
        mMessages = messages;
        this.votingEvents = votingEvents;
        mUsernameColors = context.getResources().getIntArray(R.array.username_colors);
        this.cf = cf;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
        case Message.TYPE_MESSAGE:
            layout = R.layout.item_message;
            break;
        case Message.TYPE_LOG:
            layout = R.layout.item_log;
            break;
        case Message.TYPE_ACTION:
            layout = R.layout.item_action;
            break;
        case SENT_SUGGESTION:
            layout = R.layout.sent_suggestion;
            break;
        case RECEIVED_SUGGESTION:
            layout = R.layout.received_suggestion;
            break;
        case RECEIVED_SUGGESTION_VOTED:
            layout = R.layout.received_suggestion_voted;
            break;
            default:
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == SENT_SUGGESTION) {
            Date date = new Date(votingEvents.get(position - mMessages.size()).getDatetime());
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datetime = sdf.format(date);
            String message = "I suggest " + votingEvents.get(position - mMessages.size()).getPlaceName() + " at " + datetime.substring(6) + " on " + datetime.substring(0,5);
            viewHolder.setMessage(message);
            new DownloadImageFromInternet(viewHolder.pic)
                    .execute(votingEvents.get(position - mMessages.size()).getPhoto());
        }
        else if (getItemViewType(position) == RECEIVED_SUGGESTION) {
            Date date = new Date(votingEvents.get(position - mMessages.size()).getDatetime());
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datetime = sdf.format(date);
            String message = votingEvents.get(position - mMessages.size()).getSenderName() + "suggests " + votingEvents.get(position - mMessages.size()).getPlaceName() + " at " + datetime.substring(6) + " on " + datetime.substring(0,5);
            viewHolder.setMessage(message);
            viewHolder.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cf.voteYes(votingEvents.get(position - mMessages.size()));
                }
            });
            viewHolder.no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cf.voteNo(votingEvents.get(position - mMessages.size()));
                }
            });
            new DownloadImageFromInternet(viewHolder.pic)
                    .execute(votingEvents.get(position - mMessages.size()).getPhoto());
        }
        else if (getItemViewType(position) == RECEIVED_SUGGESTION_VOTED) {
            Date date = new Date(votingEvents.get(position - mMessages.size()).getDatetime());
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datetime = sdf.format(date);
            String message = votingEvents.get(position - mMessages.size()).getSenderName() + "suggests " + votingEvents.get(position - mMessages.size()).getPlaceName() + " at " + datetime.substring(6) + " on " + datetime.substring(0,5);
            viewHolder.setMessage(message);
            new DownloadImageFromInternet(viewHolder.pic)
                    .execute(votingEvents.get(position - mMessages.size()).getPhoto());
        }
        else {
            Message message = mMessages.get(position);
            viewHolder.setMessage(message.getMessage());
            viewHolder.setUsername(message.getUsername());
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size() + votingEvents.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mMessages.size())
            return mMessages.get(position).getType();
        else {
            int newPos = position - mMessages.size();
            if (votingEvents.get(newPos).getSenderId().equals(userId))
                return SENT_SUGGESTION;
            else if (votingEvents.get(newPos).getMembersVoted().contains(userId))
                return RECEIVED_SUGGESTION_VOTED;
            else
                return RECEIVED_SUGGESTION;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mUsernameView;
        private TextView mMessageView;
        private Button yes;
        private Button no;
        private ImageView pic;

        public ViewHolder(View itemView, int itemType) {
            super(itemView);
            if (itemType == SENT_SUGGESTION) {
                mMessageView = itemView.findViewById(R.id.message_content);
                pic = itemView.findViewById(R.id.image_activity);
            }
            else if (itemType == RECEIVED_SUGGESTION) {
                mUsernameView = itemView.findViewById(R.id.name_of_sender);
                mMessageView = itemView.findViewById(R.id.message_content);
                yes = itemView.findViewById(R.id.yes);
                no = itemView.findViewById(R.id.no);
                pic = itemView.findViewById(R.id.image_activity);
            }
            else if (itemType == RECEIVED_SUGGESTION_VOTED) {
                mMessageView = itemView.findViewById(R.id.message_content);
                pic = itemView.findViewById(R.id.image_activity);
            }
            else {
                mUsernameView = (TextView) itemView.findViewById(R.id.username);
                mMessageView = (TextView) itemView.findViewById(R.id.message);
            }
        }

        public void setUsername(String username) {
            if (null == mUsernameView) return;
            mUsernameView.setText(username);
            mUsernameView.setTextColor(getUsernameColor(username));
        }

        public void setMessage(String message) {
            if (null == mMessageView) return;
            mMessageView.setText(message);
        }

        private int getUsernameColor(String username) {
            int hash = 7;
            for (int i = 0, len = username.length(); i < len; i++) {
                hash = username.codePointAt(i) + (hash << 5) - hash;
            }
            int index = Math.abs(hash % mUsernameColors.length);
            return mUsernameColors[index];
        }
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
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
