
package com.manvirai.collegeeventseatbooking;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SeatBookingActivity extends AppCompatActivity {

    LinearLayout seatContainer;
    TextView tvSelectedSeats, tvTotalAmount;
    Button btnBookNow;

    ArrayList<String> selectedSeats = new ArrayList<>();
    Set<String> bookedSeats = new HashSet<>();

    int seatPrice = 200;

    int rows = 5;
    int seatsPerRow = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking);

        seatContainer = findViewById(R.id.seatContainer);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnBookNow = findViewById(R.id.btnBookNow);

        // Add previously booked seats
        bookedSeats.addAll(BookingData.bookedSeatList);

        // Sample booked seats
        bookedSeats.add("A3");
        bookedSeats.add("B4");
        bookedSeats.add("C2");
        bookedSeats.add("D5");

        generateSeats();

        updateBookingDetails();

        // Book Now button
        btnBookNow.setOnClickListener(v -> {

            if (selectedSeats.isEmpty()) {

                Toast.makeText(this,
                        "Please select at least one seat",
                        Toast.LENGTH_SHORT).show();

            } else {

                // Prepare booking details
                String bookingDetails =
                        "Event: Tech Fest 2026\n" +
                                "Seats: " + selectedSeats + "\n" +
                                "Tickets: " + selectedSeats.size() + "\n" +
                                "Amount: ₹" + (selectedSeats.size() * seatPrice);

                // Save booking details
                BookingData.addBooking(bookingDetails);

                // Save booked seats
                for (String seat : selectedSeats) {

                    if (!BookingData.bookedSeatList.contains(seat)) {
                        BookingData.bookedSeatList.add(seat);
                    }

                    bookedSeats.add(seat);
                }

                // Show confirmation
                Toast.makeText(this,
                        "Booking successful!",
                        Toast.LENGTH_LONG).show();

                // Clear selected seats
                selectedSeats.clear();

                // Refresh seating arrangement
                generateSeats();

                updateBookingDetails();
            }
        });
    }

    // Generate seats dynamically
    private void generateSeats() {

        seatContainer.removeAllViews();

        for (int i = 0; i < rows; i++) {

            LinearLayout rowLayout = new LinearLayout(this);

            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            char rowLetter = (char) ('A' + i);

            for (int j = 1; j <= seatsPerRow; j++) {

                String seatNumber = rowLetter + String.valueOf(j);

                Button seatButton = new Button(this);

                seatButton.setText(String.valueOf(j));
                seatButton.setTextSize(10);
                seatButton.setPadding(0, 0, 0, 0);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                dpToPx(34),
                                dpToPx(40)
                        );

                params.setMargins(
                        dpToPx(2),
                        dpToPx(4),
                        dpToPx(2),
                        dpToPx(4)
                );

                seatButton.setLayoutParams(params);

                // Check seat availability
                if (bookedSeats.contains(seatNumber)) {

                    seatButton.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    Color.RED
                            )
                    );

                    seatButton.setEnabled(false);

                } else {

                    seatButton.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    Color.GREEN
                            )
                    );

                    seatButton.setOnClickListener(v -> {

                        if (selectedSeats.contains(seatNumber)) {

                            // Deselect seat
                            selectedSeats.remove(seatNumber);

                            seatButton.setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(
                                            Color.GREEN
                                    )
                            );

                        } else {

                            // Select seat
                            selectedSeats.add(seatNumber);

                            seatButton.setBackgroundTintList(
                                    android.content.res.ColorStateList.valueOf(
                                            Color.YELLOW
                                    )
                            );
                        }

                        updateBookingDetails();
                    });
                }

                rowLayout.addView(seatButton);
            }

            seatContainer.addView(rowLayout);
        }
    }

    // Update selected seats and ticket amount
    private void updateBookingDetails() {

        if (selectedSeats.isEmpty()) {

            tvSelectedSeats.setText("Selected Seats: None");

        } else {

            tvSelectedSeats.setText(
                    "Selected Seats: " + selectedSeats
            );
        }

        int totalAmount = selectedSeats.size() * seatPrice;

        tvTotalAmount.setText(
                "Total Amount: ₹" + totalAmount
        );
    }

    // Convert dp into pixels
    private int dpToPx(int dp) {

        return (int) (dp * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}
