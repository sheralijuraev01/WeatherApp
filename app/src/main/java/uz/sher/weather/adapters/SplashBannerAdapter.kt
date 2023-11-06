package uz.sher.weather.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.sher.weather.models.SplashBanner
import uz.sher.weather.databinding.SplashBannerBinding

class SplashBannerAdapter(private val list: MutableList<SplashBanner>) :
    RecyclerView.Adapter<SplashBannerAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: SplashBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(splashBanner: SplashBanner) {
            binding.splashImage.setImageResource(splashBanner.image)
            binding.splashBannerTitle.text = splashBanner.title
            binding.splashBannerDesp.text = splashBanner.desp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SplashBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}