package com.ahmedeid.weather_demo.model.weather

data class Forecastday(
    val date: String,
    val date_epoch: Int,
    val day: Day,
)