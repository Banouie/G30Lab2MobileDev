package com.g30lab3.app.adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.timeSlotVM
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//List
class TimeSlotAdapter(val data: List<timeSlot>) :
    RecyclerView.Adapter<TimeSlotAdapter.ItemViewHolder>() {

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val title: TextView = v.findViewById(R.id.time_slot_title)
        val date: TextView = v.findViewById(R.id.time_slot_date)
        val time: TextView=v.findViewById(R.id.time_slot_time)
        val location: TextView = v.findViewById(R.id.time_slot_location)

        val editButton:Button = v.findViewById(R.id.button_edit_time_slot)
        val view: View = v
        val card: MaterialCardView = v.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl = LayoutInflater.from(parent.context).inflate(R.layout.time_slot_layout, parent, false);
        return ItemViewHolder(vl);
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        //get the current time slot
        val item = data[position]

        //get info about the current logged in user
        var currentUser = Firebase.auth.currentUser
        //if the current user is not the owner of a timeSlot he can't edit it
        if(currentUser?.uid != item.author) holder.editButton.visibility = View.GONE


        //set the holder fields correctly using the info of the current time slot
        holder.title.text=item.title
        holder.date.text=item.date
        holder.time.text= item.time
        holder.location.text = item.location
        //This is a bundle containing the ID of the current Time SLot, useful in eventual navigations (see below)
        var bundle = bundleOf("time_slot_ID" to item.id)

        holder.editButton.setOnClickListener {
            //pass the ID of the selected time slot to the timeSlotEdit fragment in order to allow it to show the details of the correct Time Slot
            holder.view.findNavController().navigate(R.id.action_nav_home_to_nav_timeSlotEditFragment,bundle)
        }

        holder.card.setOnClickListener {
            //if the user clicks on the time slot a navigation to the fragment details of the time slot has to happen
            holder.view.findNavController().navigate(R.id.action_nav_home_to_nav_timeSlotDetailsFragment,bundle)
        }




    }

    override fun getItemCount(): Int = data.size

}