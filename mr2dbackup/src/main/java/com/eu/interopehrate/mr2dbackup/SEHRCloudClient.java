package com.eu.interopehrate.mr2dbackup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Base64;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SEHRCloudClient {

    private static String BASE_URL = "http://213.249.46.253:5000";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String CLOUD_URL){
        if(retrofit == null){

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(CLOUD_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

}
