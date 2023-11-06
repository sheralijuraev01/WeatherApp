package uz.sher.weather.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.sher.weather.adapters.MenuAdapter
import uz.sher.weather.models.MenuState
import uz.sher.weather.R
import uz.sher.weather.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity(), MenuAdapter.OnMenuClickedListener {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var menuAdapter: MenuAdapter
    private val list: MutableList<MenuState> = ArrayList()


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
    private val cityName = arrayOf(
        "Tashkent",
        "Andijan",
        "Fergana",
        "Namangan",
        "Jizzakh",
        "Sirdarya",
        "Samarkand",
        "Bukhara",
        "Navoi",
        "Khiva",
        "Nukus",
        "Karshi",
        "Termiz"
    )

    var apiKey = "ecf3d5788b44cf922e7793a3058865c3"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        for (i in stateNameList.indices) {
            list.add(MenuState(R.drawable.weather_location, stateNameList[i], cityName[i]))
        }

        menuAdapter = MenuAdapter(list)
        menuAdapter.setOnMenuClickedListener(this)
        binding.rcMenu.adapter = menuAdapter

    }


    override fun setOnMenuListener(position: Int) {
//        Toast.makeText(this,position.toString(),Toast.LENGTH_SHORT).show()
        val intent = Intent(this@MenuActivity, MainActivity::class.java)
        val sharedPreferences = getSharedPreferences("StatePosition", Context.MODE_PRIVATE)
        val shareEdit = sharedPreferences.edit()
        shareEdit.putInt("position", position)
        shareEdit.apply()
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}