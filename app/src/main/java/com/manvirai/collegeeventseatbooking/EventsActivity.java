package com.manvirai.collegeeventseatbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView rvEventsList;
    private TextView tvEmptyEvents;
    private Button btnOpenAdmin;

    private CampusSeatDbHelper dbHelper;
    private EventListAdapter adapter;
    private final List<Models.EventItem> eventList = new ArrayList<>();
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events1);

        currentUserId = getIntent().getIntExtra("user_id", 1);

        rvEventsList = findViewById(R.id.rvEventsList);
        tvEmptyEvents = findViewById(R.id.tvEmptyEvents);
        btnOpenAdmin = findViewById(R.id.btnOpenAdmin);

        dbHelper = new CampusSeatDbHelper(this);

        rvEventsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventListAdapter(eventList, event -> {
            Intent intent = new Intent(EventsActivity.this, EventDetailsActivity.class);
            intent.putExtra("user_id", currentUserId);
            intent.putExtra("event_data", event);
            startActivity(intent);
        });
        rvEventsList.setAdapter(adapter);

        btnOpenAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(EventsActivity.this, AdminActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        });

        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        eventList.clear();
        List<Models.EventItem> fetched = dbHelper.getAllEvents();
        if (fetched != null) {
            eventList.addAll(fetched);
        }
        adapter.notifyDataSetChanged();

        if (eventList.isEmpty()) {
            tvEmptyEvents.setVisibility(View.VISIBLE);
            rvEventsList.setVisibility(View.GONE);
        } else {
            tvEmptyEvents.setVisibility(View.GONE);
            rvEventsList.setVisibility(View.VISIBLE);
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