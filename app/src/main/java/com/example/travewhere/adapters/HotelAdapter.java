package com.example.travewhere.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.travewhere.EditHotelActivity;
import com.example.travewhere.HotelDetailActivity;
import com.example.travewhere.R;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Review;
import com.example.travewhere.models.Room;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.ManagerViewModel;
import com.example.travewhere.viewmodels.ReviewViewModel;
import com.example.travewhere.viewmodels.RoomViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private Context context;
    private List<Hotel> hotelList;
    private boolean isVertical = true;
    private RoomViewModel roomViewModel = new RoomViewModel();
    private ReviewViewModel reviewViewModel = new ReviewViewModel();
    private ManagerViewModel managerViewModel = new ManagerViewModel();
    private AuthenticationRepository authenticationRepository = new AuthenticationRepository();
    private boolean isManager = false;
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

    private Map<String, List<Room>> roomCache = new HashMap<>();
    private Map<String, List<Review>> reviewCache = new HashMap<>();

    // Prefetch all rooms and organize them by hotelId
    public void prefetch(Runnable onComplete) {
        final int[] completedTasks = {0};
        final int totalTasks = 3;

        roomViewModel.getAllRooms().observe((LifecycleOwner) context, rooms -> {
            if (rooms != null) {
                roomCache.clear();
                for (Room room : rooms) {
                    String hotelId = room.getHotelId();
                    roomCache.computeIfAbsent(hotelId, k -> new ArrayList<>()).add(room);
                }
                Log.d("HotelAdapter", "Prefetched rooms for " + roomCache.size() + " hotels");
            } else {
                Log.d("HotelAdapter", "No rooms found");
            }

            // Increment the task counter
            completedTasks[0]++;
            if (completedTasks[0] == totalTasks) {
                onComplete.run();
            }
        });

        reviewViewModel.getAllReviews().observe((LifecycleOwner) context, reviews -> {
            if (reviews != null) {
                reviewCache.clear();
                for (Review review : reviews) {
                    String hotelId = review.getHotelId();
                    reviewCache.computeIfAbsent(hotelId, k -> new ArrayList<>()).add(review);
                }
                Log.d("HotelAdapter", "Prefetched reviews for " + reviewCache.size() + " hotels");
            } else {
                Log.d("HotelAdapter", "No reviews found");
            }

            // Increment the task counter
            completedTasks[0]++;
            if (completedTasks[0] == totalTasks) {
                onComplete.run();
            }
        });

        managerViewModel.getManagerById(authenticationRepository.getCurrentUser().getUid()).observe((LifecycleOwner) context, manager -> {
            if (manager != null) {
                isManager = true;
            } else {
                isManager = false;
            }
            completedTasks[0]++;
            if (completedTasks[0] == totalTasks) {
                onComplete.run();
            }
        });
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
        String address = hotel.getAddress();
        int maxLength = 30; // Set the maximum length of the address
        if (address.length() > maxLength) {
            address = address.substring(0, maxLength) + "...";
        }
        holder.accommodationPosition.setText(address);
        holder.accommodationName.setText(hotel.getName());
        // Display average rating
        List<Review> reviews = reviewCache.get(hotel.getId());
        if (reviews != null && !reviews.isEmpty()) {
            float totalRating = 0;
            for (Review review : reviews) {
                totalRating += review.getRating();
            }
            double averageRating = totalRating / reviews.size();
            holder.ratingTextView.setText(String.format("%.1f/5", averageRating));
            holder.reviewsCountTextView.setText("(" + reviews.size() + ")"); // Example static reviews count, replace with real data if available
        } else {
            holder.ratingTextView.setText("No ratings");
            holder.reviewsCountTextView.setText("(0)");
        }
        // Get rooms for this hotel from the cache
        List<Room> rooms = roomCache.get(hotel.getId());
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

        if (isManager) {
            holder.parentLayout.setOnClickListener(v -> triggerEditIntent(hotel.getId()));
        } else {
            holder.parentLayout.setOnClickListener(v -> triggerDetailIntent(hotel.getId()));
        }
    }

    private void triggerDetailIntent(String hotelId) {
        Intent intent = new Intent(context, HotelDetailActivity.class);
        intent.putExtra("HOTEL_ID", hotelId);
        context.startActivity(intent);
    }

    private void triggerEditIntent(String hotelId) {
        Intent intent = new Intent(context, EditHotelActivity.class);
        intent.putExtra("HOTEL_ID", hotelId);
        context.startActivity(intent);
    }

    public void updateHotelList(List<Hotel> newHotelList) {
        hotelList.clear();
        hotelList.addAll(newHotelList);
        notifyDataSetChanged(); // Notify the adapter to refresh the UI
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

