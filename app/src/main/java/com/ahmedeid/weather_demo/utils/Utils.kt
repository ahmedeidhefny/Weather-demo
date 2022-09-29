package com.ahmedeid.weather_demo.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import coil.load
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

object Utils {

    fun getDateTime(date: String, zone: String): String? {
        try {
            val sdf = SimpleDateFormat("EEEE, d MMM y")
            sdf.timeZone = TimeZone.getTimeZone(zone)
            val netDate = Date(date.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }


    fun getTime(date: String, zone: String): String? {
        try {
            val sdf = SimpleDateFormat("hh:mm a")
            sdf.timeZone = TimeZone.getTimeZone(zone)
            val netDate = Date(date.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun getDay(date: String, zone: String): String? {
        try {
            val sdf = SimpleDateFormat("EEEE")
            sdf.timeZone = TimeZone.getTimeZone(zone)
            val netDate = Date(date.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun setImage(imageUrl: String, imageView: ImageView) {
        imageView.load(
            Constants.HTTP + imageUrl
        ) {
            crossfade(true)
            crossfade(1000)
        }
    }

    private fun getDateZone(timestamp: Long, zone: String): String {
        val localTime = timestamp.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Instant.ofEpochSecond(it)
                    .atZone(ZoneId.of(zone))
                    .toLocalTime()
            }
        }
        return localTime.toString()
    }


    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun hasInternetConnection(application: Application): Boolean {
        val connectivityManager = application.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}