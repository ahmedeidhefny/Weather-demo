package com.ahmedeid.weather_demo.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.ahmed.retrofitmvvm.util.Resource
import com.ahmedeid.weather_demo.R
import com.ahmedeid.weather_demo.adapter.SearchAdapter
import com.ahmedeid.weather_demo.databinding.ActivityMainBinding
import com.ahmedeid.weather_demo.model.search.SearchItem
import com.ahmedeid.weather_demo.utils.Constants
import com.ahmedeid.weather_demo.utils.Utils
import com.ahmedeid.weather_demo.utils.Utils.hideKeyboard
import com.ahmedeid.weather_demo.utils.errorSnack
import com.ahmedeid.weather_demo.viewmodel.SearchViewModel
import com.ahmedeid.weather_demo.viewmodel.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SearchAdapter.SearchAdapterListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private val viewModelSearch: SearchViewModel by viewModels()

    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent != null) {
            if (intent.hasExtra(Constants.LAT_KEY) && intent.hasExtra(Constants.LNG_KEY)) {
                val lat = intent.getDoubleExtra(Constants.LAT_KEY, 0.0)
                val lng = intent.getDoubleExtra(Constants.LNG_KEY, 0.0)
                getWeatherData("$lat,$lng")
            }
        }

        initActions()

    }


    //region default screen
    private fun initActions() {
        binding.btnSearch.setOnClickListener {
            binding.searchContainer.lytVisibleSearch.visibility = View.VISIBLE
        }

        binding.searchContainer.btnBack.setOnClickListener {
           hideSearchLyt()
        }

        binding.searchContainer.searchBoxContainer.clearSearchQuery.setOnClickListener {
            binding.searchContainer.searchBoxContainer.searchEditText.setText("")
        }

        binding.searchContainer.lytBtm.setOnClickListener {
            hideSearchLyt()
        }

        binding.searchContainer.searchBoxContainer.searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().toLowerCase(Locale.getDefault())
            filterWithQuery(query)
            toggleImageView(query)
        }


        binding.searchContainer.searchBoxContainer.voiceSearchQuery.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
            }
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0)
                }
            // Do something with spokenText
            binding.searchContainer.searchBoxContainer.searchEditText.setText(spokenText)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getWeatherData(query: String) {
        viewModel.getWeather(query, 3);
        viewModel.weatherResponse.observe(this, { weatherResponse ->
            when (weatherResponse) {
                is Resource.Success -> {
                    weatherResponse.data?.let { weather ->
                        binding.apply {
                            tvCityName.text = weather.location.name
                            tvDate.text = Utils.getDateTime(
                                weather.location.localtime_epoch.toString(),
                                weather.location.tz_id
                            )
                            tvTime.text = Utils.getTime(
                                weather.location.localtime_epoch.toString(),
                                weather.location.tz_id
                            )

                            val per = resources.getString(R.string.label_per)
                            val mph = resources.getString(R.string.label_mph);

                            tvHumidity.text = "${weather.current.humidity} $per"
                            tvWind.text = "${weather.current.wind_mph} $mph"
                            tvCondition.text = weather.current.condition.text
                            tvTemperature.text = weather.current.temp_f.toString()

                            Utils.setImage(weather.current.condition.icon, ivCurrentDayIcon)

                            val forecast = weather.forecast.forecastday
                            val temp = resources.getString(R.string.label_temp);

                            tvTemperatureMinMax.text =
                                "${forecast[0].day.mintemp_f}/ ${forecast[0].day.maxtemp_f} $temp"
                            tvTemperatureMinMax2.text =
                                "${forecast[1].day.mintemp_f}/ ${forecast[1].day.maxtemp_f} $temp"
                            tvTemperatureMinMax3.text =
                                "${forecast[2].day.mintemp_f}/ ${forecast[2].day.maxtemp_f} $temp"

                            tvDay.text = resources.getString(R.string.label_today)
                            tvDay2.text = resources.getString(R.string.label_tomorrow)
                            tvDay3.text =
                                Utils.getDay(
                                    forecast[2].date_epoch.toString(),
                                    weather.location.tz_id
                                )

                            Utils.setImage(forecast[0].day.condition.icon, ivDayIcon)
                            Utils.setImage(forecast[1].day.condition.icon, ivDayIcon2)
                            Utils.setImage(forecast[2].day.condition.icon, ivDayIcon3)


                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    weatherResponse.message?.let { message ->
                        binding.rootLayout.errorSnack(message, Snackbar.LENGTH_LONG)
                    }

                }

                is Resource.Loading -> {
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        binding.progress.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progress.visibility = View.VISIBLE
    }


    //endregion

    //region Search

    private fun attachAdapter(list: List<SearchItem>) {
        searchAdapter = SearchAdapter(list, this)
        binding.searchContainer.searchList.adapter = searchAdapter
    }

    private fun filterWithQuery(query: String) {
        if (query.isNotEmpty()) {
            getWeatherDataByCity(query)
        } else if (query.isEmpty()) {
            attachAdapter(arrayListOf())
        }
    }

    private fun getWeatherDataByCity(query: String) {

        viewModelSearch.search(query)
        viewModelSearch.weatherResponse.observe(this, { cityResponse ->
            when (cityResponse) {
                is Resource.Success -> {
                    cityResponse.data?.let { cities ->
                        toggleRecyclerView(cities)
                        attachAdapter(cities)
                    }
                }
                is Resource.Error -> {
                    cityResponse.message?.let { message ->
                        binding.rootLayout.errorSnack(message, Snackbar.LENGTH_LONG)
                    }

                }

                is Resource.Loading -> {
                }
            }
        })
    }

    private fun toggleRecyclerView(list: List<SearchItem>) {
        if (list.isEmpty()) {
            binding.searchContainer.searchList.visibility = View.INVISIBLE
            binding.searchContainer.noSearchResultsFoundText.visibility = View.VISIBLE
        } else {
            binding.searchContainer.searchList.visibility = View.VISIBLE
            binding.searchContainer.noSearchResultsFoundText.visibility = View.INVISIBLE
        }
    }

    private fun toggleImageView(query: String) {
        if (query.isNotEmpty()) {
            binding.searchContainer.searchBoxContainer.clearSearchQuery.visibility = View.VISIBLE
            binding.searchContainer.searchBoxContainer.voiceSearchQuery.visibility = View.INVISIBLE
        } else if (query.isEmpty()) {
            binding.searchContainer.searchBoxContainer.clearSearchQuery.visibility = View.INVISIBLE
            binding.searchContainer.searchBoxContainer.voiceSearchQuery.visibility = View.VISIBLE
        }
    }

    override fun onSelected(item: SearchItem?) {

        item?.name.let {
            if (it != null) {
                getWeatherData(it)
            }
        }

        Toast.makeText(this, "${item?.country}", Toast.LENGTH_SHORT).show()

        hideSearchLyt()
    }

    fun hideSearchLyt() {
        binding.searchContainer.lytVisibleSearch.visibility = View.GONE
        attachAdapter(arrayListOf())
        binding.searchContainer.searchBoxContainer.searchEditText.setText("")

        hideKeyboard(binding.root)
    }



    companion object {
        const val SPEECH_REQUEST_CODE = 0
    }

    //endregion
}