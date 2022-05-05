package com.g30lab3.app.ui.timeSlotEdit

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.g30lab3.app.R


class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {


    lateinit var btnSelectDate:EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    /*
        super.onViewCreated(view, savedInstanceState)


        val DateChooser = view.findViewById<EditText>(R.id.edit_date_Field)
        DateChooser.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(childFragmentManager,null)
        }

         */

        super.onViewCreated(view, savedInstanceState)
        btnSelectDate = view.findViewById(R.id.edit_date)

        btnSelectDate.setOnClickListener(View.OnClickListener {
            val newFragment: DialogFragment = DatePickerFragment()
            fragmentManager?.let { it1 -> newFragment.show(it1, "DatePicker") }
        })



        btnSelectDate =  view.findViewById(R.id.edit_date)
        btnSelectDate.inputType = 0;

        view.apply {

            btnSelectDate.setOnClickListener {
                // create new instance of DatePickerFragment

                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                // we have to implement setFragmentResultListener
                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        btnSelectDate.setText(date)
                    }
                }
                // show
                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }
        }

        //TODO manage back button pression for saving edited timeslot



    }
}