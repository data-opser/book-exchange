package com.vladislav.mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SettingsFragment extends Fragment {

    private DrawerLayoutActivity activity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    SwitchCompat switchNotification;
    private final int REQUEST_CODE_POST_NOTIFICATIONS = 123;
    private int userID;

    public SettingsFragment(int userID){
        this.userID = userID;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (DrawerLayoutActivity) getActivity();
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonChangePassword = view.findViewById(R.id.change_password_button);
        SwitchCompat switchTheme = view.findViewById(R.id.switch_theme);
        switchNotification = view.findViewById(R.id.switch_notification);
        sharedPreferences = activity.getSharedPreferences("MODE", Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
        switchTheme.setChecked(sharedPreferences.getBoolean("night", false));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                    != (sharedPreferences.getBoolean("notification", false) ? 1 : 0)) {
                editor.putBoolean("night", ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == 1);
            }
        }

        switchNotification.setChecked(sharedPreferences.getBoolean("notification", false));

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("night", true);
                } else {
                    editor.putBoolean("night", false);
                }
                editor.apply();
                activity.setTheme();
            }
        });
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
                        } else {
                            editor.putBoolean("notification", true);
                        }
                    }
                } else {
                    editor.putBoolean("notification", false);
                }
                editor.apply();
            }
        });
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ChangePasswordFragment(userID), "CHANGEPASSWORD_FRAGMENT");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    public void showDialog(String textHead, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(textHead)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}