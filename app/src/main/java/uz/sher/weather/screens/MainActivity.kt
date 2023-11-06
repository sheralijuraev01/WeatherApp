package uz.sher.weather.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uz.sher.weather.R
import uz.sher.weather.adapters.CurrentValuesAdapter
import uz.sher.weather.adapters.DailyTempAdapter
import uz.sher.weather.databinding.ActivityMainBinding
import uz.sher.weather.models.CurrentValues
import uz.sher.weather.models.DailyTemperature
import uz.sher.weather.util.Constant
import java.sql.Date
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

@Suppress("DEPRECATION")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listCurrent: MutableList<CurrentValues> = ArrayList()
    private val listDaily: MutableList<DailyTemperature> = ArrayList()
    private lateinit var adapterCurrentValues: CurrentValuesAdapter
    private lateinit var adapterDaily: DailyTempAdapter
    private var errorConnect = false
    private var positionInt = -1
    private lateinit var tempMeasure: String
    private lateinit var windMeasure: String
    private lateinit var pressureMeasure: String
    private var tempIndex = "°C"
    private var windIndex = "m/s"
    private var pressureIndex = "mbar"
    private var icon by Delegates.notNull<Int>()
    private val stateNameList = arrayOf(
        "Tashkent",
        "Andijan",
        "Fergana",
        "Namangan",
        "Jizzakh",
        "Sirdarya",
        "Samarkand",
        "Bukhara",
        "Navoi",
        "Khorazm",
        "Karakalpakistan",
        "Kashkadarya",
        "Surkhandarya"
    )
    private val statelist = arrayOf(
        "Tashkent",
        "Andijan",
        "Fergana",
        "Namangan",
        "Jizzakh",
        "Sirdaryo",
        "Samarkand",
        "Bukhara",
        "Navoi",
        "Khiva",
        "Nukus",
        "Qashqadaryo",
        "Termiz"
    )
    var apiKey = "ecf3d5788b44cf922e7793a3058865c3"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("StatePosition", MODE_PRIVATE)
        positionInt = sharedPreferences.getInt("position", -1)

        if (!checkInternet()) {
            turnOffInternet()
        } else {
            turnOnInternet()
            if (positionInt != -1) {
                updateData()
            }


        }

        binding.setting.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            sharedPreferences.edit().putInt("position", positionInt).apply()
            finish()
        }
        binding.backButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, MenuActivity::class.java))
            finish()
        }
        binding.aqiLinkContainer.setOnClickListener {
            val urlAQI = "https://aqicn.org/city/uzbekistan/tashkent/us-embassy/"
            connectToWebSite(urlAQI)
        }
        binding.linkOpenMapWeather.setOnClickListener {
            val urlProvided = "https://openweathermap.org/"
            connectToWebSite(urlProvided)
        }
        binding.mainUpdateDatarefresh.setOnRefreshListener {
            if (checkInternet()) {
                turnOnInternet()
                binding.updatedInfo.text = "Updating..."
                updateData()
            } else {
                turnOffInternet()
            }
            binding.mainUpdateDatarefresh.isRefreshing = false
        }


    }

    private fun updateData() {
        listCurrent.clear()
        listDaily.clear()
        binding.tempurature.text = ""
        binding.tempDescp.text = ""
        binding.humidityItem.text = ""
        getMeasureItems()
        getCurrentWeather(positionInt)
        getDailyWeather(positionInt)


    }

    private fun getCurrentWeather(position: Int) {
        val city = statelist[position]
        binding.cityName.text = stateNameList[position]
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->

            try {

                // currentTemp
                val mainObj: JSONObject = response.getJSONObject("main")
                val currentTemp: Double = mainObj.getDouble("temp")
                var tempCelsius: Float = (currentTemp - 273.15).toFloat()
                if (tempMeasure == Constant.FAHRENHEIT) {
                    tempCelsius = celsiusToFahrenheit(tempCelsius)
                    tempIndex = "°F"
                }

                binding.measureTemp.text = tempIndex
                binding.tempurature.text = tempCelsius.toInt().toString()

                // temp description
                val weatherArray: JSONArray = response.getJSONArray("weather")
                val weatherObj = weatherArray.getJSONObject(0)
                val desp = weatherObj.getString("main")
                binding.tempDescp.text = desp

                // today min va max temprature

                var min_temp: Float = (mainObj.getDouble("temp_min") - 273.15).toFloat()
                var max_temp: Float = (mainObj.getDouble("temp_max") - 273.15).toFloat()

                if (tempMeasure == Constant.FAHRENHEIT) {
                    min_temp = celsiusToFahrenheit(min_temp)
                    max_temp = celsiusToFahrenheit(max_temp)
                }


                // wind speed
                val windObj: JSONObject = response.getJSONObject("wind")
                var windSpeed: Float = windObj.getDouble("speed").toFloat()
                if (windMeasure == Constant.KMS) {
                    windSpeed = msToKmH(windSpeed)
                    windIndex = "km/h"
                }


                //Today clouds %
                val clouddObj: JSONObject = response.getJSONObject("clouds")
                val clouds: String = clouddObj.getInt("all").toString()

                // today pressure mbar

                var currentPressure: Float = mainObj.getInt("pressure").toFloat()
                if (pressureMeasure == Constant.STANDARD) {
                    currentPressure = mBarToStandardBar(currentPressure)
                    pressureIndex = "atm"

                }
                // humidity index(namlik)
                val humidity: String = mainObj.getInt("humidity").toString()
                binding.humidityItem.text = humidity

                // sunrise and sunset

                val sunRiseSet: JSONObject = response.getJSONObject("sys")
                val sunRise: Long = sunRiseSet.getLong("sunrise")
                val sunSet: Long = sunRiseSet.getLong("sunset")
                val sunRiseTime = Date(sunRise * 1000)
                val timeFormat = SimpleDateFormat("hh:MM")
                val resSunRiseTime = timeFormat.format(sunRiseTime)
                val sunSetTime = Date(sunSet * 1000)
                val resSunSetTime = timeFormat.format(sunSetTime)

                listCurrent.add(CurrentValues("Sunrise", resSunRiseTime))
                listCurrent.add(CurrentValues("Sunset", resSunSetTime))
                listCurrent.add(CurrentValues("Min temp", "${min_temp.toInt()} $tempIndex"))
                listCurrent.add(CurrentValues("Max temp", "${max_temp.toInt()} $tempIndex"))
                listCurrent.add(
                    CurrentValues(
                        "Wind",
                        "${floatNearestTen(windSpeed)} $windIndex"
                    )
                )
                listCurrent.add(CurrentValues("Cloud", clouds + "%"))
                listCurrent.add(
                    CurrentValues(
                        "Pressure",
                        "${floatNearestTen(currentPressure)} $pressureIndex"
                    )
                )
                adapterCurrentValues = CurrentValuesAdapter(listCurrent, this@MainActivity)
                binding.currentDataView.isEnabled = false
                binding.currentDataView.adapter = adapterCurrentValues
                updatedConditionMessage()
            } catch (e: JSONException) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                errorConnect = true
            }
        },
            { error ->
                Toast.makeText(this, error.message.toString(), Toast.LENGTH_SHORT)
                    .show()
                errorConnect = true
            })
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonObjectRequest)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDailyWeather(position: Int) {
        val city = statelist[position]
        val urlDaily =
            "https://api.openweathermap.org/data/2.5/forecast?q=$city&cnt=24&appid=$apiKey"
        val jsonObjectRequestDaily =
            JsonObjectRequest(Request.Method.GET, urlDaily, null, { response ->
                try {
                    val listArr: JSONArray = response.getJSONArray("list")
                    for (i in 0 until listArr.length()) {
                        val listObj: JSONObject = listArr.getJSONObject(i)
                        val time: Long = listObj.getLong("dt")
                        var wind: Float =
                            (listObj.getJSONObject("wind").getString("speed")).toFloat()

                        if (windMeasure == Constant.KMS) wind = msToKmH(wind)

                        val tempDaily: Double = (listObj.getJSONObject("main").getDouble("temp"))
                        var resTemp: Float = (tempDaily - 273.15).toFloat()
                        if (tempMeasure == Constant.FAHRENHEIT) {
                            resTemp = celsiusToFahrenheit(resTemp)
                            tempIndex=" °F"
                        }


                        val weatherArr: JSONArray = listObj.getJSONArray("weather")
                        val id: Int = weatherArr.getJSONObject(0).getInt("id")
                        getWeatherIcon(id)
                        // get current date
                        if (i == 0) {
                            val timeDayMonth = SimpleDateFormat("dd-MMMM")
                            val dayMonth = timeDayMonth.format(time * 1000)
                            binding.todayDate.text = dayMonth.toString()
                        }

                        val timeFormat = SimpleDateFormat("HH:mm")

                        var timeRes = timeFormat.format(Date(time * 1000))
                        if (timeRes.equals("02:00")) {
                            val dayFormat = SimpleDateFormat("dd/MM")
                            timeRes = dayFormat.format(time * 1000)
                        }
                        listDaily.add(
                            DailyTemperature(
                                timeRes,
                                "${resTemp.toInt()}$tempIndex",
                                icon,
                                "${floatNearestTen(wind)}$windIndex"
                            )
                        )
                    }
                    adapterDaily = DailyTempAdapter(listDaily)
                    binding.dailyTempRcView.adapter = adapterDaily
                    val layoutManager =
                        StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL)
                    binding.dailyTempRcView.layoutManager = layoutManager

                } catch (e: JSONException) {
                    Toast.makeText(this@MainActivity, e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    errorConnect = true
                }
            },
                { error ->
                    Toast.makeText(this@MainActivity, error.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    errorConnect = true
                })

        val queueDaily = Volley.newRequestQueue(this@MainActivity)
        queueDaily.add(jsonObjectRequestDaily)
    }

    private fun checkInternet(): Boolean {
        val connected: Boolean
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connected =
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
        return connected
    }

    private fun turnOffInternet() {
        binding.noNetworkMessage.text = "Please connect to the internet"
        binding.mainFragment.visibility = View.INVISIBLE
        binding.internetFragment.visibility = View.VISIBLE

    }

    private fun turnOnInternet() {
        binding.mainFragment.visibility = View.VISIBLE
        binding.internetFragment.visibility = View.INVISIBLE
    }

    private fun getWeatherIcon(id: Int) {
        val arrayIcon = arrayOf(
            R.drawable.thunderstorm_200,
            R.drawable.drizzle_300,
            R.drawable.rain_500,
            R.drawable.snow_600,
            R.drawable.fog_700,
            R.drawable.sunny_800,
            R.drawable.clouds
        )
        if (id in 200..299) {
            icon = arrayIcon[0]
        } else if (id in 300..399) {
            icon = arrayIcon[1]
        } else if (id in 500..599) {
            icon = arrayIcon[2]
        } else if (id in 600..699) {
            icon = arrayIcon[3]
        } else if (id in 700..799) {
            icon = arrayIcon[4]
        } else if (id in 801..899) {
            icon = arrayIcon[6]
        } else {
            icon = arrayIcon[5]
        }
    }

    private fun connectToWebSite(url: String) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun updatedConditionMessage() {
        val countDownTimer = object : CountDownTimer(500, 1000) {
            override fun onTick(time: Long) {
                binding.updatedInfo.text = "Updating..."
            }

            override fun onFinish() {
                if (!errorConnect) {
                    binding.updatedInfo.text = "Successfully updated"
                }
            }

        }
        countDownTimer.start()

    }

    private fun getMeasureItems() {
        val measureSharedPr = getSharedPreferences("Measures", Context.MODE_PRIVATE)
        tempMeasure = measureSharedPr.getString("temperature_measure", Constant.CELSIUS).toString()
        windMeasure = measureSharedPr.getString("wind_measure", Constant.MS).toString()
        pressureMeasure =
            measureSharedPr.getString("atmospheric_measure", Constant.MILLIBAR).toString()
    }

    private fun fahrenheitToCelsius(item: Float): Float {
        return ((item - 32) * 5) / 9
    }

    private fun celsiusToFahrenheit(item: Float): Float {
        return ((item * 9) / 5) + 32
    }

    private fun msToKmH(item: Float): Float {
        return item * (3.6.toFloat())
    }

    private fun kmhToms(item: Float): Float {
        return item / (3.6.toFloat())
    }

    private fun mBarToStandardBar(item: Float): Float {
        return item / 1013.25.toFloat()
    }

    private fun standardBarTomBar(item: Float): Float {
        return item * 1013.25.toFloat()
    }

    private fun floatNearestTen(number: Float): Float {
        return ((number * 10).toInt()) / 10f
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@MainActivity, MenuActivity::class.java))
    }

}