package com.example.weatherapp2

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherapp2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.weatherapp2.weatherapi.ApiClient
import java.util.Locale

class MainActivity : AppCompatActivity() {

    /*-23.2927, Longitude: -51.1732*/

    //Declaring the bindings
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherIconImageView: ImageView
    private lateinit var refreshDataWeatherImageView: ImageView
    private lateinit var currentTemperatureTextView: TextView
    private lateinit var currentConditionTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var windTextView: TextView
    private lateinit var tomorrowMinTemperatureTextView: TextView
    private lateinit var tomorrowMaxTemperatureTextView: TextView
    private lateinit var dayAfterTomorrowMinTemperatureTextView: TextView
    private lateinit var dayAfterTomorrowMaxTemperatureTextView: TextView
    //Declaring general variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var apiClient: ApiClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    private val API_KEY = "6aecdceab5208708eb7fdf190e00e5d1"
    private val TAG = "CHECK_RESPONSE"
    private val TAG_TRY = "CATCH"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //Instantiating the bindings
        try {
            setupBindings()
        }catch (e: Exception){
            Log.d(TAG_TRY, e.message.toString())
        }

        // Inicialize o FusedLocationProviderClient
        try {
            setupFusedLocation()
        }catch (e: Exception){
            Log.d(TAG_TRY, e.message.toString())
        }

        //Initializing Weather Api Client
        //val apiClient = ApiClient.create()

