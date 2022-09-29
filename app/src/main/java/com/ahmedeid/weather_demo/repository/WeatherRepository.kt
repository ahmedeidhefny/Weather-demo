package com.ahmedeid.weather_demo.repository

import com.ahmedeid.weather_demo.api.ApiService
import com.ahmedeid.weather_demo.utils.Constants
import javax.inject.Inject

class WeatherRepository
@Inject
constructor(private val apiService: ApiService) {
    suspend fun getWeather(query: String, numberOfDays: Int) = apiService.getWeather(
        Constants.API_KEY,
        query,
        numberOfDays
    )

    suspend fun getWeatherByCountry(query: String) = apiService.getWeatherByCountry(
        Constants.API_KEY,
        query
    )
}