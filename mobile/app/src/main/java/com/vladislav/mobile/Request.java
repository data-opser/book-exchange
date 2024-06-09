package com.vladislav.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Request {
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build();
    ApiService request = retrofit.create(ApiService.class);

    public ApiService getRequest() {
        return request;
    }



    public void loginRequest(LaunchActivity activity, String email, String password){
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        request.login(json).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    JSONObject responseBody = response.body();
                    double userID = (double) responseBody.get("user_id");
                    Intent intent = new Intent(activity, DrawerLayoutActivity.class);
                    intent.putExtra("user_id", userID);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "Wrong email or password!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                showDialog(activity, "Error", t.getMessage());
            }
        });
    }
    public void getImage(Activity activity, String fileName, ImageView imageView){
        Call<ResponseBody> call = request.downloadImage("Frontend/resources/" + fileName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Create a file to save the image
                        File file = File.createTempFile("image", null, activity.getCacheDir());
                        FileOutputStream fos = new FileOutputStream(file);
                        InputStream is = response.body().byteStream();
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                        fos.flush();
                        fos.close();
                        is.close();

                        // Load the image from the file with Glide
                        Glide.with(activity).load(file)
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                        .into(imageView);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    public void getUserData(Activity activity, double userID, DrawerLayoutActivity.UserDataCallback callbackJson){
        request.getUserInfo((int)userID).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    callbackJson.onUserDataReceived(response.body());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                showDialog(activity, "Error", t.getMessage());
            }
        });
    }
    public void deleteTrade(Activity activity, int tradeID){
        request.deleteTrade(tradeID).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                showDialog(activity, "Error", t.getMessage());
            }
        });
    }

    public void acceptTrade(Activity activity, int tradeID){
        request.acceptTrade(tradeID).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                showDialog(activity, "Error", t.getMessage());
            }
        });
    }

    public void bookRequestData(Activity activity, double userID, HomeFragment.BookRequestCallback callbackJson){
        request.requestBook((int)userID).enqueue(new Callback<List<JSONObject>>() {
            @Override
            public void onResponse(Call<List<JSONObject>> call, Response<List<JSONObject>> response) {
                if (response.isSuccessful()) {
                    callbackJson.onBookRequestReceived(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<JSONObject>> call, Throwable t) {
                showDialog(activity, "Error", t.getMessage());
            }
        });
    }







    public void showDialog(Activity activity, String textHead, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(textHead)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