        // Verifique se a permissão de localização foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissão ainda não concedida, solicitar permissão em tempo de execução
            requestLocationPermission()
        } else {
            // Permissão já concedida, iniciar a solicitação de localização
            getLastLocation()
        }

        refreshDataWeatherImageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permissão ainda não concedida, solicitar permissão em tempo de execução
                requestLocationPermission()
            } else {
                // Permissão já concedida, iniciar a solicitação de localização
                getLastLocation()
            }
        }

    }

    // Method to setup bindings
    private fun setupBindings(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherIconImageView = binding.weatherIconImageView
        refreshDataWeatherImageView = binding.refreshDataWeatherImageView
        currentTemperatureTextView = binding.currentTemperatureTextView
        currentConditionTextView = binding.currentConditionTextView
        humidityTextView = binding.humidityTextView
        windTextView = binding.windTextView
        tomorrowMinTemperatureTextView = binding.tomorrowMinTemperatureTextView
        tomorrowMaxTemperatureTextView = binding.tomorrowMaxTemperatureTextView
        dayAfterTomorrowMinTemperatureTextView = binding.dayAfterTomorrowMinTemperatureTextView
        dayAfterTomorrowMaxTemperatureTextView = binding.dayAfterTomorrowMaxTemperatureTextView
    }

    private fun setupFusedLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    // Method to request location permission
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // Method to deal with the result of the request permission
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão de localização concedida, iniciar a solicitação de localização
                getLastLocation()
            } else {
                // Permissão de localização negada pelo usuário
                showPermissionExplanationDialog()
            }
        }
    }

    private fun getWeather(lat: Double, lon: Double) {

        val apiClient = ApiClient.create()

        apiClient.getWeather(lat, lon, apiKey = API_KEY)
            .enqueue(object : Callback<WeatherResponse>{
            @SuppressLint("SetTextI18n", "DiscouragedApi")
            override fun onResponse(
                p0: Call<WeatherResponse>,
                p1: Response<WeatherResponse>)
            {
                if (p1.isSuccessful)
                {
                    val weatherResponse = p1.body()
                    weatherResponse?.let {
                        val weatherDetails = weatherResponse.current.weather.firstOrNull()
                        val iconName = "i" + weatherDetails?.icon
                        val iconResourceId = resources.getIdentifier(iconName, "drawable", packageName)
                        weatherIconImageView.setImageResource(iconResourceId)
                        currentTemperatureTextView.text = "${weatherResponse.current.temp} ºF"
                        if (weatherDetails != null) {
                            currentConditionTextView.text = weatherDetails.description.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                        }
                        humidityTextView.text = "Humidity: ${weatherResponse.current.humidity}%"
                        windTextView.text = "Wind: ${weatherResponse.current.windSpeed} mph"
                        Log.d(TAG, "${p1.body()}")
                    }
                }
            }
            override fun onFailure(p0: Call<WeatherResponse>, p1: Throwable) {
                Log.d(TAG, "${p1.message}")
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTomorrowWeather(lat: Double, lon: Double) {

        val apiClient = ApiClient.create()

        apiClient.getTomorrowWeather(lat, lon, apiKey = API_KEY).enqueue(object : Callback<WeatherForecastResponse>{
            @SuppressLint("SetTextI18n", "DiscouragedApi")
            override fun onResponse(
                p0: Call<WeatherForecastResponse>,
                p1: Response<WeatherForecastResponse>)
            {
                Log.d(TAG, "${p1.body()}")
                if (p1.isSuccessful)
                {
                    val weatherForecastResponse = p1.body()
                    weatherForecastResponse?.let {
                        tomorrowMinTemperatureTextView.text = "min: ${weatherForecastResponse.temperature.min}º"
                        tomorrowMaxTemperatureTextView.text = "max: ${weatherForecastResponse.temperature.max}º"
                        /*val weatherDetails = weatherResponse.current.weather.firstOrNull()
                        val iconName = "i" + weatherDetails?.icon
                        val iconResourceId = resources.getIdentifier(iconName, "drawable", packageName)
                        weatherIconImageView.setImageResource(iconResourceId)
                        currentTemperatureTextView.text = "${weatherResponse.current.temp} ºF"
                        currentConditionTextView.text = "Condition: ${weatherResponse.current.clouds}"*/
                        Log.d(TAG, "${p1.body()}")
                    }
                }
            }
            override fun onFailure(p0: Call<WeatherForecastResponse>, p1: Throwable) {
                Log.d(TAG, "${p1.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDayAfterTomorrowWeather(lat: Double, lon: Double) {

        val apiClient = ApiClient.create()

        apiClient.getDayAfterTomorrowWeather(lat, lon, apiKey = API_KEY).enqueue(object : Callback<WeatherForecastResponse>{
            @SuppressLint("SetTextI18n", "DiscouragedApi")
            override fun onResponse(
                p0: Call<WeatherForecastResponse>,
                p1: Response<WeatherForecastResponse>)
            {
                if (p1.isSuccessful)
                {
                    val weatherForecastResponse = p1.body()
                    weatherForecastResponse?.let {
                        dayAfterTomorrowMinTemperatureTextView.text = "min: ${weatherForecastResponse.temperature.min}º"
                        dayAfterTomorrowMaxTemperatureTextView.text = "max: ${weatherForecastResponse.temperature.max}º"
                        /*val weatherDetails = weatherResponse.current.weather.firstOrNull()
                        val iconName = "i" + weatherDetails?.icon
                        val iconResourceId = resources.getIdentifier(iconName, "drawable", packageName)
                        weatherIconImageView.setImageResource(iconResourceId)
                        currentTemperatureTextView.text = "${weatherResponse.current.temp} ºF"
                        currentConditionTextView.text = "Condition: ${weatherResponse.current.clouds}"*/
                        Log.d(TAG, "${p1.body()}")
                    }
                }
            }
            override fun onFailure(p0: Call<WeatherForecastResponse>, p1: Throwable) {
                Log.d(TAG, "${p1.message}")
            }
        })
    }

    // Método para obter a última localização conhecida
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    getWeather(latitude, longitude)
                    getTomorrowWeather(latitude, longitude)
                    getDayAfterTomorrowWeather(latitude, longitude)
                } else {
                    Toast.makeText(this, "Falha ao obter a localização.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Lidar com falhas na obtenção da localização
                Toast.makeText(this, "Erro ao obter a localização: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Método para mostrar um diálogo explicando a necessidade da permissão de localização
    private fun showPermissionExplanationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permissão de Localização Necessária")
            .setMessage("Este aplicativo requer permissão de localização para funcionar corretamente.")
            .setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                // Solicitar permissão de localização
                requestLocationPermission()
            }
            .setNegativeButton("Cancelar") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                // Aqui você pode lidar com o caso em que o usuário cancela a solicitação de permissão
                requestLocationPermission()
            }
            .setCancelable(false)
            .show()
    }
}