package com.example.carpoolbuddy.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;

public class AppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
    }

    public void MainActivity(View w){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}