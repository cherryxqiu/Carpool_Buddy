package com.example.carpoolbuddy.carpoolbuddy.controllers.profile;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.controllers.adapters.MessageAdapter;
import com.example.carpoolbuddy.controllers.fragments.MessageDialogFragment;
import com.example.carpoolbuddy.models.Message;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageMainActivity extends AppCompatActivity implements MessageAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("messages").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Message> messages = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Message message = document.toObject(Message.class);
                    messages.add(message);
                    System.out.println(message.getPreview());
                }

                messageAdapter = new MessageAdapter(MessageMainActivity.this, messages);
                messageAdapter.setOnItemClickListener(MessageMainActivity.this);
                recyclerView.setAdapter(messageAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(MessageMainActivity.this, DividerItemDecoration.VERTICAL));
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }

    @Override
    public void onItemClick(Message message) {
        // Create an instance of the MessageDialogFragment
        MessageDialogFragment dialogFragment = MessageDialogFragment.newInstance(message.getContent(), message.getSender());

        // Show the dialog fragment
        dialogFragment.show(getSupportFragmentManager(), "message_dialog");
    }

    public void back(View w) {
//        Intent intent = new Intent(MessageMainActivity.this, MainActivity.class);
//        intent.putExtra("fragmentToLoad", "profile");
//        startActivity(intent);
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}