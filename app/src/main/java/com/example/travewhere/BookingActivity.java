package com.example.travewhere;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travewhere.models.Booking;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Room;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.BookingViewModel;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.RoomViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {
    private TextView hotelName, hotelAddress, roomType, roomPrice, totalPrice;
    private Button btnCheckInTime, btnCheckOutTime, bookNowButton;
    private String roomId;
    private HotelViewModel hotelViewModel = new HotelViewModel();
    private RoomViewModel roomViewModel = new RoomViewModel();
    private BookingViewModel bookingViewModel = new BookingViewModel();
    private AuthenticationRepository authenticationRepository = new AuthenticationRepository();
    private Date checkInDate, checkOutDate;
    private double roomPricePerNight;
    private double totalPriceValue;
    private String selectedHotelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        getSupportActionBar().hide();

        // Retrieve room ID passed via Intent
        roomId = getIntent().getStringExtra("ROOM_ID");

        // Initialize UI elements
        hotelName = findViewById(R.id.hotel_name_text_view);
        hotelAddress = findViewById(R.id.hotel_address_text_view);
        roomType = findViewById(R.id.room_type_text_view);
        roomPrice = findViewById(R.id.room_price_text_view);
        btnCheckInTime = findViewById(R.id.btnCheckInTime);
        btnCheckOutTime = findViewById(R.id.btnCheckOutTime);
        totalPrice = findViewById(R.id.total_price_text_view);
        bookNowButton = findViewById(R.id.book_now_button);

        // Set onClickListeners for date pickers
        btnCheckInTime.setOnClickListener(v -> showDatePicker((day, month, year) -> {
            checkInDate = createDate(day, month, year);
            String formattedDate = String.format("%02d-%02d-%04d", day, month, year);
            btnCheckInTime.setText("Check-in: " + formattedDate);
            updateTotalPrice();
        }));

        btnCheckOutTime.setOnClickListener(v -> showDatePicker((day, month, year) -> {
            checkOutDate = createDate(day, month, year);
            String formattedDate = String.format("%02d-%02d-%04d", day, month, year);
            btnCheckOutTime.setText("Check-out: " + formattedDate);
            updateTotalPrice();
        }));

        bookNowButton.setOnClickListener(v -> addBooking());

        // Fetch and display hotel and room details
        fetchHotelAndRoomDetails();
    }

    private void addBooking() {
        // Validate all data is entered
        if (checkInDate == null || checkOutDate == null) {
            Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkOutDate.before(checkInDate)) {
            Toast.makeText(this, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
            return;
        }

        bookingViewModel.addBooking(new Booking(bookingViewModel.getUID(), authenticationRepository.getCurrentUser().getUid(), roomId, selectedHotelId, checkInDate, checkOutDate, totalPriceValue));
        Toast.makeText(this, "Booking added successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showDatePicker(DatePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new android.app.DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Trigger callback with selected values
            callback.onDatePicked(selectedDay, selectedMonth + 1, selectedYear);
        }, year, month, day).show();
    }

    private Date createDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(); // Clear all time fields to ensure they are set to zero
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Months are 0-based in Calendar
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    private void fetchHotelAndRoomDetails() {
        hotelViewModel.getAllHotels().observe(this, hotels -> {
            for (Hotel hotel : hotels) {
                for (String roomId : hotel.getRoomIdList()) {
                    if (roomId.equals(this.roomId)) {
                        hotelName.setText(hotel.getName());
                        hotelAddress.setText(hotel.getAddress());
                        selectedHotelId = hotel.getId();
                        break;
                    }
                }
                roomViewModel.getRoomsByHotel(hotel.getId()).observe(this, rooms -> {
                    for (Room room : rooms) {
                        if (room.getId().equals(roomId)) {
                            roomType.setText(room.getRoomType());
                            roomPricePerNight = room.getPricePerNight();
                            roomPrice.setText(String.format("%s$ per night", roomPricePerNight));
                            updateTotalPrice();
                            break;
                        }
                    }
                });
            }
        });
    }

    private void updateTotalPrice() {
        if (checkInDate != null && checkOutDate != null) {
            long diffInMillis = checkOutDate.getTime() - checkInDate.getTime();
            System.out.println("Time Difference in Millis: " + diffInMillis);

            if (diffInMillis < 0) {
                totalPrice.setText("Total Price: Invalid Date Range");
                return;
            }

            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1;
            double totalCost = days * roomPricePerNight;
            totalPriceValue = totalCost;
            totalPrice.setText(String.format("Total Price: %.2f$", totalCost));
        } else {
            totalPrice.setText("Total Price: $0.00");
        }
    }

    // Callback interface for date picking
    private interface DatePickerCallback {
        void onDatePicked(int day, int month, int year);
    }
}
