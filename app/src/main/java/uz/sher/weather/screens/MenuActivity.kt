package uz.sher.weather.screens

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import uz.sher.weather.R
import uz.sher.weather.adapters.MenuAdapter
import uz.sher.weather.databinding.ActivityMenuBinding
import uz.sher.weather.models.MenuState

class MenuActivity : AppCompatActivity(), MenuAdapter.OnMenuClickedListener {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var menuAdapter: MenuAdapter
    private val list: MutableList<MenuState> = ArrayList()
    private var appUpdateManager: AppUpdateManager? = null

    private val REQUEST_CODE = 123

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        for (i in stateNameList.indices) {
            list.add(MenuState(R.drawable.weather_location, stateNameList[i], cityName[i]))
        }


        checkForUpdates()
        showFeedbackDialog()
        appUpdateManager!!.registerListener(
            installStateUpdatedListener
        )

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

    private var installStateUpdatedListener =
        InstallStateUpdatedListener { installState: InstallState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                Toast.makeText(
                    this,
                    "Downloaded, Restart the app in 5 seconds",
                    Toast.LENGTH_SHORT
                ).show()
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ appUpdateManager!!.completeUpdate() }, 5000)
//            showCompletedUpdate();
            }
        }

    private fun showFeedbackDialog() {
        val reviewManager = ReviewManagerFactory.create(this)
        reviewManager.requestReviewFlow().addOnCompleteListener { it: Task<ReviewInfo?> ->
            if (it.isSuccessful) {
                reviewManager.launchReviewFlow(this, it.result!!)
            }
        }
    }

    private fun checkForUpdates() {
        appUpdateManager!!.appUpdateInfo.addOnSuccessListener { info: AppUpdateInfo ->
            val isUpdateAvailable =
                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            if (isUpdateAvailable && isUpdateAllowed) {
                try {
                    appUpdateManager!!.startUpdateFlowForResult(
                        info,
                        AppUpdateType.FLEXIBLE,
                        this,
                        REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Nimadir xato ketdi", Toast.LENGTH_SHORT).show()
                Log.e("Error", "Nimadir xato ketdi")
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager!!.unregisterListener(installStateUpdatedListener)
    }
}