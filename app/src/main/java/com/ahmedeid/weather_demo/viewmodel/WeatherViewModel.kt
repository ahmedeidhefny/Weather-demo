package com.ahmedeid.weather_demo.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.ahmed.retrofitmvvm.util.Resource
import com.ahmedeid.weather_demo.R
import com.ahmedeid.weather_demo.app.MyApplication
import com.ahmedeid.weather_demo.model.weather.WeatherResponse
import com.ahmedeid.weather_demo.repository.WeatherRepository
import com.ahmedeid.weather_demo.utils.Utils.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel
@Inject
constructor(private val repository: WeatherRepository, app: Application) : AndroidViewModel(app) {

    private val _response = MutableLiveData<Resource<WeatherResponse>>()
    val weatherResponse: LiveData<Resource<WeatherResponse>>
        get() = _response

    /*init {
        //getWeather("Paris", 3);
    }*/

    fun getWeather(query: String, numberOfDays: Int) = viewModelScope.launch {

        fetchData(query, numberOfDays)
    }


    private suspend fun fetchData(query: String, numberOfDays: Int) {
        _response.postValue(Resource.Loading())

        try {
            if (hasInternetConnection(getApplication<MyApplication>())) {
                val response = repository.getWeather(query, numberOfDays)
                _response.postValue(handlePicsResponse(response))
            } else {
                _response.postValue(Resource.Error(getApplication<MyApplication>().getString(R.string.no_internet_connection)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _response.postValue(
                    Resource.Error(
                        getApplication<MyApplication>().getString(
                            R.string.network_failure
                        )
                    )
                )
                else -> _response.postValue(
                    Resource.Error(
                        getApplication<MyApplication>().getString(
                            R.string.conversion_error
                        )
                    )
                )
            }
        }
    }

    private fun handlePicsResponse(response: Response<WeatherResponse>): Resource<WeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        } else if (response.code() == 401) {
            return Resource.Error(
                getApplication<MyApplication>().getString(
                    R.string.apikey_error
                )
            )
        }

        return Resource.Error(response.message())
    }



}