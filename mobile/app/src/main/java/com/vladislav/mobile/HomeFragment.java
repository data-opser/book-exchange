package com.vladislav.mobile;

import android.app.AlertDialog;
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
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.List;

public class HomeFragment extends Fragment {
    private DrawerLayoutActivity activity;
    private List<JSONObject> jsonBookData;
    private  int counter = 0;
    private int userID;
    private final Request request = new Request();
    public HomeFragment(int userID){
        this.userID = userID;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (DrawerLayoutActivity) getActivity();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        runRequest();
    }

    private void runRequest(){
        request.bookRequestData(activity, userID, new HomeFragment.BookRequestCallback() {
            @Override
            public void onBookRequestReceived(List<JSONObject> requestData) {
                jsonBookData = requestData;
                processingRequest();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void processingRequest(){
        TextView bookName = activity.findViewById(R.id.textView_BookName);
        TextView bookAuthor = activity.findViewById(R.id.textView_BookAuthor);
        TextView userOfferedName = activity.findViewById(R.id.textView_NameOffered);
        ImageView bookPhoto = activity.findViewById(R.id.imageView_Book);
        ImageView userOfferPhoto = activity.findViewById(R.id.imageView_UserOffered);
        ImageButton buttonAccept = activity.findViewById(R.id.imageButton_Accept);
        LinearLayout layoutRequest = activity.findViewById(R.id.linearLayout_Request);
        try {
            bookName.setText(jsonBookData.get(counter).get("user_name").toString());
            bookAuthor.setText(jsonBookData.get(counter).get("book_offered_author").toString());
            userOfferedName.setText(jsonBookData.get(counter).get("user_name").toString());
            setImage(bookPhoto, "book_offered_logo");
            setImage(userOfferPhoto, "user_profile_image");
            layoutRequest.setVisibility(View.VISIBLE);

            buttonAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();

                }
            });
        }catch (Exception ex){
            counter = 0;

            layoutRequest.setVisibility(View.INVISIBLE);
        }
    }
    public void setImage(ImageView imageView, String atributePhoto){
        request.getImage(activity, (atributePhoto == "book_offered_logo" ? "books/" : "profiles/") + jsonBookData.get(counter).get(atributePhoto).toString(), imageView);
    }
    public interface BookRequestCallback {
        void onBookRequestReceived(List<JSONObject> bookData);
        void onError(String error);
    }
    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Offer")
                .setMessage("Accept the offer?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    request.acceptTrade(activity, (int)(double)jsonBookData.get(counter).get("exchange_id"));
                    counter++;
                    processingRequest();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    request.deleteTrade(activity, (int)(double)jsonBookData.get(counter).get("exchange_id"));
                    counter++;
                    processingRequest();
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}