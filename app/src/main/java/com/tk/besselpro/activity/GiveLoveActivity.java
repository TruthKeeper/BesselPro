package com.tk.besselpro.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tk.besselpro.R;
import com.tk.besselpro.view.GiveLoveView;

public class GiveLoveActivity extends AppCompatActivity {
    GiveLoveView loveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_love);
        loveView = (GiveLoveView) findViewById(R.id.loveView);
    }

    public void give_love(View v) {
        loveView.showLove();
    }
}
