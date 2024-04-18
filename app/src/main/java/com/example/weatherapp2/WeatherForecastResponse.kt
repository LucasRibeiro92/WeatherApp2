package com.example.weatherapp2

data class WeatherForecastResponse(
    val lat: Double,
    val lon: Double,
    val tz: String,
    val date: String,
    val units: String,
    val cloudCover: CloudCover,
    val humidity: Humidity,
    val precipitation: Precipitation,
    val temperature: Temperature,
    val pressure: Pressure,
    val wind: Wind
)

data class CloudCover(
    val afternoon: Int
)

data class Humidity(
    val afternoon: Double
)

data class Precipitation(
    val total: Int
)

data class Temperature(
    val min: Double,
    val max: Double,
    val afternoon: Double,
    val night: Double,
    val evening: Double,
    val morning: Double
)

data class Pressure(
    val afternoon: Double
)

data class Wind(
    val max: WindMax
)

data class WindMax(
    val speed: Double,
    val direction: Double
)

