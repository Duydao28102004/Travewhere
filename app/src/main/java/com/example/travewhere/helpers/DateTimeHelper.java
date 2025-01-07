package com.example.travewhere.helpers;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.Button;

import java.util.Calendar;

public class DateTimeHelper {

    public static void showDateTimePicker(Context context, Button button) {
        Calendar calendar = Calendar.getInstance();

        // Get current date and time
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (dateView, selectedYear, selectedMonth, selectedDay) -> {
            // After date is picked, show TimePickerDialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (timeView, selectedHour, selectedMinute) -> {
                // Format and set date and time on the button
                String formattedDateTime = String.format("%04d-%02d-%02d %02d:%02d",
                        selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute);
                button.setText(formattedDateTime);
            }, hour, minute, false);
            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }
}
