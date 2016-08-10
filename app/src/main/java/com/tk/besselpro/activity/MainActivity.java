package com.tk.besselpro.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tk.besselpro.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void wave(View v) {
        startActivity(new Intent(this, WaveActivity.class));
    }

    public void wave_ball(View v) {
        startActivity(new Intent(this, WaveBallActivity.class));
    }
}
