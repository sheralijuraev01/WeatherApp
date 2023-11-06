package uz.sher.weather.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.sher.weather.models.DailyTemperature
import uz.sher.weather.databinding.DailyTempRowBinding


class DailyTempAdapter(private val list:MutableList<DailyTemperature>):RecyclerView.Adapter<DailyTempAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding: DailyTempRowBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(dailyTempurature: DailyTemperature){
            binding.dailyTime.text=dailyTempurature.time
            binding.dailyTemp.text=dailyTempurature.temp
            binding.dailyImage.setImageResource(dailyTempurature.icon)
            binding.dailyWind.text=dailyTempurature.wind

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view=DailyTempRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}