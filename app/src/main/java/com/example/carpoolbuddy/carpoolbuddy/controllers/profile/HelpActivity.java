package com.example.carpoolbuddy.carpoolbuddy.controllers.profile;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carpoolbuddy.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void back(View view) {
        FragmentManager fm = getFragmentManager();
            Log.i("MainActivity", "popping backstack");
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }
}