package com.nguyenquocthinh.k234112eapp;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Frame_Layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_layout);

        Button btnBackFrame = findViewById(R.id.btnBackFrame);
        btnBackFrame.setOnClickListener(v -> finish());
    }
}
