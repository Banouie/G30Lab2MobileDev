package com.g30lab3.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {

    val vm by viewModels<TimeSlotVM>()
    var user = Firebase.auth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var order = false
        var orderby = ""
        var emptyMessage: TextView = view.findViewById(R.id.empty_message)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //[Start] Manage the order by field
        var orderSelector: TextInputLayout = view.findViewById(R.id.order_field)
        val orderArguments = listOf("Title(A-Z)", "Date", "Do not order")
        val adapter = ArrayAdapter(requireContext(), R.layout.skill_dropdown_item, orderArguments)
        (orderSelector.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        // when the selection of the "order by" field change, this function will correctly set the values of "order" and "orderby" to call the "getFromSkill" function in order to
        // update/refresh the list of timeSlot and have it ordered
        orderSelector.editText?.doOnTextChanged { text, _, _, _ ->
            when (text.toString()) {
                "Do not order" -> {
                    order = false
                }
                "Time" -> {
                    order = true
                    orderby = "date"
                }
                "Title(A-Z)" -> {
                    order = true
                    orderby = "title"
                }
            }
            vm.getFromSkill(arguments?.get("skill") as String, order, orderby)
        }
        //[End]

        //[Start] RecyclerView
        vm.getFromSkill(arguments?.get("skill") as String, order, orderby)//initialization of the list to show it in RecyclerView

        vm.filtered.observe(requireActivity()) {
            // Data bind the recycler view
            recyclerView.adapter = TimeSlotAdapter(it)
            //if the list of timeSlot is empty a message is shown, shouldn't appear otherwise
            if (it.isEmpty()) {
                emptyMessage.visibility = View.VISIBLE
            } else {
                emptyMessage.visibility = View.GONE
            }
        }

        // Manage floating action button to create a new timeSlot
        var addButton: FloatingActionButton = view.findViewById(R.id.floating_add_button)
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
        }

        //Handle back button pressed, go to Home Screen
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_timeSlotListFragment_to_skillsListFragment)
                }
            })


    }
}
