package com.example.weatherapp2

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.weatherapp2.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import android.Manifest
import android.annotation.SuppressLint
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    /*-23.2927, Longitude: -51.1732*/
    private lateinit var binding: ActivityMainBinding
    private lateinit var timeZoneTextView: TextView
    private val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    private val API_KEY = "6aecdceab5208708eb7fdf190e00e5d1"
    private val TAG = "CHECK_RESPONSE"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timeZoneTextView = binding.timeZoneTextView

        // Inicialize o FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Verifique se a permissão de localização foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissão ainda não concedida, solicitar permissão em tempo de execução
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permissão já concedida, iniciar a solicitação de localização
            getLastLocation()
        }

    }

    private fun getWeather(lat: Double, lon: Double) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)

        api.getWeather(lat, lon, apiKey = API_KEY).enqueue(object : Callback<WeatherResponse>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                p0: Call<WeatherResponse>,
                p1: Response<WeatherResponse>)
            {
                if (p1.isSuccessful)
                {
                    val weatherResponse = p1.body()
                    weatherResponse?.let {
                        timeZoneTextView.text = "Time Zone: ${weatherResponse.timezone}"
                    }
                }
            }
            override fun onFailure(p0: Call<WeatherResponse>, p1: Throwable) {
                Log.d(TAG, "${p1.message}")
            }

        })

    }

    // Método para obter a última localização conhecida
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    getWeather(latitude, longitude)
                } else {
                    Toast.makeText(this, "Falha ao obter a localização.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Lidar com falhas na obtenção da localização
                Toast.makeText(this, "Erro ao obter a localização: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Método para lidar com o resultado da solicitação de permissão
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão de localização concedida, iniciar a solicitação de localização
                getLastLocation()
            } else {
                // Permissão de localização negada pelo usuário
                // Você pode lidar com isso exibindo uma mensagem de erro ou tomando outra ação apropriada
            }
        }
    }

}