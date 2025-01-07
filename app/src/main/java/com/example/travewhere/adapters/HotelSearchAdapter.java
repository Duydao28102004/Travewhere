package com.example.travewhere.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.R;
import com.example.travewhere.models.Hotel;

import java.util.List;

public class HotelSearchAdapter extends RecyclerView.Adapter<HotelSearchAdapter.ViewHolder> {

    private final List<Hotel> hotels;
    private final OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public HotelSearchAdapter(List<Hotel> hotels, OnHotelClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hotel_search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.bind(hotel, listener);
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.searchedHotelName);
            tvAddress = itemView.findViewById(R.id.searchedHotelAddress);
        }

        public void bind(Hotel hotel, OnHotelClickListener listener) {
            tvName.setText(hotel.getName());
            tvAddress.setText(hotel.getAddress());
            itemView.setOnClickListener(v -> listener.onHotelClick(hotel));
        }
    }
}
