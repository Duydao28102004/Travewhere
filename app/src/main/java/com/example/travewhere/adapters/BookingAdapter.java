package com.example.travewhere.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travewhere.R;
import com.example.travewhere.models.Booking;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Room;
import com.example.travewhere.viewmodels.BookingViewModel;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.RoomViewModel;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private final List<Booking> bookingList;
    private final Context context;
    private final HotelViewModel hotelViewModel = new HotelViewModel();
    private final RoomViewModel roomViewModel = new RoomViewModel();
    private final BookingViewModel bookingViewModel = new BookingViewModel();
    private final Map<String, Hotel> hotelCache = new HashMap<>();
    private final Map<String, Room> roomCache = new HashMap<>();

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    public void prefetch(Runnable onComplete) {
        final int[] completedTasks = {0};
        final int totalTasks = 2; // Number of async tasks (Hotels and Rooms)

        // Fetch all hotels
        hotelViewModel.getAllHotels().observe((LifecycleOwner) context, hotels -> {
            if (hotels != null) {
                hotelCache.clear();
                for (Hotel hotel : hotels) {
                    hotelCache.put(hotel.getId(), hotel);
                }
                Log.d("BookingAdapter", "Prefetched " + hotelCache.size() + " hotels");
            } else {
                Log.d("BookingAdapter", "No hotels found");
            }

            completedTasks[0]++;
            if (completedTasks[0] == totalTasks) {
                onComplete.run();
            }
        });

        // Fetch all rooms
        roomViewModel.getAllRooms().observe((LifecycleOwner) context, rooms -> {
            if (rooms != null) {
                roomCache.clear();
                for (Room room : rooms) {
                    roomCache.put(room.getId(), room);
                }
                Log.d("BookingAdapter", "Prefetched " + roomCache.size() + " rooms");
            } else {
                Log.d("BookingAdapter", "No rooms found");
            }

            completedTasks[0]++;
            if (completedTasks[0] == totalTasks) {
                onComplete.run();
            }
        });
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        holder.checkInDate.setText("Check-in: " + dateFormat.format(booking.getCheckInDate()));
        holder.checkOutDate.setText("Check-out: " + dateFormat.format(booking.getCheckOutDate()));
        holder.totalPrice.setText(String.format("Total: %.2f$", booking.getTotalPrice()));

        // Retrieve hotel and room data from cache
        Hotel hotel = hotelCache.get(booking.getHotelId());
        Room room = roomCache.get(booking.getRoomId());

        holder.hotelName.setText(hotel != null ? hotel.getName() : "Loading...");
        holder.roomType.setText(room != null ? room.getRoomType() : "Loading...");

        holder.cancelButton.setOnClickListener(v -> cancelBooking(booking, position));
    }

    private void cancelBooking(Booking booking, int position) {
        long currentTime = System.currentTimeMillis();
        long checkInTime = booking.getCheckInDate().getTime();

        if (currentTime < checkInTime) {
            // Show confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Booking")
                    .setMessage("Are you sure you want to cancel this booking?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Proceed with cancellation
                        bookingList.remove(position);
                        notifyItemRemoved(position);

                        // Remove booking from the database (example with Firebase)
                        bookingViewModel.deleteBooking(booking.getId());

                        Toast.makeText(context, "Booking canceled successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss dialog
                        dialog.dismiss();
                    })
                    .create()
                    .show();
        } else {
            // Notify user that cancellation is not allowed
            Toast.makeText(context, "Cannot cancel booking after check-in date.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView hotelName, roomType, checkInDate, checkOutDate, totalPrice;
        Button cancelButton;


        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.tv_hotel_name);
            roomType = itemView.findViewById(R.id.tv_room_type);
            checkInDate = itemView.findViewById(R.id.tv_check_in_date);
            checkOutDate = itemView.findViewById(R.id.tv_check_out_date);
            totalPrice = itemView.findViewById(R.id.tv_total_price);
            cancelButton = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
