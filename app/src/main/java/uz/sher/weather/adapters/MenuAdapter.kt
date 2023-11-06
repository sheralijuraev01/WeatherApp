package uz.sher.weather.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.sher.weather.models.MenuState
import uz.sher.weather.databinding.MenuRowBinding

class MenuAdapter(private val list: MutableList<MenuState>):RecyclerView.Adapter<MenuAdapter.ViewHolder> (){
    private var menuListiner:OnMenuClickedListener?=null
    inner class ViewHolder(val binding: MenuRowBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(menu: MenuState){
            binding.iconDespMenu.setImageResource(menu.image_desp)
            binding.stateNameMenu.text=menu.stateName
            binding.cityNameMenu.text=menu.cityName
            binding.root.setOnClickListener {
                menuListiner?.setOnMenuListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view=MenuRowBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
   holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
       return list.size
    }

    fun setOnMenuClickedListener(listener: OnMenuClickedListener){
        menuListiner=listener
    }

    interface OnMenuClickedListener{
        fun setOnMenuListener(position: Int)
    }
}