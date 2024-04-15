package com.example.weatherapp2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("onecall")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "hourly,daily,minutely",
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>
}