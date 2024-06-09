package com.vladislav.mobile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import org.json.simple.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.http.Url;

import java.util.List;

public interface ApiService {
    @GET("api/last-releases")
    Call<List<JSONObject>> getLastReleases();

    @GET("api/users/{id}")
    Call<JSONObject> getUserInfo(@Path("id") int userID);

    @GET
    Call<ResponseBody> downloadImage(@Url String fileUrl);

    @GET("api/")
    Call<JSONObject> request(@Url String fileUrl);

    @POST("api/login")
    @Headers("Content-Type: application/json;")
    Call<JSONObject> login(@Body JSONObject loginData);
}
