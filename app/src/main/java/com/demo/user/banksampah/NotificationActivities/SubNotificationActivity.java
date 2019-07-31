package com.demo.user.banksampah.NotificationActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.demo.user.banksampah.R;

public class SubNotificationActivity extends AppCompatActivity {

    protected TextView tvKonfirmasiPenjemputan, tvStatusPenjemputan, tvStatusOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_notification);

        tvKonfirmasiPenjemputan = findViewById(R.id.tvKonfirmasiPenjemputan);
        tvStatusPenjemputan = findViewById(R.id.tvStatusPenjemputan);
        tvStatusOrder = findViewById(R.id.tvStatusOrderan);

        tvKonfirmasiPenjemputan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(SubNotificationActivity.this, ConfirmPickOrder.class);
                startActivity(a);
            }
        });

        tvStatusPenjemputan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(SubNotificationActivity.this, PickStatus.class);
                startActivity(b);
            }
        });

        tvStatusOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(SubNotificationActivity.this, PointGetStatus.class);
                startActivity(c);
            }
        });
    }
}
