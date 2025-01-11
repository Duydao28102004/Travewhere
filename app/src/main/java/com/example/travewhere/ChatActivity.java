package com.example.travewhere;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.ChatAdapter;
import com.example.travewhere.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();

    private DatabaseReference chatDatabase;
    private String currentUserId;
    private String chatId;
    private String receiverId; // Receiver's user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatDatabase = FirebaseDatabase.getInstance().getReference("chats");

        // Retrieve chatId, currentUserId, and receiverId from intent
        chatId = getIntent().getStringExtra("chatId");
        currentUserId = getIntent().getStringExtra("currentUserId");
        receiverId = getIntent().getStringExtra("receiverId");

        if (chatId == null || currentUserId == null || receiverId == null) {
            Toast.makeText(this, "Missing chat or user information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatAdapter = new ChatAdapter(messageList, currentUserId);
        chatRecyclerView.setAdapter(chatAdapter);

        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        chatDatabase.child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.post(() -> chatRecyclerView.scrollToPosition(messageList.size() - 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatActivity", "Error loading messages: " + error.getMessage());
            }
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        Message message = new Message(currentUserId, receiverId, messageText, timestamp);

        String messageId = chatDatabase.child(chatId).child("messages").push().getKey();

        if (messageId == null) {
            Log.e("ChatActivity", "Failed to generate message ID");
            Toast.makeText(this, "Failed to generate message ID", Toast.LENGTH_SHORT).show();
            return;
        }

        chatDatabase.child(chatId).child("messages").child(messageId).setValue(message)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ChatActivity", "Message sent successfully");
                        runOnUiThread(() -> messageEditText.setText(""));
                    } else {
                        Log.e("ChatActivity", "Failed to send message: " + task.getException());
                        Toast.makeText(this, "Failed to send message: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        String messagePath = chatDatabase.child(chatId).child("messages").child(messageId).toString();
        Log.d("FirebasePath", "Database path: " + messagePath);
    }



    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
