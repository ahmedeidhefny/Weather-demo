package com.ahmedeid.weather_demo.model.weather

data class WeatherResponse(
    val current: Current,
    val forecast: ForecastX,
    val location: Location
)