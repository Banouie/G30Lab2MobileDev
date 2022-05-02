package com.g30lab3.app.ui.TimeSlotEdit


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.g30lab3.app.R
import com.google.android.material.datepicker.MaterialDatePicker

class EditProfileFragment : Fragment(R.layout.fragment_time_slot_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val DateChooser = view.findViewById<EditText>(R.id.edit_date_Field)
        DateChooser.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(childFragmentManager,null)
        }



    }
}