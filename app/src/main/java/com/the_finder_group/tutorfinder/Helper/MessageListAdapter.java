package com.the_finder_group.tutorfinder.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.the_finder_group.tutorfinder.ConnManager.UserMessageDTO;
import com.the_finder_group.tutorfinder.R;

import java.util.HashMap;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<UserMessageDTO> mMessageList;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private SQLiteHandler db;
    private Integer db_user_id;

    public MessageListAdapter(Context context, List<UserMessageDTO> messageList) {
        mContext = context;
        mMessageList = messageList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        // SqLite database handler
        db = new SQLiteHandler(mContext);
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        //Id de l'usuari que esta realitzant lel registre
        db_user_id = Integer.parseInt(user.get("user_id"));

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_message_sent, viewGroup, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_message_received, viewGroup, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final UserMessageDTO message = (UserMessageDTO) mMessageList.get(position);

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        final UserMessageDTO message = (UserMessageDTO) mMessageList.get(position);

        if (message.getMessageUserId()== db_user_id) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(UserMessageDTO message) {
            messageText.setText(message.getMessageText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(String.valueOf(message.getMessageDate()));
            nameText.setText(message.getMessageUserName());

            // Insert the profile image from the URL into the ImageView.
            Bitmap imageBtmp;
            if ((imageBtmp = (db.getImagePath(message.getMessageUserId())))!=null) {
                profileImage.setImageBitmap(imageBtmp);
            }
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(UserMessageDTO message) {

            messageText.setText(message.getMessageText());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(String.valueOf(message.getMessageDate()));
        }
    }

}