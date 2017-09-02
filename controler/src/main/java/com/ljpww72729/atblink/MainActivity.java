package com.ljpww72729.atblink;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ljpww72729.atblink.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceScanActivity.start(MainActivity.this);
            }
        });
        binding.realtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceScanActivity.start(MainActivity.this);
            }
        });
    }

}
