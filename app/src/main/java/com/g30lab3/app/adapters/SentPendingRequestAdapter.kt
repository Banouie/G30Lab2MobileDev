package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.models.timeSlot


class SentPendingRequestAdapter(val timeSlots: List<timeSlot>, val requests: List <PendingRequestInfo>) :
    RecyclerView.Adapter<SentPendingRequestAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ItemViewHolder {
            val vl = LayoutInflater.from(parent.context).inflate(R.layout.sent_pending_request_layout,parent,false)
            return  SentPendingRequestAdapter.ItemViewHolder(vl)
    }

    override fun onBindViewHolder(holder: SentPendingRequestAdapter.ItemViewHolder, position: Int) {

        val item = requests[position]
        val leadingTimeSlot = timeSlots.firstOrNull { t -> t.id == item.leadingTimeSlot } //get the timeslot of the request

        //title,ecc are the same in both layout
        val title: TextView = holder.view.findViewById(R.id.request_title)
        title.text = leadingTimeSlot?.title
        val date: TextView = holder.view.findViewById(R.id.request_date)
        date.text = leadingTimeSlot?.date
        val time: TextView = holder.view.findViewById(R.id.request_time)
        time.text = leadingTimeSlot?.title
        val location: TextView = holder.view.findViewById(R.id.request_location)
        location.text = leadingTimeSlot?.location

    }

    override fun getItemCount(): Int {
        return requests.size
    }
}