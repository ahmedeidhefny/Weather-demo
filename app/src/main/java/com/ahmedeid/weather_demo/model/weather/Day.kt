package com.ahmedeid.weather_demo.model.weather

data class Day(
    val condition: Condition,
    val maxtemp_f: Double,
    val mintemp_f: Double,
)