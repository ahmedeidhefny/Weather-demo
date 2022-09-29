package com.ahmedeid.weather_demo.model.weather

data class Location(
    val country: String,
    val localtime: String,
    val localtime_epoch: Int,
    val name: String,
    val region: String,
    val tz_id: String
)