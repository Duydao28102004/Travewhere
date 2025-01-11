package com.example.travewhere;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travewhere.models.Booking;
import com.example.travewhere.models.Coupon;
import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Room;
import com.example.travewhere.repositories.AuthenticationRepository;
import com.example.travewhere.viewmodels.BookingViewModel;
import com.example.travewhere.viewmodels.CouponViewModel;
import com.example.travewhere.viewmodels.HotelViewModel;
import com.example.travewhere.viewmodels.RoomViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BookingActivity extends AppCompatActivity {
    private TextView hotelName, hotelAddress, roomType, roomPrice, totalPrice, appliedCouponTextView;
    private Button btnCheckInTime, btnCheckOutTime, bookNowButton, applyCouponButton;
    private ImageView clearCouponButton;
    private EditText couponCodeEditText;
    private RelativeLayout backbutton, couponDisplayLayout;
    private String roomId;
    private HotelViewModel hotelViewModel = new HotelViewModel();
    private RoomViewModel roomViewModel = new RoomViewModel();
    private BookingViewModel bookingViewModel = new BookingViewModel();
    private CouponViewModel couponViewModel = new CouponViewModel();
    private AuthenticationRepository authenticationRepository = new AuthenticationRepository();
    private Date checkInDate, checkOutDate;
    private double roomPricePerNight;
    private double totalPriceValue;
    private String selectedHotelId;
    private List<Coupon> couponList = new ArrayList<>();
    private Coupon appliedCoupon = null;


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
        backbutton = findViewById(R.id.btnBackLayout);

        backbutton.setOnClickListener(v -> {
            finish();
        });

        // Set onClickListeners for date pickers
        btnCheckInTime.setOnClickListener(v -> showDatePicker((day, month, year) -> {
            checkInDate = createDate(day, month, year);
            String formattedDate = String.format("%02d-%02d-%04d", day, month, year);
            btnCheckInTime.setText("Check-in: " + formattedDate);
            Log.d("BookingActivity", "Check-in Date set: " + checkInDate);
            updateTotalPrice();
        }));

        btnCheckOutTime.setOnClickListener(v -> showDatePicker((day, month, year) -> {
            checkOutDate = createDate(day, month, year);
            String formattedDate = String.format("%02d-%02d-%04d", day, month, year);
            btnCheckOutTime.setText("Check-out: " + formattedDate);
            Log.d("BookingActivity", "Check-out Date set: " + checkOutDate);
            updateTotalPrice();
        }));


        bookNowButton.setOnClickListener(v -> addBooking());

        // Fetch and display hotel and room details
        fetchHotelAndRoomDetails();

        // Fetch coupons
        fetchCoupons();

        clearCouponButton = findViewById(R.id.clear_coupon_button);
        applyCouponButton = findViewById(R.id.apply_coupon_button);
        couponCodeEditText = findViewById(R.id.coupon_code_edit_text);
        appliedCouponTextView = findViewById(R.id.applied_coupon_text_view);
        couponDisplayLayout = findViewById(R.id.coupon_display_layout);

        applyCouponButton.setOnClickListener(v -> {
            if (appliedCoupon != null) {
                Toast.makeText(this, "A coupon is already applied. Clear it to apply another.", Toast.LENGTH_SHORT).show();
                return; // Exit early if a coupon is already applied
            }

            String couponCode = couponCodeEditText.getText().toString().trim();

            if (!couponCode.isEmpty()) {
                // Search the coupon list for a matching coupon code
                Coupon matchedCoupon = null;
                for (Coupon coupon : couponList) {
                    if (coupon.getCode().equalsIgnoreCase(couponCode)) { // Case-insensitive comparison
                        matchedCoupon = coupon;
                        break;
                    }
                }

                if (matchedCoupon != null) {
                    appliedCoupon = matchedCoupon; // Mark the coupon as applied
                    double discountPercent = matchedCoupon.getDiscount(); // Get discount in percentage
                    double discountAmount = (totalPriceValue * discountPercent) / 100;

                    // Display layout and clear button
                    couponDisplayLayout.setVisibility(View.VISIBLE);
                    clearCouponButton.setVisibility(View.VISIBLE);

                    // Update total price
                    totalPriceValue -= discountAmount;

                    // Display the applied coupon and updated total price
                    appliedCouponTextView.setText("Applied Coupon: " + matchedCoupon.getCode() +
                            "\nDiscount: " + discountPercent + "% (-$" + String.format("%.2f", discountAmount) + ")");
                    appliedCouponTextView.setVisibility(View.VISIBLE);

                    totalPrice.setText(String.format("Total Price: %.2f$", totalPriceValue));

                    Toast.makeText(this, "Coupon applied successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Invalid coupon code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
            }
        });


        clearCouponButton.setOnClickListener(v -> {
            // Reset the applied coupon
            appliedCoupon = null; // Clear the applied coupon
            appliedCouponTextView.setText("");
            appliedCouponTextView.setVisibility(View.GONE);

            // Hide the coupon display layout and clear button
            couponDisplayLayout.setVisibility(View.GONE);
            clearCouponButton.setVisibility(View.GONE);

            // Reset total price to the original calculation
            updateTotalPrice();

            // Clear the coupon code input
            couponCodeEditText.setText("");

            Toast.makeText(this, "Coupon cleared", Toast.LENGTH_SHORT).show();
        });

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

    private void fetchCoupons() {
        couponViewModel.getAllCoupons().observe(this, coupons -> {
            couponList.clear();
            if (coupons != null) {
                couponList.addAll(coupons);
            }
        });
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
                        roomViewModel.getRoomsByHotel(selectedHotelId).observe(this, rooms -> {
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
                        break;
                    }
                }

            }
        });
    }

    private void updateTotalPrice() {
        if (roomPricePerNight <= 0) {
            Log.w("BookingActivity", "Room price not initialized yet.");
            return;
        }

        if (checkInDate != null && checkOutDate != null) {
            long diffInMillis = checkOutDate.getTime() - checkInDate.getTime();
            if (diffInMillis < 0) {
                totalPrice.setText("Total Price: Invalid Date Range");
                Log.e("BookingActivity", "Invalid date range: Check-in is after Check-out");
                return;
            }

            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1;
            totalPriceValue = days * roomPricePerNight;

            // Apply the coupon if one is active
            if (appliedCoupon != null) {
                double discountPercent = appliedCoupon.getDiscount();
                double discountAmount = (totalPriceValue * discountPercent) / 100;
                totalPriceValue -= discountAmount; // Apply discount
                appliedCouponTextView.setText(String.format(
                        "Applied Coupon: %s\nDiscount: %.0f%% (-$%.2f)",
                        appliedCoupon.getCode(),
                        discountPercent,
                        discountAmount
                ));
            }

            // Update the total price TextView
            totalPrice.setText(String.format("Total Price: %.2f$ (%d nights)", totalPriceValue, days));
            Log.d("BookingActivity", "Total price updated: " + totalPriceValue);
        } else {
            totalPrice.setText("Total Price: $0.00");
            Log.d("BookingActivity", "Dates not set; total price not updated.");
        }
    }

    // Callback interface for date picking
    private interface DatePickerCallback {
        void onDatePicked(int day, int month, int year);
    }
}
