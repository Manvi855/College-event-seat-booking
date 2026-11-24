package com.manvirai.collegeeventseatbooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView ivDetailsPoster;
    private TextView tvDetailsTitle;
    private TextView tvDetailsFeeBadge;
    private TextView tvDetailsMeta;
    private TextView tvDetailsDescription;
    private Button btnProceedToSeats;

    private CampusSeatDbHelper dbHelper;
    private Models.EventItem event;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        dbHelper = new CampusSeatDbHelper(this);

        ivDetailsPoster = findViewById(R.id.ivDetailsPoster);
        tvDetailsTitle = findViewById(R.id.tvDetailsTitle);
        tvDetailsFeeBadge = findViewById(R.id.tvDetailsFeeBadge);
        tvDetailsMeta = findViewById(R.id.tvDetailsMeta);
        tvDetailsDescription = findViewById(R.id.tvDetailsDescription);
        btnProceedToSeats = findViewById(R.id.btnProceedToSeats);

        currentUserId = getIntent().getIntExtra("user_id", 1);
        Object rawEvent = getIntent().getSerializableExtra("event_data");
        if (rawEvent instanceof Models.EventItem) {
            event = (Models.EventItem) rawEvent;
        }

        if (event == null) {
            Toast.makeText(this, "Event details not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindEventDetails();

        btnProceedToSeats.setOnClickListener(v -> {
            try {
                Class<?> seatActivityClass = Class.forName(
                        "com.manvirai.collegeeventseatbooking.SeatSelectionActivity");
                Intent intent = new Intent(EventDetailsActivity.this, seatActivityClass);
                intent.putExtra("user_id", currentUserId);
                intent.putExtra("event_id", event.id);
                intent.putExtra("hall_id", event.hallId);
                intent.putExtra("event_title", event.title);
                intent.putExtra("hall_name", event.hallName);
                intent.putExtra("is_paid", event.isPaid);
                intent.putExtra("fee", event.fee);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                Toast.makeText(
                        this,
                        "SeatSelectionActivity is not added yet (Hall: " + event.hallName + ")",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void bindEventDetails() {
        tvDetailsTitle.setText(event.title);
        tvDetailsFeeBadge.setText(dbHelper.formatFeeBadge(event));

        String meta = "Date: " + event.eventDate +
                "\nTime: " + event.eventTime +
                "\nVenue: " + event.venue +
                "\nAssigned Hall: " + event.hallName;
        tvDetailsMeta.setText(meta);

        tvDetailsDescription.setText(event.description);
        ivDetailsPoster.setImageResource(getPosterDrawable(event.posterType));
    }

    private int getPosterDrawable(String posterType) {
        if (posterType == null) {
            return android.R.drawable.ic_menu_today;
        }
        switch (posterType.toUpperCase(Locale.ROOT)) {
            case "TECH":
                return android.R.drawable.ic_menu_manage;
            case "CULTURAL":
                return android.R.drawable.ic_menu_gallery;
            case "WORKSHOP":
                return android.R.drawable.ic_menu_edit;
            case "SPORTS":
                return android.R.drawable.ic_menu_compass;
            default:
                return android.R.drawable.ic_menu_today;
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}