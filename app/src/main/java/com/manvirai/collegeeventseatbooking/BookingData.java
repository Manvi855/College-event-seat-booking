
package com.manvirai.collegeeventseatbooking;

import java.util.ArrayList;

public class BookingData {

    public static ArrayList<String> bookedSeatList = new ArrayList<>();

    public static ArrayList<String> bookingList = new ArrayList<>();

    public static void addBooking(String bookingDetails) {
        bookingList.add(bookingDetails);
    }
}

