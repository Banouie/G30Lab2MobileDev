package com.g30lab3.app.ui.timeSlotDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.activity.addCallback
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.g30lab3.app.R
import com.g30lab3.app.models.timeSlot
import com.g30lab3.app.timeSlotVM


class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {

    val vm by viewModels<timeSlotVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var title: TextView = view.findViewById(R.id.adv_title)
        var description: TextView = view.findViewById(R.id.adv_description)
        var dateTime: TextView = view.findViewById(R.id.adv_date_time)
        var location: TextView = view.findViewById(R.id.adv_location)
        var duration: TextView = view.findViewById(R.id.adv_duration)


        vm.all.observe(requireActivity()) {
            //get from the list of timeSLot the correct one selected in the home fragment from the user
            var result: List<timeSlot> =
                it.filter { timeSlot -> timeSlot.id == arguments?.get("time_slot_ID") }
            var to_show_timeSlot: timeSlot = result[0]
            title.text = to_show_timeSlot.title
            //HtmlCompat.fromHtml just format the string in order to obtain the names of the fields of the time slot in bold
            description.text = HtmlCompat.fromHtml("<b>Description</b>: "+ to_show_timeSlot.description,HtmlCompat.FROM_HTML_MODE_LEGACY)
            location.text = HtmlCompat.fromHtml("<b>Location</b>: " + to_show_timeSlot.location,HtmlCompat.FROM_HTML_MODE_LEGACY)
            duration.text = HtmlCompat.fromHtml("<b>Duration</b> (in number of time slots): " + to_show_timeSlot.duration.toString(),HtmlCompat.FROM_HTML_MODE_LEGACY)
            dateTime.text=HtmlCompat.fromHtml("<b>Date</b>: " + to_show_timeSlot.date + " - <b>Starting time</b>: " + to_show_timeSlot.time,HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(R.id.action_nav_timeSlotDetailsFragment_to_nav_home)
        }


    }
}