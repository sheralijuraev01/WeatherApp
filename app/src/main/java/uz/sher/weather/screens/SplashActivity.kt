package uz.sher.weather.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import uz.sher.weather.R
import uz.sher.weather.adapters.SplashBannerAdapter
import uz.sher.weather.databinding.ActivitySplashBinding
import uz.sher.weather.models.SplashBanner
import java.text.SimpleDateFormat
import java.util.*


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var bannerAdapter: SplashBannerAdapter
    private var currentIndex = 0;
    private val list: MutableList<SplashBanner> = ArrayList()
    private lateinit var sharedPr: SharedPreferences

    private var isLoggin = false
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPr = getSharedPreferences("isLoggin", Context.MODE_PRIVATE)
        isLoggin = sharedPr.getBoolean("isLog", false)



            object : CountDownTimer(1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.splashFragment.visibility = View.VISIBLE
                    binding.viewPagerSplash.visibility = View.INVISIBLE
                }

                override fun onFinish() {
                    if (isLoggin) {
                        startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
                    finish()
                    } else {
                        binding.splashFragment.visibility = View.INVISIBLE
                        initList()
                        binding.viewPagerSplash.visibility = View.VISIBLE
                        bannerAdapter = SplashBannerAdapter(list)
                        binding.splashBanner.adapter = bannerAdapter
                        setUpIndicators()
                        setActivityIndicators(0)
                    }
                }

            }.start()






        binding.startBtn.setOnClickListener {
            val shp = getSharedPreferences("isLoggin", Context.MODE_PRIVATE)
            val eshp: SharedPreferences.Editor = shp.edit()
            eshp.putBoolean("isLog", true)
            eshp.apply()
            startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
            finish()
        }




        binding.idNext.setOnClickListener {
            setActivityIndicators(++currentIndex)
            binding.splashBanner.currentItem = currentIndex
        }
        binding.idSkip.setOnClickListener {

            setActivityIndicators(list.size - 1)
            binding.splashBanner.currentItem = (list.size - 1)
        }

        binding.splashBanner.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setActivityIndicators(position)
                currentIndex = position
                if (position == list.size - 1) {
                    binding.startBtn.visibility = View.VISIBLE
                    binding.idSkip.visibility = View.INVISIBLE
                    binding.idNext.visibility = View.INVISIBLE
                } else {
                    binding.startBtn.visibility = View.INVISIBLE
                    binding.idSkip.visibility = View.VISIBLE
                    binding.idNext.visibility = View.VISIBLE
                }

            }
        })

    }


    private fun initList() {
        list.add(
            SplashBanner(
                R.drawable.banner_1,
                "For the plan",
                "You can determine the weather for the next few days through the application"
            )
        )
        list.add(
            SplashBanner(
                R.drawable.banner_2,
                "Quality",
                "All information is highly accurate and predictive"
            )
        )
        list.add(
            SplashBanner(
                R.drawable.banner_3,
                "Reliability",
                "Convenient interface and safe program "
            )
        )

    }

    private fun setActivityIndicators(position: Int) {
        val count = binding.bannerIndicators.childCount

        for (i in 0 until count) {
            val indicator = binding.bannerIndicators.getChildAt(i) as ImageView
            if (i == position) {
                indicator.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@SplashActivity,
                        R.drawable.banner_enable_icon
                    )
                )
            } else {
                indicator.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@SplashActivity,
                        R.drawable.banner_disable_icon
                    )
                )
            }


        }
    }

    private fun setUpIndicators() {
        val indicators = arrayOfNulls<ImageView>(bannerAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this@SplashActivity)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.root.context,
                    R.drawable.banner_disable_icon
                )
            )
            indicators[i]?.layoutParams = layoutParams
            binding.bannerIndicators.addView(indicators[i])
        }
    }
}
