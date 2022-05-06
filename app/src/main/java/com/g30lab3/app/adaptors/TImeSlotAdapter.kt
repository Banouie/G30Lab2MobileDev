package com.g30lab3.app.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.models.timeSlot

//List
class TimeSlotAdapter(val data: List<timeSlot>) :
    RecyclerView.Adapter<TimeSlotAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val title: TextView = v.findViewById(R.id.time_slot_title)
        val date: TextView = v.findViewById(R.id.time_slot_date)
        val time: TextView=v.findViewById(R.id.time_slot_time)
        val location: TextView = v.findViewById(R.id.time_slot_location)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl = LayoutInflater.from(parent.context).inflate(R.layout.time_slot_layout, parent, false);
        return ItemViewHolder(vl);
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = data[position]
        holder.title.text=item.title
        holder.date.text=item.date
        holder.time.text= item.time
        holder.location.text = item.location
    }

    override fun getItemCount(): Int = data.size

}