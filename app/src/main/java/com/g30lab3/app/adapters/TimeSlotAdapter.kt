package com.g30lab3.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.UserVM
import com.g30lab3.app.models.timeSlot
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

//List
class TimeSlotAdapter(val data: MutableList<timeSlot>) :
    RecyclerView.Adapter<TimeSlotAdapter.ItemViewHolder>() {

    val initialTimeSlotList =
        mutableListOf<timeSlot>().apply { addAll(data) } //copy of the timeSlot list for filtering after search

    class ItemViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val title: TextView = v.findViewById(R.id.time_slot_title)
        val date: TextView = v.findViewById(R.id.time_slot_date)
        val time: TextView = v.findViewById(R.id.time_slot_time)
        val location: TextView = v.findViewById(R.id.time_slot_location)
        val star : MaterialButton = v.findViewById(R.id.star_time_slot)

        val editButton: Button = v.findViewById(R.id.button_edit_time_slot)
        val view: View = v
        val card: MaterialCardView = v.findViewById(R.id.card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val vl =
            LayoutInflater.from(parent.context).inflate(R.layout.time_slot_layout, parent, false);
        return ItemViewHolder(vl);
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        //get the current time slot
        val item = data[position]

        //get info about the current logged in user
        var currentUser = Firebase.auth.currentUser
        //if the current user is not the owner of a timeSlot he can't edit it
        if (currentUser?.uid != item.author) holder.editButton.visibility = View.GONE


        //set the holder fields correctly using the info of the current time slot
        holder.title.text = item.title
        holder.date.text = item.date
        holder.time.text = item.time
        holder.location.text = item.location
        //TODO set the star, must be shown only if the current user is != from author user
        //This is a bundle containing the ID of the current Time SLot, useful in eventual navigation (see below)
        var bundle = bundleOf("time_slot_ID" to item.id)

        holder.editButton.setOnClickListener {
            //pass the ID of the selected time slot to the timeSlotEdit fragment in order to allow it to show the details of the correct Time Slot
            holder.view.findNavController()
                .navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
        }

        holder.star.setOnClickListener {
            //change the icon
            //todo change icon color considering the state and add or remove this timeslot to the "liked one" of the current user
            holder.star.setIconResource(R.drawable.ic_star_yes)
        }

        holder.card.setOnClickListener {
            //if the user clicks on the time slot a navigation to the fragment details of the time slot has to happen
            holder.view.findNavController()
                .navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment, bundle)
        }

    }

    override fun getItemCount(): Int = data.size

    fun getFilter(): Filter {
        return timeSlotFilter
    }

    private val timeSlotFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: MutableList<timeSlot> = mutableListOf()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(initialTimeSlotList)
            } else {
                val query = constraint.toString().trim().lowercase()
                initialTimeSlotList.forEach {
                    if (it.title.lowercase().contains(query)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values is MutableList<*>) {
                data.clear()
                data.addAll(results.values as MutableList<timeSlot>)
                notifyDataSetChanged()
            }
        }
    }





}