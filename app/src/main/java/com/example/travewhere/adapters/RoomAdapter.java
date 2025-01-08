package com.example.travewhere.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.BookingActivity;
import com.example.travewhere.R;
import com.example.travewhere.models.Room;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.ManagerViewModel;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<Room> roomList;
    private Context context;
    private ManagerViewModel managerViewModel = new ManagerViewModel();
    private AuthenticationRepository authenticationRepository = new AuthenticationRepository();
    private boolean isManager = false;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    public void prefetch(Runnable oncomplete) {
        managerViewModel.getManagerById(authenticationRepository.getCurrentUser().getUid()).observe((LifecycleOwner) context, manager -> {
            if (manager != null) {
                isManager = true;
            }
            oncomplete.run();
        });
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomTypeTextView.setText("Type: " + room.getRoomType());
        holder.priceTextView.setText("Price: $" + room.getPricePerNight());
        holder.capacityTextView.setText("Capacity: " + room.getCapacity());

        if (isManager) {
            holder.deleteButton.setOnClickListener(v -> {
                roomList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, roomList.size());
            });
            holder.bookButton.setVisibility(View.GONE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
            holder.bookButton.setOnClickListener(v -> {
                Intent intent= new Intent(context, BookingActivity.class);
                intent.putExtra("ROOM_ID", room.getId());
                context.startActivity(intent);
            });
        }
    }

    public void updateHotelList(List<Room> roomList) {
        roomList.clear();
        this.roomList = roomList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomTypeTextView, priceTextView, capacityTextView;
        ImageButton deleteButton;
        Button bookButton;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomTypeTextView = itemView.findViewById(R.id.roomTypeTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            capacityTextView = itemView.findViewById(R.id.capacityTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            bookButton = itemView.findViewById(R.id.bookButton);
        }
    }
}
