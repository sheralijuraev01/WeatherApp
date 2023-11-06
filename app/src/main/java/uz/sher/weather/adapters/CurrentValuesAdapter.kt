package uz.sher.weather.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import uz.sher.weather.models.CurrentValues
import uz.sher.weather.R

class CurrentValuesAdapter (private val list: MutableList<CurrentValues>,private val context: Context):BaseAdapter() {
    private lateinit var title:TextView
    private lateinit var descp:TextView
    private var layoutinflater:LayoutInflater?=null
    override fun getCount(): Int {
       return list.size
    }

    override fun getItem(position: Int): Any? {
     return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
         var view=convertView
        if(layoutinflater==null){
            layoutinflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if(view==null){
            view=layoutinflater!!.inflate(R.layout.current_day_values_layout,null)
        }

        title=view!!.findViewById<TextView>(R.id.current_values_title)
        descp=view.findViewById<TextView>(R.id.current_values_desc)

        title.text=list[position].title
        descp.text=list[position].item
        return view
    }
}