
package com.manvirai.collegeeventseatbooking;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    EditText etEmail, etPassword;
    Button btnStudentLogin, btnAdminLogin;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnStudentLogin = findViewById(R.id.btnStudentLogin);
        btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnStudentLogin.setOnClickListener(v ->
                checkLogin("STUDENT"));

        btnAdminLogin.setOnClickListener(v ->
                checkLogin("ADMIN"));
    }

    private void checkLogin(String expectedRole) {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String actualRole =
                databaseHelper.loginUser(email, password);

        if (actualRole == null) {

            Toast.makeText(this, "Invalid email or password",
                    Toast.LENGTH_SHORT).show();

        } else if (!actualRole.equals(expectedRole)) {

            Toast.makeText(this, "Wrong login type",
                    Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this,
                    actualRole + " Login Successful",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
