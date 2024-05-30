package com.vladislav.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    DrawerLayoutActivity activity;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (DrawerLayoutActivity) getActivity();
        SwitchCompat switchTheme = view.findViewById(R.id.switch_theme);
        sharedPreferences = activity.getSharedPreferences("MODE", Context.MODE_PRIVATE);

        switchTheme.setChecked(activity.isNight);

        switchTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
                if (activity.isNight) {
                    editor.putBoolean("night", false);
                } else {
                    editor.putBoolean("night", true);
                }
                editor.apply();

                activity.setTheme();
            }
        });


        /*switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    activity.isNight = true;
                    editor.putBoolean("night", true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    activity.isNight = false;
                    editor.putBoolean("night", false);
                }
                editor.apply();
            }
        });*/
    }
}