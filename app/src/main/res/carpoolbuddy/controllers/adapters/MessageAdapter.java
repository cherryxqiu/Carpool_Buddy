package com.example.carpoolbuddy.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(Message message);
    }

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_row, parent, false);
        int width = parent.getMeasuredWidth();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.usernameTextView.setText(message.getSender());
        holder.messagePreviewTextView.setText(message.getPreview());

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onItemClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView usernameTextView;
        TextView messagePreviewTextView;
        View dividerLine;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messagePreviewTextView = itemView.findViewById(R.id.messagePreviewTextView);
        }
    }
}