
package com.manvirai.collegeeventseatbooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CampusSeat.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL)";

        db.execSQL(createTable);

        // Create default admin account
        ContentValues admin = new ContentValues();

        admin.put("name", "Admin");
        admin.put("email", "admin@campusseat.com");
        admin.put("password", "admin123");
        admin.put("role", "ADMIN");

        db.insert("users", null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Database upgrade logic can be added later.
    }

    // Student Registration
    public boolean registerStudent(String name,
                                   String email,
                                   String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("role", "STUDENT");

        long result = db.insert("users", null, values);

        return result != -1;
    }

    // Student and Admin Login
    public String loginUser(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE email=? AND password=?",
                new String[]{email, password}
        );

        String role = null;

        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }

        cursor.close();

        return role;
    }
}

