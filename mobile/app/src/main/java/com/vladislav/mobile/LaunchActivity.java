package com.vladislav.mobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends AppCompatActivity {
    private EditText editTextLogin;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTheme();

        Button buttonLogin = findViewById(R.id.button_login);
        editTextLogin = findViewById(R.id.editText_Email);
        editTextPassword = findViewById(R.id.editText_Password);


        buttonLogin.setOnClickListener(v -> {
            String login = editTextLogin.getText().toString();
            String password = editTextPassword.getText().toString();

            Intent intent2 = new Intent(LaunchActivity.this, DrawerLayoutActivity.class);///////////// ВИДАЛИТИ!!!!
            startActivity(intent2); ///////////// ВИДАЛИТИ!!!!
            if(login.isEmpty() || password.isEmpty()){
                Toast.makeText(LaunchActivity.this, "Field is empty!", Toast.LENGTH_LONG).show();
            }
            else if(false){
                Toast.makeText(LaunchActivity.this, "Wrong email or password!", Toast.LENGTH_LONG).show();
            }
            else{
                //Intent intent = new Intent(LaunchActivity.this, DrawerLayoutActivity.class);
                //startActivity(intent);
            }
        });
    }

    public void showDialog(String textHead, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
        builder.setTitle(textHead)
                .setMessage(message)
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
}