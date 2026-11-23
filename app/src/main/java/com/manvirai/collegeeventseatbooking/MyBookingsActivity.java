
package com.manvirai.collegeeventseatbooking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyBookingsActivity extends AppCompatActivity {

    LinearLayout bookingsContainer;
    TextView tvNoBookings;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookingsContainer = findViewById(R.id.bookingsContainer);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        btnBack = findViewById(R.id.btnBack);

        displayBookings();

        btnBack.setOnClickListener(v -> finish());
    }

    private void displayBookings() {

        bookingsContainer.removeAllViews();

        ArrayList<String> bookings = BookingData.bookingList;

        if (bookings.isEmpty()) {

            tvNoBookings.setVisibility(View.VISIBLE);
            tvNoBookings.setText("You have no bookings yet.");

        } else {

            tvNoBookings.setVisibility(View.GONE);

            for (int i = 0; i < bookings.size(); i++) {

                final int bookingIndex = i;

                LinearLayout bookingCard = new LinearLayout(this);
                bookingCard.setOrientation(LinearLayout.VERTICAL);
                bookingCard.setPadding(20, 20, 20, 20);
                bookingCard.setBackgroundColor(0xFFFFFFFF);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                cardParams.setMargins(0, 10, 0, 10);
                bookingCard.setLayoutParams(cardParams);

                TextView bookingDetails = new TextView(this);
                bookingDetails.setText(bookings.get(i));
                bookingDetails.setTextSize(16);
                bookingDetails.setTextColor(0xFF222222);

                bookingCard.addView(bookingDetails);

                Button cancelButton = new Button(this);
                cancelButton.setText("Cancel Booking");

                bookingCard.addView(cancelButton);

                cancelButton.setOnClickListener(v -> {

                    if (bookingIndex >= BookingData.bookingList.size()) {
                        return;
                    }

                    String details =
                            BookingData.bookingList.get(bookingIndex);

                    int start = details.indexOf("Seats: ");
                    int end = details.indexOf("\nTickets:");

                    if (start == -1 || end == -1 || end < start) {
                        Toast.makeText(this,
                                "Unable to cancel this booking.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String seatLine = details.substring(start + 7, end);

                    String cleanedSeats = seatLine
                            .replace("[", "")
                            .replace("]", "")
                            .replace(" ", "");

                    String[] seats = cleanedSeats.split(",");

                    // Make cancelled seats available again
                    for (String seat : seats) {
                        BookingData.bookedSeatList.remove(seat);
                    }

                    // Remove the booking
                    BookingData.bookingList.remove(bookingIndex);

                    Toast.makeText(this,
                            "Booking cancelled successfully!",
                            Toast.LENGTH_SHORT).show();

                    displayBookings();
                });

                bookingsContainer.addView(bookingCard);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bookingsContainer != null) {
            displayBookings();
        }
    }
}
