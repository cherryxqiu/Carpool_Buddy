package com.example.carpoolbuddy.controllers.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.carpoolbuddy.R;

public class MessageDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE_TEXT = "message_text";
    private static final String ARG_SENDER = "sender";

    public static MessageDialogFragment newInstance(String messageText, String sender) {
        MessageDialogFragment fragment = new MessageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE_TEXT, messageText);
        args.putString(ARG_SENDER, sender);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_message_details, null);

        // Get the message details from arguments
        String messageText = getArguments().getString(ARG_MESSAGE_TEXT);
        String sender = getArguments().getString(ARG_SENDER);

        // Set the message details in the dialog view
        TextView senderTextView = view.findViewById(R.id.senderTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
        senderTextView.setText(sender);
        messageTextView.setText(messageText);

        builder.setView(view)
                .setTitle("Message Details")
                .setPositiveButton("Close", (dialog, which) -> dismiss());

        return builder.create();
    }
}