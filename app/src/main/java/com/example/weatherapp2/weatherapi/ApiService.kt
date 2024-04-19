package com.example.weatherapp2.weatherapi

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp2.WeatherForecastResponse
import com.example.weatherapp2.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface ApiService {
    @GET("onecall")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "hourly,daily,minutely",
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>

    @RequiresApi(Build.VERSION_CODES.O)
    @GET("onecall/day_summary")
    fun getTomorrowWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("tz") tz: String = "-03:00",
        @Query("date") date: String = getTomorrowDate(),
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): Call<WeatherForecastResponse>

    @RequiresApi(Build.VERSION_CODES.O)
    @GET("onecall/day_summary")
    fun getDayAfterTomorrowWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("tz") tz: String = "-03:00",
        @Query("date") date: String = getDayAfterTomorrowDate(),
        @Query("units") units: String = "imperial",
        @Query("appid") apiKey: String
    ): Call<WeatherForecastResponse>
}

fun getTomorrowDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getDayAfterTomorrowDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 2)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}