package com.manvirai.collegeeventseatbooking;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    private static final String[] CATEGORIES = {"TECH", "CULTURAL", "WORKSHOP", "SPORTS"};

    private TextView tvFormMode;
    private EditText etAdminTitle;
    private EditText etAdminDescription;
    private EditText etAdminDate;
    private EditText etAdminTime;
    private EditText etAdminVenue;
    private Spinner spAdminCategory;
    private Spinner spAdminHall;
    private CheckBox cbAdminIsPaid;
    private EditText etAdminFee;
    private Button btnAdminSaveEvent;
    private Button btnAdminResetForm;
    private Button btnAdminSelectEventToEditDelete;
    private TextView tvAdminEventsOverview;
    private TextView tvAdminBookingsMonitor;

    private CampusSeatDbHelper dbHelper;
    private final List<Models.Hall> halls = new ArrayList<>();
    private final List<Models.EventItem> events = new ArrayList<>();

    private Integer editingEventId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new CampusSeatDbHelper(this);

        tvFormMode = findViewById(R.id.tvFormMode);
        etAdminTitle = findViewById(R.id.etAdminTitle);
        etAdminDescription = findViewById(R.id.etAdminDescription);
        etAdminDate = findViewById(R.id.etAdminDate);
        etAdminTime = findViewById(R.id.etAdminTime);
        etAdminVenue = findViewById(R.id.etAdminVenue);
        spAdminCategory = findViewById(R.id.spAdminCategory);
        spAdminHall = findViewById(R.id.spAdminHall);
        cbAdminIsPaid = findViewById(R.id.cbAdminIsPaid);
        etAdminFee = findViewById(R.id.etAdminFee);
        btnAdminSaveEvent = findViewById(R.id.btnAdminSaveEvent);
        btnAdminResetForm = findViewById(R.id.btnAdminResetForm);
        btnAdminSelectEventToEditDelete = findViewById(R.id.btnAdminSelectEventToEditDelete);
        tvAdminEventsOverview = findViewById(R.id.tvAdminEventsOverview);
        tvAdminBookingsMonitor = findViewById(R.id.tvAdminBookingsMonitor);

        setupSpinners();
        setupListeners();
        refreshAdminPanels();
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAdminCategory.setAdapter(categoryAdapter);

        halls.clear();
        halls.addAll(dbHelper.getAllHalls());
        ArrayAdapter<Models.Hall> hallAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                halls
        );
        hallAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAdminHall.setAdapter(hallAdapter);
    }

    private void setupListeners() {
        cbAdminIsPaid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etAdminFee.setEnabled(isChecked);
            if (!isChecked) {
                etAdminFee.setText("0");
            }
        });
        etAdminFee.setEnabled(cbAdminIsPaid.isChecked());

        btnAdminSaveEvent.setOnClickListener(v -> saveEvent());
        btnAdminResetForm.setOnClickListener(v -> resetForm());
        btnAdminSelectEventToEditDelete.setOnClickListener(v -> showEventPickerDialog());
    }

    private void saveEvent() {
        String title = etAdminTitle.getText().toString().trim();
        String description = etAdminDescription.getText().toString().trim();
        String date = etAdminDate.getText().toString().trim();
        String time = etAdminTime.getText().toString().trim();
        String venue = etAdminVenue.getText().toString().trim();
        String posterType = (String) spAdminCategory.getSelectedItem();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(venue)) {
            Toast.makeText(this, "Please fill in all event fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (halls.isEmpty() || spAdminHall.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "No hall available to assign", Toast.LENGTH_SHORT).show();
            return;
        }

        Models.Hall selectedHall = halls.get(spAdminHall.getSelectedItemPosition());
        boolean isPaid = cbAdminIsPaid.isChecked();
        double fee = 0.0;

        if (isPaid) {
            String feeStr = etAdminFee.getText().toString().trim();
            if (TextUtils.isEmpty(feeStr)) {
                Toast.makeText(this, "Enter a valid fee for a paid event", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                fee = Double.parseDouble(feeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid fee amount", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fee <= 0.0) {
                Toast.makeText(this, "Paid event fee must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (editingEventId == null) {
            long insertedId = dbHelper.addEvent(
                    title, description, date, time, venue, posterType,
                    selectedHall.id, isPaid, fee
            );
            if (insertedId != -1) {
                Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                resetForm();
                refreshAdminPanels();
            } else {
                Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean updated = dbHelper.updateEvent(
                    editingEventId, title, description, date, time, venue, posterType,
                    selectedHall.id, isPaid, fee
            );
            if (updated) {
                Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
                resetForm();
                refreshAdminPanels();
            } else {
                Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetForm() {
        editingEventId = null;
        tvFormMode.setText("Mode: Add New Event");
        btnAdminSaveEvent.setText("Add Event");

        etAdminTitle.setText("");
        etAdminDescription.setText("");
        etAdminDate.setText("");
        etAdminTime.setText("");
        etAdminVenue.setText("");

        if (spAdminCategory.getAdapter() != null && spAdminCategory.getAdapter().getCount() > 0) {
            spAdminCategory.setSelection(0);
        }
        if (spAdminHall.getAdapter() != null && spAdminHall.getAdapter().getCount() > 0) {
            spAdminHall.setSelection(0);
        }

        cbAdminIsPaid.setChecked(false);
        etAdminFee.setText("0");
        etAdminFee.setEnabled(false);
    }

    private void showEventPickerDialog() {
        events.clear();
        events.addAll(dbHelper.getAllEvents());

        if (events.isEmpty()) {
            Toast.makeText(this, "No events available to edit or delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] labels = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            Models.EventItem ev = events.get(i);
            labels[i] = ev.title + " (" + ev.eventDate + " • " + ev.hallName + ")";
        }

        new AlertDialog.Builder(this)
                .setTitle("Select an Event")
                .setItems(labels, (dialog, which) -> showEditOrDeleteActionDialog(events.get(which)))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditOrDeleteActionDialog(Models.EventItem selectedEvent) {
        String[] actions = {"Edit Event", "Delete Event"};
        new AlertDialog.Builder(this)
                .setTitle(selectedEvent.title)
                .setItems(actions, (dialog, which) -> {
                    if (which == 0) {
                        loadEventIntoForm(selectedEvent);
                    } else if (which == 1) {
                        confirmDeleteEvent(selectedEvent);
                    }
                })
                .show();
    }

    private void loadEventIntoForm(Models.EventItem ev) {
        editingEventId = ev.id;
        tvFormMode.setText("Mode: Editing Event #" + ev.id);
        btnAdminSaveEvent.setText("Update Event");

        etAdminTitle.setText(ev.title);
        etAdminDescription.setText(ev.description);
        etAdminDate.setText(ev.eventDate);
        etAdminTime.setText(ev.eventTime);
        etAdminVenue.setText(ev.venue);

        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equalsIgnoreCase(ev.posterType)) {
                spAdminCategory.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < halls.size(); i++) {
            if (halls.get(i).id == ev.hallId) {
                spAdminHall.setSelection(i);
                break;
            }
        }

        cbAdminIsPaid.setChecked(ev.isPaid);
        etAdminFee.setEnabled(ev.isPaid);
        etAdminFee.setText(ev.isPaid
                ? String.format(Locale.getDefault(), "%.0f", ev.fee)
                : "0");
    }

    private void confirmDeleteEvent(Models.EventItem ev) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete \"" + ev.title + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteEvent(ev.id);
                    if (deleted) {
                        if (editingEventId != null && editingEventId == ev.id) {
                            resetForm();
                        }
                        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                        refreshAdminPanels();
                    } else {
                        Toast.makeText(this, "Could not delete event (may have linked records)", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void refreshAdminPanels() {
        events.clear();
        events.addAll(dbHelper.getAllEvents());

        if (events.isEmpty()) {
            tvAdminEventsOverview.setText("No events created yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Models.EventItem ev : events) {
                sb.append("• ").append(ev.title)
                        .append(" — ").append(ev.eventDate).append(" ").append(ev.eventTime)
                        .append("\n  Hall: ").append(ev.hallName)
                        .append(" | Venue: ").append(ev.venue)
                        .append(" | ").append(dbHelper.formatFeeBadge(ev))
                        .append("\n\n");
            }
            tvAdminEventsOverview.setText(sb.toString().trim());
        }

        List<Models.BookingSummary> bookings = dbHelper.getAllBookingsForAdmin();
        if (bookings.isEmpty()) {
            tvAdminBookingsMonitor.setText("No seat bookings recorded yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Models.BookingSummary b : bookings) {
                String amountStr = b.amountPaid <= 0.0
                        ? "FREE"
                        : String.format(Locale.getDefault(), "₹%.0f", b.amountPaid);
                sb.append("• [").append(b.bookingCode).append("] ")
                        .append(b.studentName).append(" -> ").append(b.eventTitle)
                        .append("\n  ").append(b.hallName)
                        .append(" | Seat: ").append(b.seatLabel)
                        .append(" | ").append(amountStr)
                        .append(" | ").append(b.status)
                        .append("\n\n");
            }
            tvAdminBookingsMonitor.setText(sb.toString().trim());
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