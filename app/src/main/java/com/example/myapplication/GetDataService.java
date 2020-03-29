package com.example.myapplication;


import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {
    @GET("/")
    Call<CoronaPojo> getAllPhotos();

    @GET("/")
    Call<Object> getCountry();

}
