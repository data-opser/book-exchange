package com.vladislav.mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class DrawerLayoutActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private SettingsFragment settingsFragment;
    private final int REQUEST_CODE_POST_NOTIFICATIONS = 123;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme();
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_drawerlayout);

        settingsFragment = new SettingsFragment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editor = sharedPreferences.edit();

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_home) {
            setNewFragment(new HomeFragment(), "HOME_FRAGMENT");
        } else if (menuItem.getItemId() == R.id.nav_settings) {
            settingsFragment = new SettingsFragment();
            setNewFragment(settingsFragment, "SETTINGS_FRAGMENT");
        } else if (menuItem.getItemId() == R.id.nav_support) {
            setNewFragment(new SupportFragment(), "SUPPORT_FRAGMENT");
        } else if (menuItem.getItemId() == R.id.nav_exit) {
            Exit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if(!(currentFragment instanceof HomeFragment)){
            setNewFragment(new HomeFragment(), "HOME_FRAGMENT");
        } else{
            super.onBackPressed();
        }
    }

    public void setNewFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, tag);
        ft.commit();
    }

    public void Exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerLayoutActivity.this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void setTheme(){
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("night", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getWindow().setStatusBarColor(getResources().getColor(R.color.panel_night));
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getWindow().setStatusBarColor(getResources().getColor(R.color.panel_light));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            SettingsFragment settingsFragment = (SettingsFragment) fragmentManager.findFragmentByTag("SETTINGS_FRAGMENT");
            if (settingsFragment != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                settingsFragment.switchNotification.setChecked(true);
                editor.putBoolean("notification", true);
            } else if (settingsFragment != null) {
                settingsFragment.switchNotification.setChecked(false);
                editor.putBoolean("notification", false);
                showDialog("Notification", "Give access to notifications in settings. This is the android requirement if you declined :(");
            }
            editor.apply();
        }
    }
    public void showDialog(String textHead, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(textHead)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Okay", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
