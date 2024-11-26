package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // Thay bằng URL API của bạn
    private static Retrofit retrofit;
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Chuyển đổi JSON tự động
                    .build();
        }
        return retrofit;
    }
}