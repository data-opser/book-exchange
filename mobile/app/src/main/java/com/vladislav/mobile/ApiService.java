package com.vladislav.mobile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import org.json.simple.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.http.Url;

import java.util.List;

public interface ApiService {

    @GET("api/users/{id}")
    Call<JSONObject> getUserInfo(@Path("id") int userID);

    @POST("api/trades/{trade_id}/accept")
    Call<JSONObject> acceptTrade(@Path("trade_id") int tradeID);

    @DELETE("api/trades/{trade_id}")
    Call<JSONObject> deleteTrade(@Path("trade_id") int tradeID);

    @POST("api/users/{user_id}/change-password")
    Call<JSONObject> changePassword(@Path("user_id") int userID, @Body JSONObject json);

    @GET
    Call<ResponseBody> downloadImage(@Url String fileUrl);

    @GET("api/users/{user_id}/active_offers")
    Call<List<JSONObject>> requestBook(@Path("user_id") int userID);

    @POST("api/login")
    @Headers("Content-Type: application/json;")
    Call<JSONObject> login(@Body JSONObject loginData);
}

