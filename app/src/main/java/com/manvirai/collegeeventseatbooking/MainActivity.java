
package com.manvirai.collegeeventseatbooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnBookSeats, btnMyBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBookSeats = findViewById(R.id.btnBookSeats);
        btnMyBookings = findViewById(R.id.btnMyBookings);

        // Open Seat Booking screen
        btnBookSeats.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SeatBookingActivity.class
            );
            startActivity(intent);
        });

        // Open My Bookings screen
        btnMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    MyBookingsActivity.class
            );
            startActivity(intent);
        });
    }
}
