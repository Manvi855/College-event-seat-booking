package com.manvirai.collegeeventseatbooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CampusSeatDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "campus_seat.db";
    private static final int DB_VERSION = 1;

    public CampusSeatDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS halls (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "rows_count INTEGER NOT NULL, " +
                "cols_count INTEGER NOT NULL, " +
                "layout_type TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT NOT NULL, " +
                "event_date TEXT NOT NULL, " +
                "event_time TEXT NOT NULL, " +
                "venue TEXT NOT NULL, " +
                "poster_type TEXT NOT NULL, " +
                "hall_id INTEGER NOT NULL, " +
                "is_paid INTEGER NOT NULL DEFAULT 0, " +
                "fee REAL NOT NULL DEFAULT 0.0, " +
                "FOREIGN KEY(hall_id) REFERENCES halls(id) ON DELETE RESTRICT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "event_id INTEGER NOT NULL, " +
                "hall_id INTEGER NOT NULL, " +
                "seat_label TEXT NOT NULL, " +
                "amount_paid REAL NOT NULL DEFAULT 0.0, " +
                "status TEXT NOT NULL DEFAULT 'CONFIRMED', " +
                "booking_code TEXT NOT NULL, " +
                "booked_at TEXT NOT NULL)");

        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bookings");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS halls");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    private void seedData(SQLiteDatabase db) {
        ContentValues u1 = new ContentValues();
        u1.put("name", "Rohan Verma");
        u1.put("email", "rohan@campus.edu");
        u1.put("password", "1234");
        u1.put("role", "STUDENT");
        long userId = db.insert("users", null, u1);

        long h1 = insertHall(db, "Main Auditorium", 8, 10, "AUDITORIUM");
        long h2 = insertHall(db, "Seminar Hall A", 6, 8, "SEMINAR");
        long h3 = insertHall(db, "Open Amphitheater", 7, 9, "VIP_BALCONY");
        long h4 = insertHall(db, "CS Lecture Hall 1", 5, 6, "STANDARD");

        long e1 = insertEvent(db,
                "AI & Cloud Conclave",
                "Keynote talks on generative AI, cloud infrastructure, and student project demos.",
                "2026-12-04", "10:00 AM", "Academic Block A", "TECH", (int) h1, 0, 0.0);

        insertEvent(db,
                "Inter-College Cultural Gala",
                "Live music, dance performances, drama showcases, and food stalls.",
                "2026-12-08", "05:30 PM", "Central Courtyard", "CULTURAL", (int) h3, 1, 120.0);

        insertEvent(db,
                "Git, Docker & CI/CD Workshop",
                "Hands-on lab session covering version control workflows and container basics.",
                "2026-12-11", "01:30 PM", "Computing Lab 2", "WORKSHOP", (int) h2, 0, 0.0);

        insertEvent(db,
                "Campus Coding & Gaming Cup",
                "Team finals with live stage projection and prizes for top finishers.",
                "2026-12-15", "11:00 AM", "Innovation Hub", "SPORTS", (int) h4, 1, 80.0);

        if (userId != -1 && e1 != -1) {
            ContentValues b = new ContentValues();
            b.put("user_id", userId);
            b.put("event_id", e1);
            b.put("hall_id", h1);
            b.put("seat_label", "A1");
            b.put("amount_paid", 0.0);
            b.put("status", "CONFIRMED");
            b.put("booking_code", "CS-SEED01");
            b.put("booked_at", "2026-11-23 18:00");
            db.insert("bookings", null, b);
        }
    }

    private long insertHall(SQLiteDatabase db, String name, int rows, int cols, String layoutType) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("rows_count", rows);
        cv.put("cols_count", cols);
        cv.put("layout_type", layoutType);
        return db.insert("halls", null, cv);
    }

    private long insertEvent(SQLiteDatabase db, String title, String desc, String date, String time,
                             String venue, String posterType, int hallId, int isPaid, double fee) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", desc);
        cv.put("event_date", date);
        cv.put("event_time", time);
        cv.put("venue", venue);
        cv.put("poster_type", posterType);
        cv.put("hall_id", hallId);
        cv.put("is_paid", isPaid);
        cv.put("fee", isPaid == 1 ? fee : 0.0);
        return db.insert("events", null, cv);
    }

    public List<Models.Hall> getAllHalls() {
        List<Models.Hall> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, name, rows_count, cols_count, layout_type FROM halls ORDER BY id ASC", null);
        while (c.moveToNext()) {
            Models.Hall h = new Models.Hall();
            h.id = c.getInt(0);
            h.name = c.getString(1);
            h.rowsCount = c.getInt(2);
            h.colsCount = c.getInt(3);
            h.layoutType = c.getString(4);
            list.add(h);
        }
        c.close();
        return list;
    }

    public List<Models.EventItem> getAllEvents() {
        List<Models.EventItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT e.id, e.title, e.description, e.event_date, e.event_time, e.venue, " +
                        "e.poster_type, e.hall_id, h.name, e.is_paid, e.fee " +
                        "FROM events e JOIN halls h ON e.hall_id = h.id " +
                        "ORDER BY e.event_date ASC, e.id DESC", null);
        while (c.moveToNext()) {
            Models.EventItem ev = new Models.EventItem();
            ev.id = c.getInt(0);
            ev.title = c.getString(1);
            ev.description = c.getString(2);
            ev.eventDate = c.getString(3);
            ev.eventTime = c.getString(4);
            ev.venue = c.getString(5);
            ev.posterType = c.getString(6);
            ev.hallId = c.getInt(7);
            ev.hallName = c.getString(8);
            ev.isPaid = c.getInt(9) == 1;
            ev.fee = c.getDouble(10);
            list.add(ev);
        }
        c.close();
        return list;
    }

    public long addEvent(String title, String description, String date, String time,
                         String venue, String posterType, int hallId, boolean isPaid, double fee) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title.trim());
        cv.put("description", description.trim());
        cv.put("event_date", date.trim());
        cv.put("event_time", time.trim());
        cv.put("venue", venue.trim());
        cv.put("poster_type", posterType);
        cv.put("hall_id", hallId);
        cv.put("is_paid", isPaid ? 1 : 0);
        cv.put("fee", isPaid ? fee : 0.0);
        return db.insert("events", null, cv);
    }

    public boolean updateEvent(int eventId, String title, String description, String date, String time,
                               String venue, String posterType, int hallId, boolean isPaid, double fee) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title.trim());
        cv.put("description", description.trim());
        cv.put("event_date", date.trim());
        cv.put("event_time", time.trim());
        cv.put("venue", venue.trim());
        cv.put("poster_type", posterType);
        cv.put("hall_id", hallId);
        cv.put("is_paid", isPaid ? 1 : 0);
        cv.put("fee", isPaid ? fee : 0.0);
        return db.update("events", cv, "id=?", new String[]{String.valueOf(eventId)}) > 0;
    }

    public boolean deleteEvent(int eventId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("events", "id=?", new String[]{String.valueOf(eventId)}) > 0;
    }

    public List<Models.BookingSummary> getAllBookingsForAdmin() {
        List<Models.BookingSummary> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT b.id, b.booking_code, COALESCE(u.name, 'Student #' || b.user_id), " +
                        "e.title, h.name, b.seat_label, b.amount_paid, b.status " +
                        "FROM bookings b " +
                        "LEFT JOIN users u ON b.user_id = u.id " +
                        "JOIN events e ON b.event_id = e.id " +
                        "JOIN halls h ON b.hall_id = h.id " +
                        "ORDER BY b.id DESC", null);
        while (c.moveToNext()) {
            Models.BookingSummary item = new Models.BookingSummary();
            item.id = c.getInt(0);
            item.bookingCode = c.getString(1);
            item.studentName = c.getString(2);
            item.eventTitle = c.getString(3);
            item.hallName = c.getString(4);
            item.seatLabel = c.getString(5);
            item.amountPaid = c.getDouble(6);
            item.status = c.getString(7);
            list.add(item);
        }
        c.close();
        return list;
    }

    public String formatFeeBadge(Models.EventItem ev) {
        if (!ev.isPaid || ev.fee <= 0.0) {
            return "FREE";
        }
        return String.format(Locale.getDefault(), "PAID • ₹%.0f", ev.fee);
    }
}