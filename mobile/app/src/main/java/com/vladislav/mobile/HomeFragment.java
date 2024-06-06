package com.vladislav.mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    DrawerLayoutActivity activity;
    LinearLayout linearLayoutRequest;
    ImageView imageViewUser;
    ImageView imageViewBook;
    TextView textViewUserName;
    TextView textViewBookInfo;
    TextView textViewBookGenre;
    TextView textViewBookYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (DrawerLayoutActivity) getActivity();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton buttonExchange = view.findViewById(R.id.imageButton_Exchange);
        imageViewUser = view.findViewById(R.id.imageView_UserRequest);
        imageViewBook = view.findViewById(R.id.imageView_Book);
        textViewUserName = view.findViewById(R.id.textView_UserName);
        textViewBookInfo = view.findViewById(R.id.textView_BookInfo);
        textViewBookGenre = view.findViewById(R.id.textView_BookGenre);
        textViewBookYear = view.findViewById(R.id.textView_BookYear);
        linearLayoutRequest = view.findViewById(R.id.linearLayout_Request);

        buttonExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRequest("Confirmation of operation", "You confirm the exchange?");
            }
        });
    }
    public void timchasoviyMetod(Drawable userPhoto, Drawable bookPhoto, String username,
                                 String bookInfo, String bookGenre, String bookYear){
        imageViewUser.setImageDrawable(userPhoto);
        imageViewBook.setImageDrawable(bookPhoto);
        textViewUserName.setText(username);
        textViewBookInfo.setText(bookInfo);
        textViewBookGenre.setText(bookGenre);
        textViewBookYear.setText(bookYear);
    }
    public void showDialogRequest(String textHead, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(textHead)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes, sure!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linearLayoutRequest.setVisibility(View.INVISIBLE);
                    }
                })
                .setNegativeButton("No, please!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Користувач натиснув "Ні"
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}