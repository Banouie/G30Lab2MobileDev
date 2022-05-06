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

        val title: TextView = v.findViewById(R.id.item_slot_title)
        val description: TextView = v.findViewById(R.id.item_slot_description)
        val location: TextView = v.findViewById(R.id.item_slot_location)
//Or


        /*
        fun bind(item: timeSlot, action: (v: View) -> Unit) {
            title.text = item.title
            description.text = item.description
            location.text = item.location

            itemView.setOnClickListener(
                action
            )
        }

        fun unbind() {
//            delete.setonclickListenner(null)
        }
         */
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false);
        return ItemViewHolder(vl);
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
////
//        holder.bind(data[position]){
//            data.removeAt(position)
//            notifyItemRemoved(position)
//        }


        /*
        holder.bind(item) {
//            val pos=data.indexOf(item);
//            if (pos!=-1)
//            { data.remove(data[pos])
//            notifyItemRemoved(pos)}
        }
         */

        val item = data[position]
        holder.title.text=item.title
        holder.description.text=item.description
    }

    override fun getItemCount(): Int = data.size

}