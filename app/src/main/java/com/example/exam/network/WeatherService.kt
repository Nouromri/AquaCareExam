package com.example.exam.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val current_weather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val weathercode: Int
)

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true
    ): WeatherResponse
}

object WeatherClient {
    private const val BASE_URL = "https://api.open-meteo.com/"

    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}
