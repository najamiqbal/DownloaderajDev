package com.ajdeveloper.instadownloader.Network;

import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Builder().baseUrl("https://youtube-dll.herokuapp.com").addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
