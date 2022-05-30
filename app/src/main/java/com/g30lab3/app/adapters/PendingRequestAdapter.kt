package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.models.PendingRequestInfo
import com.g30lab3.app.models.timeSlot
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

internal enum class REQUEST_TYPE {
    SENT_PR_VIEW, INCOME_PR_VIEW
}

class PendingRequestAdapter(val timeSlots: List<timeSlot>,val requests: List <PendingRequestInfo>) :
    RecyclerView.Adapter<PendingRequestAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val view = v
    }

    //inflate different layout for sent or incoming pending request
    override fun getItemViewType(position: Int): Int {
        val item = timeSlots[position]
        return if (item.author == Firebase.auth.currentUser?.uid) {
            REQUEST_TYPE.INCOME_PR_VIEW.ordinal
        } else REQUEST_TYPE.SENT_PR_VIEW.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ItemViewHolder {
        return if (viewType == REQUEST_TYPE.SENT_PR_VIEW.ordinal){
            //this timeslot has been requested from the logged user
            val vl = LayoutInflater.from(parent.context).inflate(R.layout.sent_pending_request_layout,parent,false)
            PendingRequestAdapter.ItemViewHolder(vl)
        }else{
            //the author of this timeslot is the logged user, is a income request for his timeSlot
            //TODO("change the inflated layout here into income_pending_request_layout")
            val vl = LayoutInflater.from(parent.context).inflate(R.layout.sent_pending_request_layout,parent,false)
            PendingRequestAdapter.ItemViewHolder(vl)
        }
    }

    override fun onBindViewHolder(holder: PendingRequestAdapter.ItemViewHolder, position: Int) {
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

        // the only difference is that in an income request layout the logged user (which is the author of this timeslot)
        // will see the name of the requesting user (we can have different pending request for the same timeSlot from different users)
        //TODO("set the from field and the incipit properly in income/sent request")



    }

    override fun getItemCount(): Int {
        return requests.size
    }
}