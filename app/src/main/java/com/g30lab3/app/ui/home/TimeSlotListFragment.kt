package com.g30lab3.app.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.g30lab3.app.R
import com.g30lab3.app.adaptors.TimeSlotAdapter
import com.g30lab3.app.TimeSlotVM
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {

    val vm by viewModels<TimeSlotVM>()
    var user = Firebase.auth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var emptyMessage: TextView = view.findViewById(R.id.empty_message)

        val recyclerView: RecyclerView = view.findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        vm.all.observe(requireActivity()) {
            var list = it
            if(!arguments?.isEmpty!!){
                var selected = arguments?.get("selected_skill") as String
                list = it.filter { t -> t.skill==selected }
            }
            // Data bind the recycler view
            recyclerView.adapter = TimeSlotAdapter(list)

            //if the list of timeSlot is empty a message is shown, shouldn't appear otherwise
            if (list.isEmpty()) {
                emptyMessage.visibility = View.VISIBLE
            } else {
                emptyMessage.visibility = View.GONE
            }
        }

        var addButton: FloatingActionButton = view.findViewById(R.id.floating_add_button)
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_timeSlotEditFragment)
        }


    }
}
