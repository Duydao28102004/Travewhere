package com.example.travewhere;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.adapters.ChatAdapter;
import com.example.travewhere.models.Message;
import com.example.travewhere.receivers.NetworkChangeReceiver;
import com.example.travewhere.viewmodels.CustomerViewModel;
import com.example.travewhere.viewmodels.ManagerViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private RelativeLayout btnBackLayout;
    private TextView tvPageTitle;
    private EditText messageEditText;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();

    private ManagerViewModel managerViewModel = new ManagerViewModel();
    private CustomerViewModel customerViewModel = new CustomerViewModel();
    private DatabaseReference chatDatabase;
    private String currentUserId;
    private String chatId;
    private String receiverId;

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        btnBackLayout = findViewById(R.id.btnBackLayout);
        tvPageTitle = findViewById(R.id.tvPageTitle);

        chatId = getIntent().getStringExtra("chatId");
        currentUserId = getIntent().getStringExtra("currentUserId");
        receiverId = getIntent().getStringExtra("receiverId");

        if (chatId == null || currentUserId == null || receiverId == null) {
            Toast.makeText(this, "Missing chat or user information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        managerViewModel.getManagerById(receiverId).observe(this, manager -> {
            if (manager != null) {
                tvPageTitle.setText(manager.getName());
            }
        });

        customerViewModel.getCustomerById(receiverId).observe(this, customer -> {
            if (customer != null) {
                tvPageTitle.setText(customer.getName());
            }
        });
        btnBackLayout.setOnClickListener(v -> finish());

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatDatabase = FirebaseDatabase.getInstance().getReference("chats");

        // Retrieve chatId, currentUserId, and receiverId from intent


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

    @Override
    protected void onStart() {
        super.onStart();
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }
}
