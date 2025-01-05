package com.example.travewhere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travewhere.R;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Room;
import com.example.travewhere.viewmodels.RoomViewModel;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private Context context;
    private List<Hotel> hotelList;
    private boolean isVertical = true;
    private RoomViewModel roomViewModel = new RoomViewModel();

    public HotelAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    // Function to switch layout orientation
    public void setOrientation(boolean isVertical) {
        this.isVertical = isVertical;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return isVertical ? 1 : 0;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.hotel_item_vertical, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.hotel_item_horizontal, parent, false);
        }
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);

        // Load the image from the Firebase Storage URL
        String imageUrl = hotel.getImageUrl(); // Assuming you have an `imageUrl` field in your Hotel class
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.testingimage) // Placeholder while loading
                .error(R.drawable.testingimage) // Fallback in case of error
                .into(holder.imageView);

        // Assuming you have drawable or URI for hotel images
        holder.accommodationPosition.setText(hotel.getAddress());
        holder.accommodationName.setText(hotel.getName());
        holder.ratingTextView.setText("10/10"); // Example static rating, replace with real data if available
        holder.reviewsCountTextView.setText("(85)"); // Example static reviews count, replace with real data if available
        roomViewModel.getRoomsByHotel(hotel.getId()).observe((LifecycleOwner) context, rooms -> {
            if (rooms != null && !rooms.isEmpty()) {
                double lowestPrice = rooms.get(0).getPricePerNight();
                for (Room room : rooms) {
                    if (room.getPricePerNight() < lowestPrice) {
                        lowestPrice = room.getPricePerNight();
                    }
                }
                holder.priceTextView.setText("$" + lowestPrice);
            } else {
                holder.priceTextView.setText("Price not available");
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentLayout;
        ImageView imageView;
        TextView accommodationPosition;
        TextView accommodationName;
        TextView ratingTextView;
        TextView reviewsCountTextView;
        TextView priceTextView;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout); // Root LinearLayout
            imageView = itemView.findViewById(R.id.accommodationImage);
            accommodationPosition = itemView.findViewById(R.id.accommodationPosition);
            accommodationName = itemView.findViewById(R.id.accommodationName);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            reviewsCountTextView = itemView.findViewById(R.id.reviewsCountTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
