package com.example.weatherapp2

import android.os.Build
import androidx.annotation.RequiresApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.Instant
import java.time.temporal.ChronoUnit

interface WeatherApi {
    @GET("onecall")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "hourly,daily,minutely",
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>

    @RequiresApi(Build.VERSION_CODES.O)
    @GET("onecall/timemachine")
    fun getTomorrowWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("dt") dt: Long = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond,
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): Call<WeatherForecastResponse>

    @RequiresApi(Build.VERSION_CODES.O)
    @GET("onecall/timemachine")
    fun getDayAfterTomorrowWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("dt") dt: Long = Instant.now().plus(2, ChronoUnit.DAYS).epochSecond,
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): Call<WeatherForecastResponse>
}