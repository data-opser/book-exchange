package com.vladislav.mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ChangePasswordFragment extends Fragment {
    private DrawerLayoutActivity activity;
    private final Request request = new Request();
    private int userID;
    public ChangePasswordFragment(int userID){
        this.userID = userID;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTextOldPassword = view.findViewById(R.id.editText_OldPassword);
        EditText editTextNewPassword = view.findViewById(R.id.editText_NewPassword);
        EditText editTextConfirmPassword = view.findViewById(R.id.editText_ConfirmPassword);
        Button buttonConfirm = view.findViewById(R.id.button_Confirm);
        Button buttonAlert = view.findViewById(R.id.button_Alert);
        activity = (DrawerLayoutActivity) getActivity();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = editTextOldPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                if (oldPassword.isEmpty() || newPassword.isEmpty()
                        || confirmPassword.isEmpty()) {
                    Toast.makeText(activity, "Field is empty!", Toast.LENGTH_LONG).show();
                } else if(!newPassword.equals(confirmPassword)){
                    Toast.makeText(activity, "The new passwords do not match!", Toast.LENGTH_LONG).show();
                } else if(!checkPassword(newPassword)){
                    Toast.makeText(activity, "The new password does not meet the minimum requirements!", Toast.LENGTH_LONG).show();
                }else {
                    request.changePassword(activity, userID, oldPassword, newPassword);
                    Toast.makeText(activity, "Correct!", Toast.LENGTH_LONG).show();
                    activity.onBackPressed();
                }
            }
        });
        buttonAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Minimum password requirements");
                builder.setMessage("The password must contain at least 8 characters, 2 numbers and 1 sign.");
                builder.setPositiveButton("Understand :Р", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    private boolean checkPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasDigit = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;

        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                hasDigit = true;
            }
            if (Character.isUpperCase(ch)) {
                hasUpperCase = true;
            }
            if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            }

            if (hasDigit && hasUpperCase && hasLowerCase) {
                return true;
            }
        }

        return hasDigit && hasUpperCase && hasLowerCase;
    }

}