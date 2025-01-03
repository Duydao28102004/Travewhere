package com.example.travewhere.helpers;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.Button;

import java.util.Calendar;

public class DateTimeHelper {

    public static void showTimePicker(Context context, Button button) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, selectedHour, selectedMinute) -> {
            String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            button.setText(formattedTime);
        }, hour, minute, false);

        timePickerDialog.show();
    }
}
