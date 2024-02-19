package com.example.carpoolbuddy.controllers.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carpoolbuddy.R;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

public class ThemedAutocompleteSupportFragment extends AutocompleteSupportFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places_autocomplete, container, false);
    }
}