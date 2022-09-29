package com.ahmedeid.weather_demo.model.weather

data class Current(
    val temp_f: Double,
    val wind_mph: Double,
    val condition: Condition,
    val humidity: Int,
    val last_updated: String,
    val last_updated_epoch: Int
)