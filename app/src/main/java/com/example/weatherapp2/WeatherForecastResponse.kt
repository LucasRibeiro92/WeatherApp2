package com.example.weatherapp2

data class WeatherForecastResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int,
    val data: List<WeatherForecastData>
)

data class WeatherForecastData(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherForecastDetails>
)

data class WeatherForecastDetails(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

