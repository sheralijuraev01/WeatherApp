package uz.sher.weather.screens

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import uz.sher.weather.databinding.ActivitySettingBinding
import uz.sher.weather.util.Constant
import kotlin.properties.Delegates


class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var measureSharedPr: SharedPreferences
    private var tempPos by Delegates.notNull<Int>()
    private var windPos by Delegates.notNull<Int>()
    private var atmPos by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        measureSharedPr =
            getSharedPreferences("Measures", Context.MODE_PRIVATE)

        getPositions()

        binding.temperatureSpinner.setSelection(tempPos)
        binding.windSpinner.setSelection(windPos)
        binding.atmosphericSpinner.setSelection(atmPos)

        binding.backtosetting.setOnClickListener {
            startActivity(Intent(this@SettingActivity, MainActivity::class.java))
            finish()
        }

        binding.temperatureSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1)
                        saveSharedPreference("temperature_measure", Constant.FAHRENHEIT)
                    else saveSharedPreference("temperature_measure", Constant.CELSIUS)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }

        binding.windSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1)
                        saveSharedPreference("wind_measure", Constant.KMS)
                    else saveSharedPreference("wind_measure", Constant.MS)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }


        binding.atmosphericSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1)
                        saveSharedPreference("atmospheric_measure", Constant.STANDARD)
                    else saveSharedPreference("atmospheric_measure", Constant.MILLIBAR)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }

    }

    private fun saveSharedPreference(key: String, item: String) {
        val measureSharedEdit = measureSharedPr.edit()
        measureSharedEdit.putString(
            key,
            item
        )
        measureSharedEdit.apply()
    }

    private fun getPositions() {
        val tempSelected = measureSharedPr.getString("temperature_measure", Constant.CELSIUS)
        val windSelected = measureSharedPr.getString("wind_measure", Constant.MS)
        val atmSelected = measureSharedPr.getString("atmospheric_measure", Constant.MILLIBAR)

        if (tempSelected == Constant.FAHRENHEIT) tempPos =
            1 else if (tempSelected == Constant.CELSIUS) tempPos = 0
        if (windSelected == Constant.KMS) windPos =
            1 else if (windSelected == Constant.MS) windPos = 0
        if (atmSelected == Constant.STANDARD) atmPos =
            1 else if (atmSelected == Constant.MILLIBAR) atmPos = 0
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SettingActivity, MainActivity::class.java))
    }
}