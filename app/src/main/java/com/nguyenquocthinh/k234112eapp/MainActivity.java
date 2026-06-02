package com.nguyenquocthinh.k234112eapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.nguyenquocthinh.models.UserAccount;

public class MainActivity extends AppCompatActivity {

    private TextView txtWelcome;
    private CardView cardCalculator;
    private CardView cardOrderManagement;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtWelcome = findViewById(R.id.txtWelcome);
        cardCalculator = findViewById(R.id.cardCalculator);
        cardOrderManagement = findViewById(R.id.cardOrderManagement);
        btnLogout = findViewById(R.id.btnLogout);

        // Get Login User
        UserAccount loginUser = (UserAccount) getIntent().getSerializableExtra("USER_LOGIN");
        if (loginUser != null) {
            txtWelcome.setText("Welcome back, " + loginUser.getUsername() + "!");
        }

        // Open Calculator Activity
        cardCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(intent);
            }
        });

        // Open Order Management Activity
        cardOrderManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderManagement.class);
                startActivity(intent);
            }
        });

        // Logout Session direct back to Login activity
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
