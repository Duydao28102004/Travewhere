package com.example.travewhere;

import com.example.travewhere.models.Hotel;
import com.example.travewhere.models.Manager;
import com.example.travewhere.models.Room;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleData {

    public static List<Hotel> getSampleHotels() {
        Manager manager1 = new Manager("M1", "John Doe", "john.doe@example.com", "1234567890");
        Manager manager2 = new Manager("M2", "Jane Smith", "jane.smith@example.com", "0987654321");

        Room room1 = new Room("R1", "Deluxe Room", 1000000.0, "H1", 2);
        Room room2 = new Room("R2", "Suite Room", 2000000.0, "H1", 2);
        Room room3 = new Room("R3", "Standard Room", 500000.0,"H1", 2);

        List<String> roomList1 = new ArrayList<>(Arrays.asList("1", "2"));
        List<String> roomList2 = new ArrayList<>(Arrays.asList("1", "2"));

        Hotel hotel1 = new Hotel(
                "H1",
                "Grand Plaza Hotel",
                "123 Main Street, Cityville",
                "0123456789",
                "grandplaza@example.com",
                manager1,
                roomList1
        );

        Hotel hotel2 = new Hotel(
                "H2",
                "Luxury Suites",
                "456 Elm Street, Townsville",
                "0987654321",
                "luxurysuites@example.com",
                manager2,
                roomList2
        );

        return new ArrayList<>(Arrays.asList(hotel1, hotel2));
    }
}
