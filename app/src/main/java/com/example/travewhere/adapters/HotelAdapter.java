package com.example.travewhere.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.R;
import com.example.travewhere.models.Hotel;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private Context context;
    private List<Hotel> hotelList;

    public HotelAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hotel_item, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        // Assuming you have drawable or URI for hotel images
        holder.imageView.setImageResource(R.drawable.testingimage); // Replace with dynamic image loading
        holder.accommodationPosition.setText(hotel.getAddress());
        holder.accommodationName.setText(hotel.getName());
        holder.ratingTextView.setText("10/10"); // Example static rating, replace with real data if available
        holder.reviewsCountTextView.setText("(85)"); // Example static reviews count, replace with real data if available
        holder.priceTextView.setText("500.000 VND"); // Replace with dynamic price data
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView accommodationPosition;
        TextView accommodationName;
        TextView ratingTextView;
        TextView reviewsCountTextView;
        TextView priceTextView;
        TextView taxTextView;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.accommodationImage);
            accommodationPosition = itemView.findViewById(R.id.accommodationPosition);
            accommodationName = itemView.findViewById(R.id.accommodationName);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            reviewsCountTextView = itemView.findViewById(R.id.reviewsCountTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
