package com.manvirai.collegeeventseatbooking;

import java.io.Serializable;

public class Models {

    public static class Hall implements Serializable {
        public int id;
        public String name;
        public int rowsCount;
        public int colsCount;
        public String layoutType;

        public int getCapacity() {
            return rowsCount * colsCount;
        }

        @Override
        public String toString() {
            return name + " (" + getCapacity() + " seats)";
        }
    }

    public static class EventItem implements Serializable {
        public int id;
        public String title;
        public String description;
        public String eventDate;
        public String eventTime;
        public String venue;
        public String posterType;
        public int hallId;
        public String hallName;
        public boolean isPaid;
        public double fee;
    }

    public static class BookingSummary {
        public int id;
        public String bookingCode;
        public String studentName;
        public String eventTitle;
        public String hallName;
        public String seatLabel;
        public double amountPaid;
        public String status;
    }
}