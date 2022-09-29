package com.ahmedeid.weather_demo.api

import com.ahmedeid.weather_demo.model.search.SearchResponse
import com.ahmedeid.weather_demo.model.weather.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("forecast.json")
    suspend fun getWeather(
        @Query("key") apiKey:String,
        @Query("q") query:String,
        @Query("days") numberOfDays:Int,
    ): Response<WeatherResponse>


    @GET("search.json")
    suspend fun getWeatherByCountry(
        @Query("key") apiKey:String,
        @Query("q") query:String,
    ): Response<SearchResponse>


}